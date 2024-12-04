package it.simonvic.giovi.delegates;

import java.util.Optional;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviBool;
import it.simonvic.giovi.GioviToken;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.util.VisitorUtils;

public class SwitchStatementDelegate extends GioviVisitorDelegate {

	private final SwitchBlockContext ctx;
	private final GioviParserVisitResult subject;

	public SwitchStatementDelegate(
		GioviVisitor gioviVisitor,
		SwitchBlockContext ctx,
		GioviParserVisitResult subject
	) {
		super(gioviVisitor);
		this.ctx = ctx;
		this.subject = subject;
	}

	public static Optional<SwitchLabelContext> evaluate(
		GioviVisitor gioviVisitor,
		SwitchBlockContext ctx,
		GioviParserVisitResult subject
	) {
		return new SwitchStatementDelegate(gioviVisitor, ctx, subject).evaluate();
	}

	public Optional<SwitchLabelContext> evaluate() {
		for (SwitchLabelContext ctx : ctx.switchLabel()) {
			if (visit(ctx.caseMatch()).asNativeBool()
				&& (ctx.guard == null || gioviVisitor.visit(ctx.guard).asNativeBool())) {
				return Optional.of(ctx);
			}
		}
		return Optional.empty();
	}

	@Override
	public GioviParserVisitResult visitCaseMatchParenthesized(CaseMatchParenthesizedContext ctx) {
		return visit(ctx.caseMatch());
	}

	@Override
	public GioviParserVisitResult visitCaseMatchRelational(CaseMatchRelationalContext ctx) {
		return VisitorUtils.relational(
			subject,
			GioviToken.of(ctx.op.getText()),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitCaseMatchEquality(CaseMatchEqualityContext ctx) {
		return VisitorUtils.equality(
			subject,
			GioviToken.of(ctx.op.getText()),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitCaseMatchCascade(CaseMatchCascadeContext ctx) {
		return VisitorUtils.bool(
			visit(ctx.caseMatch(0)),
			GioviToken.of(ctx.bop.getText()),
			() -> visit(ctx.caseMatch(1)));
	}

	@Override
	public GioviParserVisitResult visitCaseMatchContainment(CaseMatchContainmentContext ctx) {
		return VisitorUtils.containment(
			subject,
			GioviToken.of(ctx.op.getText()),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitCaseMatchBoolean(CaseMatchBooleanContext ctx) {
		return VisitorUtils.bool(
			subject,
			GioviToken.of(ctx.op.getText()),
			() -> gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitCaseMatchTypeCheck(CaseMatchTypeCheckContext ctx) {
		var valueType = subject.unwrapAsGioviValue().getGioviType();
		var checkType = gioviVisitor.visit(ctx.type());
		boolean equals = valueType.equals(checkType);
		if (ctx.NOT() != null) {
			return new GioviBool(!equals);
		}
		return new GioviBool(equals);
	}

}
