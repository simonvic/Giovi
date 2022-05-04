package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.exceptions.*;
import it.simonvic.giovi.lang.LoopScope;
import it.simonvic.giovi.lang.Scope;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.Sequence;
import it.simonvic.giovi.lang.text.ExactText;
import it.simonvic.giovi.lang.text.ExactTextBlock;
import it.simonvic.giovi.lang.text.Text;
import it.simonvic.giovi.lang.text.TextBlock;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.util.OperationEvaluator;
import it.simonvic.giovi.visitors.SwitchHelperVisitor;

import java.util.List;
import java.util.Map;

public class GioviVisitor extends GioviParserBaseVisitor<GioviObject> {

	private static final Scope globalScope = new Scope();
	private static Scope currentScope = globalScope;

	public static Scope getCurrentScope() {
		return currentScope;
	}

	public static void setCurrentScope(Scope scope) {
		currentScope = scope;
	}

	@Override
	public GioviObject visitBlock(GioviParser.BlockContext ctx) {
		for (GioviParser.StatementContext statementContext : ctx.statement()) {
			if (getCurrentScope().shouldBreak()) {
				break;
			}

			if (getCurrentScope().shouldContinue()) {
				getCurrentScope().onContinued();
				break;
			}
			visit(statementContext);
		}
		return null;
	}

	@Override
	public GioviObject visitIfStatement(GioviParser.IfStatementContext ctx) {
		if (visit(ctx.expression()).asBool()) {
			visit(ctx.ifBody);
		} else if (ctx.elseBody != null) {
			visit(ctx.elseBody);
		}
		return null;
	}

	@Override
	public GioviObject visitSwitchStatement(GioviParser.SwitchStatementContext ctx) {
		SwitchHelperVisitor.evaluate(this, ctx).ifPresent(this::visit);
		return null;
	}

	@Override
	public GioviObject visitBreakStatement(GioviParser.BreakStatementContext ctx) {
		getCurrentScope().doBreak();
		return null;
	}

	@Override
	public GioviObject visitContinueStatement(GioviParser.ContinueStatementContext ctx) {
		getCurrentScope().doContinue();
		return null;
	}

	@Override
	public GioviObject visitForStatement(GioviParser.ForStatementContext ctx) {
		GioviObject collection = visit(ctx.collection);
		switch (collection) {
			case GioviList<?> list -> forList(ctx.identifier(), list, ctx.block());
			case GioviMap<?, ?> map -> forMap(ctx.identifier(), map, ctx.block());
			default -> throw new OperatorNotSupported("for");
		}
		return null;
	}

	private void forList(List<GioviParser.IdentifierContext> iterators, GioviList<?> list, GioviParser.BlockContext block) {
		if (iterators.size() != 1) throw new GioviException("Wrong number of iterators");
		if (list.get().size() == 0) return;

		setCurrentScope(new LoopScope(getCurrentScope()));

		String iteratorName = iterators.get(0).getText();
		getCurrentScope().declare(iteratorName, list.get().get(0));
		GioviValue<?> iterator = getCurrentScope().getVariableValue(iteratorName);

		for (GioviValue<?> value : list.get()) {

			if (getCurrentScope().shouldBreak()) {
				break;
			}
			if (getCurrentScope().shouldContinue()) {
				getCurrentScope().onContinued();
				continue;
			}

			iterator.set(value);
			visit(block);
		}

		setCurrentScope(getCurrentScope().getParentScope());
//		currentScope.delete(valueIteratorName); //@todo can I trust the GC?
	}

	private void forMap(List<GioviParser.IdentifierContext> iterators, GioviMap<?, ?> map, GioviParser.BlockContext block) {
		if (iterators.size() != 2) throw new GioviException("Wrong number of iterators");
		if (map.get().size() == 0) return;

		setCurrentScope(new LoopScope(getCurrentScope()));
		String keyIteratorName = iterators.get(0).getText();
		String elementIteratorName = iterators.get(1).getText();

		Map.Entry<?, ?> entry = map.get().entrySet().iterator().next();
		getCurrentScope().declare(keyIteratorName, (GioviValue<?>) entry.getKey());
		getCurrentScope().declare(elementIteratorName, (GioviValue<?>) entry.getValue());
		GioviValue<?> keyIterator = getCurrentScope().getVariableValue(keyIteratorName);
		GioviValue<?> elementIterator = getCurrentScope().getVariableValue(elementIteratorName);

		for (Map.Entry<?, ?> e : map.get().entrySet()) {

			if (getCurrentScope().shouldBreak()) {
				break;
			}
			if (getCurrentScope().shouldContinue()) {
				getCurrentScope().onContinued();
				continue;
			}

			keyIterator.set((GioviValue<?>) e.getKey());
			elementIterator.set((GioviValue<?>) e.getValue());
			visit(block);
		}

		setCurrentScope(getCurrentScope().getParentScope());
//		currentScope.delete(keyIteratorName);
//		currentScope.delete(elementIteratorName);
	}

