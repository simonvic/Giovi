package it.simonvic.giovi.visitors;

import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.util.OperationEvaluator;

import java.util.Optional;

public class SwitchHelperVisitor extends GioviHelperVisitor {

	private final SwitchStatementContext ctx;
	private final GioviObject value;

	public SwitchHelperVisitor(GioviVisitor gioviVisitor, SwitchStatementContext ctx) {
		super(gioviVisitor);
		this.ctx = ctx;
		this.value = gioviVisitor.visit(ctx.expression());
	}

	public static Optional<StatementOrBlockContext> evaluate(GioviVisitor gioviVisitor, SwitchStatementContext ctx) {
		return new SwitchHelperVisitor(gioviVisitor, ctx).evaluate();
	}

	public Optional<StatementOrBlockContext> evaluate() {
		for (SwitchLabelContext label : ctx.switchBlock().switchLabel()) {
			if (visit(label.caseMatch()).asBoolean()) {
				return Optional.of(label.body);
			}
		}
		return Optional.ofNullable(ctx.elseBody);
	}

	@Override
	public GioviObject visitParenthesizedCaseMatch(ParenthesizedCaseMatchContext ctx) {
		return visit(ctx.caseMatch());
	}

	@Override
	public GioviObject visitRelationalCaseMatch(RelationalCaseMatchContext ctx) {
		return OperationEvaluator.relational(
			value,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitEqualityCaseMatch(EqualityCaseMatchContext ctx) {
		return OperationEvaluator.equality(
			value,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitGuardedCaseMatch(GuardedCaseMatchContext ctx) {

		return OperationEvaluator.bool(
			visit(ctx.caseMatch(0)),
			ctx.bop.getText(),
			visit(ctx.caseMatch(1)));
	}

	@Override
	public GioviObject visitContainmentCaseMatch(ContainmentCaseMatchContext ctx) {
		return OperationEvaluator.containment(
			value,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitBooleanCaseMatch(BooleanCaseMatchContext ctx) {
		return OperationEvaluator.bool(
			value,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}
}
