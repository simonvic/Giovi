package it.simonvic.giovi.lsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;

import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.antlr.GioviParserBaseListener;

public class SymbolsListener extends GioviParserBaseListener {


	private Stack<DocumentSymbol> stack = new Stack<>();
	private List<DocumentSymbol> symbols = new ArrayList<>();

	public List<DocumentSymbol> getSymbols() {
		return symbols;
	}

	private static Range rangeFrom(ParserRuleContext ctx) {
		return new Range(
			new Position(ctx.getStart().getLine() - 1, ctx.getStart().getCharPositionInLine()),
			new Position(ctx.getStop().getLine() - 1, ctx.getStop().getCharPositionInLine()));
	}

	private void add(DocumentSymbol documentSymbol) {
		if (stack.isEmpty()) {
			symbols.add(documentSymbol);
			return;
		}
		DocumentSymbol parent = stack.peek();
		if (parent.getChildren() == null) {
			parent.setChildren(new ArrayList<>());
		}
		parent.getChildren().add(documentSymbol);
	}

	private void pushEmptySymbol() {
		var symbol = new DocumentSymbol();
		add(symbol);
		stack.push(symbol);
	}

	@Override
	public void exitVarDeclaration(VarDeclarationContext ctx) {
		add(new DocumentSymbol(
			ctx.identifier().getText(),
			SymbolKind.Variable,
			rangeFrom(ctx),
			rangeFrom(ctx.identifier())));
	}

	@Override
	public void exitConstantDeclaration(ConstantDeclarationContext ctx) {
		add(new DocumentSymbol(
			ctx.name.getText(),
			SymbolKind.Constant,
			rangeFrom(ctx),
			rangeFrom(ctx.name)));
	}

	@Override
	public void exitImportEntry(ImportEntryContext ctx) {
		if (ctx.alias == null) return;
		add(new DocumentSymbol(
			ctx.alias.getText(),
			SymbolKind.Namespace,
			rangeFrom(ctx),
			rangeFrom(ctx.alias),
			ctx.importURI().stream().map(c -> c.getText()).collect(Collectors.joining("."))));
	}

	@Override
	public void enterModuleDeclaration(ModuleDeclarationContext ctx) {
		pushEmptySymbol();
	}

	@Override
	public void exitModuleDeclaration(ModuleDeclarationContext ctx) {
		var symbol = stack.pop();
		symbol.setName(ctx.identifier().getText());
		symbol.setKind(SymbolKind.Module);
		symbol.setRange(rangeFrom(ctx));
		symbol.setSelectionRange(rangeFrom(ctx.identifier()));
	}

	@Override
	public void enterFunctionDeclaration(FunctionDeclarationContext ctx) {
		pushEmptySymbol();
	}

	@Override
	public void exitFunctionDeclaration(FunctionDeclarationContext ctx) {
		var symbol = stack.pop();
		symbol.setName(ctx.functionSignature().identifier().getText());
		symbol.setKind(SymbolKind.Function);
		symbol.setRange(rangeFrom(ctx));
		symbol.setSelectionRange(rangeFrom(ctx.functionSignature().identifier()));
		symbol.setDetail(ctx.functionSignature().getText());
	}

	@Override
	public void enterSelfFunctionDeclaration(SelfFunctionDeclarationContext ctx) {
		pushEmptySymbol();
	}

	@Override
	public void exitSelfFunctionDeclaration(SelfFunctionDeclarationContext ctx) {
		var symbol = stack.pop();
		symbol.setName(ctx.selfFunctionSignature().identifier().getText());
		symbol.setKind(SymbolKind.Function); // Method?
		symbol.setRange(rangeFrom(ctx));
		symbol.setSelectionRange(rangeFrom(ctx.selfFunctionSignature().identifier()));
		symbol.setDetail(ctx.selfFunctionSignature().getText());
	}

	@Override
	public void enterStructDefinition(StructDefinitionContext ctx) {
		pushEmptySymbol();
	}

	@Override
	public void exitStructDefinition(StructDefinitionContext ctx) {
		var symbol = stack.pop();
		symbol.setName(ctx.typename.getText());
		symbol.setKind(SymbolKind.Struct);
		symbol.setRange(rangeFrom(ctx));
		symbol.setSelectionRange(rangeFrom(ctx.typename));
	}

	@Override
	public void exitStructFieldDefinition(StructFieldDefinitionContext ctx) {
		add(new DocumentSymbol(
			ctx.identifier().getText(),
			SymbolKind.Field,
			rangeFrom(ctx),
			rangeFrom(ctx.identifier())));
	}


}