	@Override
	public GioviObject visitRepeatStatement(GioviParser.RepeatStatementContext ctx) {
		setCurrentScope(new LoopScope(getCurrentScope()));
		boolean infiniteRepeat = ctx.IF() == null;
		while (infiniteRepeat || visit(ctx.expression()).asBool()) {
			if (getCurrentScope().shouldBreak()) {
				break;
			}
			if (getCurrentScope().shouldContinue()) {
				getCurrentScope().onContinued();
				continue;
			}
			visit(ctx.block());
		}
		setCurrentScope(getCurrentScope().getParentScope());
		return null;
	}


	@Override
	public GioviObject visitRedirectionToVarDeclaration(GioviParser.RedirectionToVarDeclarationContext ctx) {
		//@todo improve this. Declare variable with value already set
		visit(ctx.varDeclaration());
		switch (ctx.bop.getText()) {
			case ">" -> getCurrentScope()
				.getVariableValue(ctx.varDeclaration().identifier().getText())
				.redirection(visit(ctx.expression()));
			default -> throw new RedirectionException("Unknown redirection operator: " + ctx.bop.getText());
		}
		return null;
	}

	@Override
	public GioviObject visitVarDeclaration(GioviParser.VarDeclarationContext ctx) {
		if (ctx.type().collectionType() != null) {
			declareCollection(ctx.identifier().getText(), ctx.type().collectionType());
		} else {
			declareVariable(ctx.identifier().getText(), ctx.type().getText());
		}
		return null;
	}

	private void declareCollection(String identifier, GioviParser.CollectionTypeContext collectionType) {
		if (collectionType.list() != null) {
			declareList(identifier, collectionType.list().type().getText());
		} else if (collectionType.map() != null) {
			declareMap(identifier, collectionType.map().type());
		}
	}

	private void declareMap(String identifier, List<GioviParser.TypeContext> type) {
		getCurrentScope().declare(identifier, new GioviMap<>());
	}

	private void declareList(String identifier, String type) {
		getCurrentScope().declare(identifier, switch (type) {
			case "Natural" -> new GioviList<GioviNatural>();
			case "Int" -> new GioviList<GioviInt>();
			case "Real" -> new GioviList<GioviReal>();
			case "Bool" -> new GioviList<GioviBool>();
			case "Text" -> new GioviList<GioviText>();
			default -> throw new TypeNotFound(type);
		});
	}


	private void declareVariable(String identifier, String type) {
		getCurrentScope().declare(identifier, switch (type) {
			case "Natural" -> new GioviNatural();
			case "Int" -> new GioviInt();
			case "Real" -> new GioviReal();
			case "Bool" -> new GioviBool();
			case "Text" -> new GioviText();
			default -> throw new TypeNotFound(type);
		});
	}

	@Override
	public GioviObject visitRedirectionToIdentifier(GioviParser.RedirectionToIdentifierContext ctx) {
		String identifier = ctx.identifier().getText();
		switch (ctx.bop.getText()) {
			case ">" ->
				getCurrentScope().getVariableValue(identifier).redirection(visit(ctx.expression())).asGioviValue();
			case ">+" ->
				getCurrentScope().getVariableValue(identifier).redirectionPlus(visit(ctx.expression())).asGioviValue();
			case ">-" ->
				getCurrentScope().getVariableValue(identifier).redirectionMinus(visit(ctx.expression())).asGioviValue();
			case ">*" ->
				getCurrentScope().getVariableValue(identifier).redirectionMultiply(visit(ctx.expression())).asGioviValue();
			case ">/" ->
				getCurrentScope().getVariableValue(identifier).redirectionDivide(visit(ctx.expression())).asGioviValue();
			default -> throw new RedirectionException("Unknown redirection operator: " + ctx.bop.getText());
		}
		return null;
	}


	@Override
	public GioviObject visitRedirectionToPrimitiveDestination(GioviParser.RedirectionToPrimitiveDestinationContext ctx) {
		GioviObject content = visit(ctx.expression());
		switch (ctx.primitiveDestination().getText()) {
			case "\\", "\\out" -> System.out.println(content);
			case "\\error" -> System.err.println(content);
		}
		return null;
	}

