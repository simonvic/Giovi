package it.simonvic.giovi.visitors;

import java.util.Optional;

import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.values.GioviBool;
import it.simonvic.giovi.util.OperationEvaluator;

public class SwitchHelperVisitor extends GioviHelperVisitor {

	private final SwitchBlockContext ctx;
	private final GioviObject subject;

	public SwitchHelperVisitor(
		GioviVisitor gioviVisitor,
		SwitchBlockContext ctx,
		GioviObject subject) {
		super(gioviVisitor);
		this.ctx = ctx;
		this.subject = subject;
	}

	public static Optional<SwitchLabelContext> evaluate(
		GioviVisitor gioviVisitor,
		SwitchBlockContext ctx,
		GioviObject subject) {
		return new SwitchHelperVisitor(gioviVisitor, ctx, subject).evaluate();
	}

	public Optional<SwitchLabelContext> evaluate() {
		for (SwitchLabelContext ctx : ctx.switchLabel()) {
			if (visit(ctx.caseMatch()).asBoolean()) {
				return Optional.of(ctx);
			}
		}
		return Optional.empty();
	}

	@Override
	public GioviObject visitCaseMatchParenthesized(CaseMatchParenthesizedContext ctx) {
		return visit(ctx.caseMatch());
	}

	@Override
	public GioviObject visitCaseMatchRelational(CaseMatchRelationalContext ctx) {
		return OperationEvaluator.relational(
			subject,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitCaseMatchEquality(CaseMatchEqualityContext ctx) {
		return OperationEvaluator.equality(
			subject,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitCaseMatchGuarded(CaseMatchGuardedContext ctx) {
		return OperationEvaluator.bool(
			visit(ctx.caseMatch(0)),
			ctx.bop.getText(),
			visit(ctx.caseMatch(1)));
	}

	@Override
	public GioviObject visitCaseMatchContainment(CaseMatchContainmentContext ctx) {
		return OperationEvaluator.containment(
			subject,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitCaseMatchBoolean(CaseMatchBooleanContext ctx) {
		return OperationEvaluator.bool(
			subject,
			ctx.op.getText(),
			gioviVisitor.visit(ctx.expression()));
	}

	@Override
	public GioviObject visitCaseMatchTypeCheck(CaseMatchTypeCheckContext ctx) {
		var valueType = subject.asGioviValue().getGioviType();
		var checkType = gioviVisitor.visit(ctx.type());
		return new GioviBool(valueType.equals(checkType));
	}

}
