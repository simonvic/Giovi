package it.simonvic.giovi.delegates;

import java.io.IOException;
import java.net.URI;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import giovi.GioviParserVisitResult;
import giovi.Module.FileModule;
import giovi.Module.InnerModule;
import giovi.Module.NativeModule;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.GioviVisitorDelegate;
import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.util.TextProcessor;

public class ImportStatementDelegate extends GioviVisitorDelegate {

	public ImportStatementDelegate(GioviVisitor gioviVisitor) {
		super(gioviVisitor);
	}

	@Override
	public GioviParserVisitResult visitImportStatement(ImportStatementContext ctx) {
		for (var importEntryCtx : ctx.importEntry()) {
			visit(importEntryCtx);
		}
		return null;
	}

	private URI createURIFrom(ImportURIContext ctx) {
		return switch (ctx) {
			case ImportURIIdentifierContext c -> URI.create(c.getText());
			case ImportURIExactTextContext c -> URI.create(
				TextProcessor.of(c.getText())
					.trimDelimiters(1)
					.unescapeEscapes()
					.collectString()
			);
			case ImportURITextContext c -> URI.create(
				TextProcessor.of(c.getText())
					.trimDelimiters(1)
					.unescapeEscapes()
					.injectVariables(getCurrentScope())
					.injectExpressions(gioviVisitor)
					.collectString()
			);
			default -> throw new RuntimeException("Unhandled ImportURIContext");
		};
	}

	@Override
	public GioviParserVisitResult visitImportEntry(ImportEntryContext ctx) {
		giovi.Module module = getModule();
		for (var uriCtx : ctx.importURI()) {
			var uri = createURIFrom(uriCtx);
			module = switch (uri.getScheme()) {
				// case "http", "https" -> new RemoteModule(uri, ROOT_MODULE);
				case "file" -> {
					// file import are only available as top level
					if (module != getModule()) {
						throw new GioviException("Can't import nested file module");
					}
					yield new FileModule(uri);
				}
				case null -> {
					// if parent module is a file module, visit level module declarations
					if (module instanceof FileModule m && m != getModule()) {
						try {
							var lexer = new GioviLexer(CharStreams.fromFileName(m.getPath()));
							var parser = new GioviParser(new CommonTokenStream(lexer));
							parser.begin().program().statement().stream()
								.filter(statement -> statement.moduleDeclaration() != null)
								.map(statement -> statement.moduleDeclaration())
								.map(
									innerModuleDeclarationCtx -> new InnerModule(
										m, innerModuleDeclarationCtx.identifier().getText(),
										innerModuleDeclarationCtx.body
									)
								)
								.forEach(innerModule -> {
									m.declare(innerModule);
									GioviVisitor.scanInnerModules(innerModule, true);
								});
						} catch (IOException e) {
							throw new GioviException(e);
						}
					}
					yield module.getModule(uri.toString())
						.orElseThrow(
							() -> new GioviException("Can't find module " + uri.toString())
						);
				}
				default -> throw new GioviException(
					"Unsupported module URI scheme: " + uri.getScheme()
				);
			};
		}
		switch (module) {
			case NativeModule m -> doImport(m);
			case InnerModule m -> doImport(m);
			case FileModule m -> doImport(m);
		}
		if (ctx.alias != null) {
			getCurrentScope().declare(ctx.alias.getText(), module);
		}
		return null;
	}

	private void doImport(NativeModule module) {
		// TODO: import if autoimport = false
	}

	private void doImport(InnerModule module) {
		var visitor = new GioviVisitor(module);
		visitor.visit(module.bodyCtx());
	}

	private void doImport(FileModule module) {
		try {
			var lexer = new GioviLexer(CharStreams.fromFileName(module.getPath()));
			var parser = new GioviParser(new CommonTokenStream(lexer));
			var tree = parser.begin();
			var visitor = new GioviVisitor(module);
			visitor.visit(tree);
		} catch (IOException e) {
			throw new GioviException(e);
		}
	}

}