	@Override
	public GioviObject visitParenthesizedExpression(GioviParser.ParenthesizedExpressionContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public GioviObject visitPrefixedExpression(GioviParser.PrefixedExpressionContext ctx) {
		return OperationEvaluator.prefixed(
			visit(ctx.expression()),
			ctx.prefix.getText());
	}

	@Override
	public GioviObject visitAdditiveExpression(GioviParser.AdditiveExpressionContext ctx) {
		return OperationEvaluator.additive(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitMultiplicativeExpression(GioviParser.MultiplicativeExpressionContext ctx) {
		return OperationEvaluator.multiplicative(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitPowerExpression(GioviParser.PowerExpressionContext ctx) {
		return visit(ctx.expression(0)).power(visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitRelationalExpression(GioviParser.RelationalExpressionContext ctx) {
		return OperationEvaluator.relational(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitNotExpression(GioviParser.NotExpressionContext ctx) {
		return visit(ctx.expression()).not();
	}

	@Override
	public GioviObject visitBoolExpression(GioviParser.BoolExpressionContext ctx) {
		return OperationEvaluator.bool(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitEqualityExpression(GioviParser.EqualityExpressionContext ctx) {
		return OperationEvaluator.equality(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitContainmentExpression(GioviParser.ContainmentExpressionContext ctx) {
		return OperationEvaluator.containment(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitCollectionAccess(GioviParser.CollectionAccessContext ctx) {
		return getCurrentScope()
			.getVariableValue(ctx.identifier().getText())
			.asGioviCollection()
			.access(visit(ctx.collectionAccessor()));
	}


	@Override
	public GioviObject visitSpecialAccessor(GioviParser.SpecialAccessorContext ctx) {
		return new Accessor(ctx.getText());
	}

	@Override
	public GioviObject visitExpressionAccessor(GioviParser.ExpressionAccessorContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public GioviObject visitIdentifier(GioviParser.IdentifierContext ctx) {
		return getCurrentScope().getVariableValue(ctx.getText());
	}

	@Override
	public GioviObject visitSequence(GioviParser.SequenceContext ctx) {
		return Sequence.of(
			visit(ctx.start).asGioviValue(),
			visit(ctx.end).asGioviValue(),
			ctx.step == null ? null : visit(ctx.step).asGioviValue());
	}

	@Override
	public GioviObject visitListCreation(GioviParser.ListCreationContext ctx) {
		GioviList<GioviValue<?>> list = new GioviList<>();
		for (GioviParser.ListEntryContext entryCtx : ctx.listEntries().listEntry()) {
			GioviObject entry = visit(entryCtx);
			switch (entry) {
				case Sequence<?> seq -> list.addAll(seq.getValues());
				case GioviValue<?> v -> list.add(v);
				default -> throw new IncompatibleTypes(entry.getClass(), list.getClass());
			}
		}
		return list;
	}

	@Override
	public GioviObject visitMapCreation(GioviParser.MapCreationContext ctx) {
		GioviMap<GioviValue<?>, GioviValue<?>> map = new GioviMap<>();
		for (GioviParser.MapEntryContext entryCtx : ctx.mapEntries().mapEntry()) {
			map.put(visit(entryCtx.mapKey).asGioviValue(), visit(entryCtx.mapValue).asGioviValue());
		}
		return map;
	}

	@Override
	public GioviObject visitIntegerLiteral(GioviParser.IntegerLiteralContext ctx) {
		return new GioviInt(Integer.parseInt(ctx.getText()));
	}

	@Override
	public GioviObject visitNaturalLiteral(GioviParser.NaturalLiteralContext ctx) {
		return new GioviNatural(new Natural(Integer.parseInt(ctx.getText())));
	}

	@Override
	public GioviObject visitRealLiteral(GioviParser.RealLiteralContext ctx) {
		return new GioviReal(Double.parseDouble(ctx.getText()));
	}

	@Override
	public GioviObject visitBoolLiteral(GioviParser.BoolLiteralContext ctx) {
		return new GioviBool(Boolean.parseBoolean(ctx.getText()));
	}

	@Override
	public GioviObject visitTextLiteral(GioviParser.TextLiteralContext ctx) {
		return new GioviText((new Text(ctx.getText()).toString()));
	}

	@Override
	public GioviObject visitTextBlockLiteral(GioviParser.TextBlockLiteralContext ctx) {
		return new GioviText(new TextBlock(ctx.getText()).toString());
	}

	@Override
	public GioviObject visitExactTextLiteral(GioviParser.ExactTextLiteralContext ctx) {
		return new GioviText(new ExactText(ctx.getText()).toString());
	}

	@Override
	public GioviObject visitExactTextBlockLiteral(GioviParser.ExactTextBlockLiteralContext ctx) {
		return new GioviText(new ExactTextBlock(ctx.getText()).toString());
	}

	@Override
	public GioviObject visitNullLiteral(GioviParser.NullLiteralContext ctx) {
		return null;
	}

//	@Override
//	public GioviObject visitDefaultLiteral(GioviParser.DefaultLiteralContext ctx) {
//		return "_";
//	}

}