package it.simonvic.giovi.visitors;

import it.simonvic.giovi.util.OperationEvaluator;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Optional;

public class SwitchHelperVisitor extends GioviHelperVisitor {

	private final GioviParser.SwitchStatementContext ctx;
	private final GioviObject value;

	public SwitchHelperVisitor(GioviVisitor gioviVisitor, GioviParser.SwitchStatementContext ctx) {
		super(gioviVisitor);
		this.ctx = ctx;
		this.value = gioviVisitor.visit(ctx.expression());
	}

	public static Optional<GioviParser.BlockContext> evaluate(GioviVisitor gioviVisitor, GioviParser.SwitchStatementContext ctx) {
		return new SwitchHelperVisitor(gioviVisitor, ctx).evaluate();
	}

	public Optional<GioviParser.BlockContext> evaluate() {
		for (GioviParser.SwitchLabelContext label : ctx.switchBlock().switchLabel()) {
			if (visit(label.caseMatch()).asBoolean()) {
				return Optional.of(label.block());
			}
		}
		return Optional.ofNullable(ctx.elseBody);
	}

	@Override
	public GioviObject visitParenthesizedCaseMatch(GioviParser.ParenthesizedCaseMatchContext ctx) {
		return visit(ctx.caseMatch());
	}

	@Override
	public GioviObject visitRelationalCaseMatch(GioviParser.RelationalCaseMatchContext ctx) {
		return OperationEvaluator.relational(
			value,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitEqualityCaseMatch(GioviParser.EqualityCaseMatchContext ctx) {
		return OperationEvaluator.equality(
			value,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitGuardedCaseMatch(GioviParser.GuardedCaseMatchContext ctx) {

		return OperationEvaluator.bool(
			visit(ctx.caseMatch(0)),
			ctx.bop.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitContainmentCaseMatch(GioviParser.ContainmentCaseMatchContext ctx) {
		return OperationEvaluator.containment(
			value,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitBooleanCaseMatch(GioviParser.BooleanCaseMatchContext ctx) {
		return OperationEvaluator.bool(
			value,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}
}
