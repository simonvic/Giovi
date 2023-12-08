package it.simonvic.giovi.delegates;

import java.util.Optional;

import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.lang.GioviParserVisitResult;
import it.simonvic.giovi.lang.values.GioviBool;
import it.simonvic.giovi.util.OperationEvaluator;

public class SwitchStatementDelegate extends GioviVisitorDelegate {

	private final SwitchBlockContext ctx;
	private final GioviParserVisitResult subject;

	public SwitchStatementDelegate(
		GioviVisitor gioviVisitor,
		SwitchBlockContext ctx,
		GioviParserVisitResult subject) {
		super(gioviVisitor);
		this.ctx = ctx;
		this.subject = subject;
	}

	public static Optional<SwitchLabelContext> evaluate(
		GioviVisitor gioviVisitor,
		SwitchBlockContext ctx,
		GioviParserVisitResult subject) {
		return new SwitchStatementDelegate(gioviVisitor, ctx, subject).evaluate();
	}

	public Optional<SwitchLabelContext> evaluate() {
		for (SwitchLabelContext ctx : ctx.switchLabel()) {
			if (visit(ctx.caseMatch()).asNativeBool()) {
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
		return OperationEvaluator.relational(
			subject,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitCaseMatchEquality(CaseMatchEqualityContext ctx) {
		return OperationEvaluator.equality(
			subject,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitCaseMatchGuarded(CaseMatchGuardedContext ctx) {
		return OperationEvaluator.bool(
			visit(ctx.caseMatch(0)),
			ctx.bop.getText(),
			visit(ctx.caseMatch(1)));
	}

	@Override
	public GioviParserVisitResult visitCaseMatchContainment(CaseMatchContainmentContext ctx) {
		return OperationEvaluator.containment(
			subject,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitCaseMatchBoolean(CaseMatchBooleanContext ctx) {
		return OperationEvaluator.bool(
			subject,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitCaseMatchTypeCheck(CaseMatchTypeCheckContext ctx) {
		var valueType = subject.asGioviValue().getGioviType();
		var checkType = gioviVisitor.visit(ctx.type());
		return new GioviBool(valueType.equals(checkType));
	}

}
