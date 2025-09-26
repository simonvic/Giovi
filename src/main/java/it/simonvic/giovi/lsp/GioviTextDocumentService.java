package it.simonvic.giovi.lsp;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import giovi.Module.FileModule;
import it.simonvic.giovi.antlr.GioviExtendedParser.StatementContext;
import it.simonvic.giovi.antlr.GioviParser;

public class GioviTextDocumentService implements TextDocumentService {

	private static final Logger log = Logger.getLogger("TextDocumentService");

	private final GioviLanguageServer server;
	private final Map<String, GioviDocument> documents = new HashMap<>();

	public GioviTextDocumentService(GioviLanguageServer server) {
		this.server = server;
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
		log.info("didOpen() " + params.getTextDocument().getUri());
		var lspDoc = params.getTextDocument();
		var doc = new GioviDocument(
			lspDoc.getUri(),
			lspDoc.getVersion(),
			lspDoc.getText());
		documents.put(lspDoc.getUri(), doc);
	}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		log.info("didChange " + params.getTextDocument().getUri());
		var lspDoc = params.getTextDocument();
		// TODO: change once we implement incremental sync
		String newText = params.getContentChanges().getFirst().getText();
		var doc = documents.get(lspDoc.getUri()); // TODO: can didChange triggers without didOpen?
		doc.setContent(newText);
		doc.setVersion(lspDoc.getVersion());
		// CompletableFutures.computeAsync(
		// 	// CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS),
		// 	_ -> {
		// 		var diagnostics = validate(doc);
		// 		try {
		// 			Thread.sleep(500);
		// 		} catch (InterruptedException e) {
		// 		}
		// 		server.getClient().publishDiagnostics(new PublishDiagnosticsParams(
		// 			params.getTextDocument().getUri(),
		// 			diagnostics));
		// 		return null;
		// 	});
	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		log.info("didSave() " + params.getTextDocument().getUri());
		// TODO: check if buffered content equals params.getText
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
		log.info("didClose() " + params.getTextDocument().getUri());
		documents.remove(params.getTextDocument().getUri());
	}

	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
		CompletionParams completionParams
	) {
		log.info("completion()");
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
		log.info("codeActions()");
		return CompletableFutures.computeAsync(_ -> {
			return List.of(
				Either.forRight(new CodeAction("Flip redirection")),
				Either.forRight(new CodeAction("Fix imports")),
				Either.forRight(new CodeAction("Extract")));
		});
	}

	@Override
	public CompletableFuture<Hover> hover(HoverParams params) {
		log.info("hover()");
		var doc = documents.get(params.getTextDocument().getUri());
		var parser = LSPUtils.extendedParserFrom(doc);

		Optional<TerminalNode> matchedNodeOpt = LSPUtils.findLeaf(
			parser.begin(),
			params.getPosition());
		Hover hover;
		if (matchedNodeOpt.isEmpty()) {
			hover = null;
		} else {
			var matchedNode = matchedNodeOpt.orElseThrow();
			var sb = new StringBuilder();
			List<ParseTree> hierarchy = LSPUtils.getHierarchy(matchedNode).reversed();
			for (int i = 0; i < hierarchy.size(); i++) {
				sb.repeat(" ", i);
				sb.append("(");
				sb.append(hierarchy.get(i).getClass().getSimpleName());
				sb.append("\n");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.repeat(")", hierarchy.size());
			String content = String.format(
				"""
				- Text: *%s*
				- Type: `%s`
				- Ancestor rule type: `%s`
				- Statement ancestor: `%s`
				- Hierarchy :
				```scheme
				%s
				````
				""",
				matchedNode.getText(),
				matchedNode.getClass().getSimpleName(),
				matchedNode.getParent().getClass().getSimpleName(),
				LSPUtils.findClosestAncestor(matchedNode, StatementContext.class)
					.map(ctx -> ctx.getChild(0).getClass().getSimpleName())
					.orElse("unknown"),
				sb.toString());
			hover = new Hover(new MarkupContent(MarkupKind.MARKDOWN, content));
		}
		return CompletableFutures.computeAsync(_ -> {
			return hover;
		});
	}


	@Override
	public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
		log.info("formatting()");
		return CompletableFutures.computeAsync(_ -> {
			var edits = new ArrayList<TextEdit>();
			return edits;
		});
	}

	@Override
	public CompletableFuture<List<InlayHint>> inlayHint(InlayHintParams params) {
		log.info("inlayHint()");
		return CompletableFutures.computeAsync(_ -> {
			return List.of(
				new InlayHint(new Position(0, 0), Either.forLeft("This is a sample inlay hint")));
		});
	}

	@Override
	public CompletableFuture<SignatureHelp> signatureHelp(SignatureHelpParams params) {
		log.info("signatureHelp()");
		return CompletableFutures.computeAsync(_ -> {
			return new SignatureHelp(List.of(new SignatureInformation("signature help")), 0, 0);
		});
	}

	@Override
	public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(
		DocumentSymbolParams params
	) {
		log.info("documentSymbol()");
		var symbols = documents.get(params.getTextDocument().getUri())
			.getSymbols()
			.stream()
			.<Either<SymbolInformation, DocumentSymbol>>map(Either::forRight)
			.toList();
		return CompletableFutures.computeAsync(_ -> {
			return symbols;
		});
	}

	@Override
	public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
		log.info("diagnostic()");
		return CompletableFutures.computeAsync(cancel -> {
			GioviDocument doc = documents.get(params.getTextDocument().getUri());
			cancel.checkCanceled();
			return new DocumentDiagnosticReport(
				new RelatedFullDocumentDiagnosticReport(doc.getDiagnostics()));
		});
	}

	@Override
	public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
		DefinitionParams params
	) {
		log.info("definition()");
		return CompletableFutures.computeAsync(_ -> {
			return Either.forLeft(List.of());
		});
	}

}

