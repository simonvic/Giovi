package it.simonvic.giovi.lsp;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.antlr.GioviParser.*;

public class GioviTextDocumentService implements TextDocumentService {

	private static final Logger log = Logger.getLogger("TextDocumentService");

	public GioviTextDocumentService() {
		try {
			var fileHandler = new FileHandler("/tmp/giovils/textdocument.log", true);
			fileHandler.setFormatter(new SimpleFormatter());
			log.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		log.info("init text document service");
	}

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		log.info("client opened " + params.getTextDocument().getUri());
	}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		log.info("client changed " + params.getTextDocument().getUri());
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
		log.info("client closed " + params.getTextDocument().getUri());
	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		log.info("client saved " + params.getTextDocument().getUri());
	}

	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
		CompletionParams completionParams
	) {
		log.info("\tcontext: " + completionParams.getContext());
		return CompletableFutures.computeAsync(_ -> {
			var completionItems = new ArrayList<CompletionItem>();
			completionItems.addAll(Snippets.snippets);
			completionItems.add(Snippets.snippet(
				"foo",
				"debug snippet",
				"""
				for ${1:i} in ${2:0}..${3:n} do {
					$0
				}
				"""));
			return Either.forLeft(completionItems);
		});
	}

	@Override
	public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(
		CodeActionParams params
	) {
		log.info("client triggered codeActions");
		return CompletableFutures.computeAsync(_ -> {
			return List.of(
				Either.forRight(new CodeAction("Flip redirection")),
				Either.forRight(new CodeAction("Fix imports")),
				Either.forRight(new CodeAction("Extract")));
		});
	}

	@Override
	public CompletableFuture<Hover> hover(HoverParams params) {
		log.info("client triggered hover");
		return CompletableFutures.computeAsync(_ -> {
			return new Hover(List.of(
				Either.forLeft("""
					hello world from
					real server 
					"""),
				Either.forLeft("""
					This is some documentation
					""")));

		});
	}

	@Override
	public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
		System.out.println(params);
		return CompletableFutures.computeAsync(_ -> {
			var edits = new ArrayList<TextEdit>();
			return edits;
		});
	}

	@Override
	public CompletableFuture<List<InlayHint>> inlayHint(InlayHintParams params) {
		return CompletableFutures.computeAsync(_ -> {
			return List.of(
				new InlayHint(new Position(0, 0), Either.forLeft("This is a sample inlay hint")));
		});
	}

	@Override
	public CompletableFuture<SignatureHelp> signatureHelp(SignatureHelpParams params) {
		return CompletableFutures.computeAsync(_ -> {
			return new SignatureHelp(List.of(new SignatureInformation("signature help")), 0, 0);
		});
	}

	private static GioviParser parserFrom(TextDocumentIdentifier textDocument)
		throws IOException {
		var lexer = new GioviLexer(
			CharStreams.fromFileName(
				URI.create(textDocument.getUri()).getPath()));
		return new GioviParser(new CommonTokenStream(lexer));
	}

	@Override
	public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(
		DocumentSymbolParams params
	) {
		return CompletableFutures.computeAsync(_ -> {
			GioviParser parser;
			var listener = new SymbolsListener();
			try {
				parser = parserFrom(params.getTextDocument());
				parser.addParseListener(listener);
			} catch (IOException e) {
				log.info(e.toString());
				return List.of();
			}
			parser.begin();
			return listener.getSymbols().stream()
				.<Either<SymbolInformation, DocumentSymbol>>map(Either::forRight)
				.toList();
		});
	}

	private static List<DocumentSymbol> findSymbols(
		ParseTree ruleCtx
	) {
		List<DocumentSymbol> symbols = new ArrayList<>();
		for (int i = 0; i < ruleCtx.getChildCount(); i++) {
			var childCtx = ruleCtx.getChild(i);
			switch (childCtx) {
				case ModuleDeclarationContext c -> symbols.add(new DocumentSymbol(
					c.identifier().getText(),
					SymbolKind.Module,
					rangeFrom(c),
					rangeFrom(c.identifier()),
					null,
					findSymbols(c)));
				case StructDefinitionContext c -> symbols.add(new DocumentSymbol(
					c.typename.getText(),
					SymbolKind.Struct,
					rangeFrom(c),
					rangeFrom(c.typename),
					null,
					findSymbols(c.body)));
				case FunctionDeclarationContext c -> symbols.add(new DocumentSymbol(
					c.functionSignature().identifier().getText(),
					SymbolKind.Function,
					rangeFrom(c),
					rangeFrom(c.functionSignature().identifier()),
					c.functionSignature().getText(),
					findSymbols(c.body)));
				case SelfFunctionDeclarationContext c -> symbols.add(new DocumentSymbol(
					c.selfFunctionSignature().identifier().getText(),
					SymbolKind.Function,
					rangeFrom(c),
					rangeFrom(c.selfFunctionSignature().identifier()),
					c.selfFunctionSignature().getText(),
					findSymbols(c.body)));
				case ConstantDeclarationContext c -> symbols.add(new DocumentSymbol(
					c.identifier().getText(),
					SymbolKind.Constant,
					rangeFrom(c),
					rangeFrom(c.identifier())));
				case VarDeclarationContext c -> symbols.add(new DocumentSymbol(
					c.identifier().getText(),
					SymbolKind.Variable,
					rangeFrom(c),
					rangeFrom(c.identifier())));
				case ProgramContext _,BlockContext _,StatementContext _,StatementOrBlockContext _
					-> symbols.addAll(findSymbols(childCtx));
				default -> {}
			}
		}

		return symbols;
	}

	private static Range rangeFrom(ParserRuleContext ctx) {
		return new Range(
			new Position(ctx.getStart().getLine() - 1, ctx.getStart().getCharPositionInLine()),
			new Position(ctx.getStop().getLine() - 1, ctx.getStop().getCharPositionInLine()));
	}

	@Override
	public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
		return CompletableFutures.computeAsync(_ -> {
			var diagnostics = new ArrayList<Diagnostic>();
			var syntaxErrorsListener = new SyntaxErrorListener();
			try {
				var parser = parserFrom(params.getTextDocument());
				parser.addErrorListener(syntaxErrorsListener);
				parser.begin();
			} catch (IOException e) {
				log.info(e.toString());
			}
			for (var error : syntaxErrorsListener.getErrors()) {
				diagnostics.add(new Diagnostic(
					error.getRange(),
					error.msg()));
			}
			return new DocumentDiagnosticReport(
				new RelatedFullDocumentDiagnosticReport(diagnostics));
		});
	}

	@Override
	public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
		DefinitionParams params
	) {
		return CompletableFutures.computeAsync(_ -> {
			return Either.forLeft(List.of());
		});
	}

}

