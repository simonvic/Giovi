package it.simonvic.giovi.lsp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DocumentSymbol;

import giovi.Module.FileModule;

public class GioviDocument {

	private final String uri;
	private Integer version;
	private String content;
	private boolean dirtyAST = true;
	private ParseTree ast;
	private List<Diagnostic> diagnostics = new ArrayList<>();
	private List<CodeAction> codeActions = new ArrayList<>();
	private List<DocumentSymbol> symbols = new ArrayList<>();

	public GioviDocument(String uri) {
		this.uri = uri;
	}

	public GioviDocument(String uri, Integer version, String content) {
		this.uri = uri;
		this.version = version;
		this.content = content;
	}

	public String getUri() {
		return uri;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		if (!this.content.equals(content)) {
			this.dirtyAST = true;
			this.content = content;
		}
	}

	public boolean isDirtyAST() {
		return dirtyAST;
	}

	public ParseTree getAST() {
		if (dirtyAST) {
			updateAST();
		}
		return ast;
	}

	public List<DocumentSymbol> getSymbols() {
		if (dirtyAST) {
			updateAST();
		}
		return symbols;
	}

	public List<Diagnostic> getDiagnostics() {
		if (dirtyAST) {
			updateAST();
		}
		return diagnostics;
	}

	public void updateAST() {
		var parser = LSPUtils.extendedParserFrom(content);
		var syntaxErrorsListener = new SyntaxErrorListener();
		var functionsBodyListener = new FunctionsBodyDiagnosticsListener();
		var symbolsListener = new SymbolsListener();
		parser.removeErrorListeners(); // silences terminal output
		parser.addErrorListener(syntaxErrorsListener);
		parser.addParseListener(functionsBodyListener);
		parser.addParseListener(symbolsListener);
		// TODO: use completable future
		ast = parser.begin();
		diagnostics = new ArrayList<>();
		var begin = parser.begin();
		var visitor = new LSVisitor(new FileModule(URI.create(uri)));
		visitor.visit(begin);
		diagnostics.addAll(syntaxErrorsListener.getDiagnostics());
		diagnostics.addAll(functionsBodyListener.getDiagnostics());
		diagnostics.addAll(visitor.getDiagnostics());

		codeActions = new ArrayList<>();
		codeActions.addAll(visitor.getCodeActions());

		symbols = new ArrayList<>();
		symbols.addAll(symbolsListener.getSymbols());

		dirtyAST = false;
	}

}

