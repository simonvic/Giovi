package it.simonvic.giovi.lsp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DocumentSymbol;

import giovi.GioviParserVisitResult;
import giovi.Module;
import giovi.lang.*;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.antlr.GioviExtendedParser.*;
import it.simonvic.giovi.antlr.GioviExtendedParserBaseVisitor;
import it.simonvic.giovi.exceptions.TypeNotFound;
import it.simonvic.giovi.lang.FunctionScope;
import it.simonvic.giovi.lang.LoopScope;
import it.simonvic.giovi.lang.Scope;
import it.simonvic.giovi.util.TextProcessor;

public class LSVisitor extends GioviExtendedParserBaseVisitor<GioviParserVisitResult> {

	private final Module module;
	private final List<Diagnostic> diagnostics = new ArrayList<>();
	private final List<CodeAction> codeActions = new ArrayList<>();
	private final List<DocumentSymbol> symbols = new ArrayList<>();

	private Scope currentScope;

	public LSVisitor(Module module) {
		this.module = module;
		this.currentScope = module.scope();
	}

	public List<Diagnostic> getDiagnostics() {
		return diagnostics;
	}

	public List<CodeAction> getCodeActions() {
		return codeActions;
	}

	public List<DocumentSymbol> getSymbols() {
		return symbols;
	}

	public Scope getCurrentScope() {
		return currentScope;
	}

	private void setCurrentScope(Scope scope) {
		currentScope = scope;
	}

	private void enterScope() {
		setCurrentScope(new Scope(getCurrentScope()));
	}

	private void enterFunctionScope() {
		setCurrentScope(new FunctionScope(getCurrentScope()));
	}

	private void enterLoopScope() {
		setCurrentScope(new LoopScope(getCurrentScope()));
	}

	private void exitScope() {
		setCurrentScope(getCurrentScope().getParent());
	}

	@Override
	public GioviType<?> visitType(TypeContext ctx) {
		if (ctx.identifier() != null) {
			String typename = ctx.identifier().getText();
			return getCurrentScope()
				.getType(typename)
				.orElseThrow(() -> new TypeNotFound(typename));
		}
		return visitChildren(ctx).asGioviType();
	}

	@Override
	public VarD<?> visitVarDeclaration(VarDeclarationContext ctx) {
		try {
			var type = visitType(ctx.type());
			var value = type.spawn();
			GioviValue<?> defaultValue;
			defaultValue = (ctx.defaultDefinition() != null)
				? type.widen(visit(ctx.defaultDefinition()).asGioviValue())
				: type.spawn();
			var variable = new VarD<>(value, defaultValue);
			getCurrentScope().declare(ctx.identifier().getText(), variable);
			return variable;
		} catch (Exception e) {
			diagnostics.add(new Diagnostic(LSPUtils.range(ctx), e.getMessage()));
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToReservedIdentifier(
		RedirectionToReservedIdentifierContext ctx
	) {
		checkRedirectionToStdInput(ctx.dest);
		return super.visitRedirectionToReservedIdentifier(ctx);
	}

	@Override
	public GioviParserVisitResult visitRedirectionToReservedIdentifierRTL(
		RedirectionToReservedIdentifierRTLContext ctx
	) {
		checkRedirectionToStdInput(ctx.dest);
		return super.visitRedirectionToReservedIdentifierRTL(ctx);
	}

	private void checkRedirectionToStdInput(ReservedIdentifierContext dest) {
		if (dest.getText().equals("_in")) {
			var d = new Diagnostic();
			d.setRange(LSPUtils.range(dest));
			d.setSeverity(DiagnosticSeverity.Error);
			d.setMessage("Can't redirect to standard input");
			diagnostics.add(d);
		}
	}

	@Override
	public GioviParserVisitResult visitReservedIdentifier(ReservedIdentifierContext ctx) {
		switch (ctx.getText()) {
			case "_in", "_out" -> {}
			case String s -> {
				var d = new Diagnostic();
				d.setRange(LSPUtils.range(ctx));
				d.setMessage("Unknown reserved identifier: " + s);
				d.setSeverity(DiagnosticSeverity.Error);
				diagnostics.add(d);
			}
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitLiteralInteger(LiteralIntegerContext ctx) {
		return GioviInt.valueOf(ctx.getText());
	}

	@Override
	public GioviParserVisitResult visitLiteralNatural(LiteralNaturalContext ctx) {
		return GioviNatural.valueOf(ctx.getText());
	}

	@Override
	public GioviParserVisitResult visitLiteralReal(LiteralRealContext ctx) {
		return GioviReal.valueOf(ctx.getText());
	}

	@Override
	public GioviParserVisitResult visitLiteralBool(LiteralBoolContext ctx) {
		return GioviBool.valueOf(ctx.getText());
	}

	@Override
	public GioviParserVisitResult visitLiteralText(LiteralTextContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.unescapeEscapes()
			// .injectVariables(getCurrentScope())
			// .injectExpressions(this)
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralTextBlock(LiteralTextBlockContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.unescapeEscapes()
			// .injectVariables(getCurrentScope())
			// .injectExpressions(this)
			.trimIncidentalWhitespaces()
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralExactText(LiteralExactTextContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.unescapeEscapes()
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralExactTextBlock(LiteralExactTextBlockContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.trimIncidentalWhitespaces()
			.unescapeEscapes()
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralDefault(LiteralDefaultContext ctx) {
		return new GioviDefault();
	}

	@Override
	public GioviParserVisitResult visitLiteralPlaceholder(LiteralPlaceholderContext ctx) {
		return new GioviVoid();
	}

}
