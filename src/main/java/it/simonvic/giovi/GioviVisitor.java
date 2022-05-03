package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.exceptions.*;
import it.simonvic.giovi.lang.Block;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.Sequence;
import it.simonvic.giovi.lang.text.ExactText;
import it.simonvic.giovi.lang.text.ExactTextBlock;
import it.simonvic.giovi.lang.text.Text;
import it.simonvic.giovi.lang.text.TextBlock;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.*;

import java.util.List;
import java.util.Map;

public class GioviVisitor extends GioviParserBaseVisitor<GioviObject> {

	private static final Block globalBlock = new Block(null);
	private static Block currentScopeBlock = globalBlock;

	public static Block getCurrentScope() {
		return currentScopeBlock;
	}

	@Override
	public GioviObject visitIfStatement(GioviParser.IfStatementContext ctx) {
		if (visit(ctx.expression()).asBool()) {
			visit(ctx.ifBlock);
		} else if (ctx.ELSE() != null) {
			visit(ctx.elseBlock);
		}
		return null;
	}


	// lol
	private boolean shouldBreak = false;

	@Override
	public GioviObject visitForStatement(GioviParser.ForStatementContext ctx) {
		GioviObject collection = visit(ctx.collection);
		switch (collection) {
			case ListValue list -> forList(ctx.identifier(), list, ctx.block());
			case MapValue map -> forMap(ctx.identifier(), map, ctx.block());
			default -> throw new OperatorNotSupported("for");
		}
		return null;
	}

	private void forMap(List<GioviParser.IdentifierContext> iterators, MapValue<Value<?>, Value<?>> map, GioviParser.BlockContext block) {
		if (iterators.size() != 2) throw new GioviException("Wrong number of iterators");
		if (map.get().size() == 0) return;

		String keyIteratorName = iterators.get(0).getText();
		String elementIteratorName = iterators.get(1).getText();

		Map.Entry<Value<?>, Value<?>> entry = map.get().entrySet().iterator().next();
		currentScopeBlock.declare(keyIteratorName, entry.getKey());
		currentScopeBlock.declare(elementIteratorName, entry.getValue());

		map.get().forEach((keyValue, elementValue) -> {
			currentScopeBlock.getVariableValue(keyIteratorName).set(keyValue);
			currentScopeBlock.getVariableValue(elementIteratorName).set(elementValue);
			visit(block);
		});

		currentScopeBlock.delete(keyIteratorName);
		currentScopeBlock.delete(elementIteratorName);
	}

	private void forList(List<GioviParser.IdentifierContext> iterators, ListValue<Value<?>> list, GioviParser.BlockContext block) {
		if (iterators.size() != 1) throw new GioviException("Wrong number of iterators");
		if (list.get().size() == 0) return;

		String valueIteratorName = iterators.get(0).getText();
		currentScopeBlock.declare(valueIteratorName, list.get().get(0));
		list.get().forEach(value -> {
			currentScopeBlock.getVariableValue(valueIteratorName).set(value);
			visit(block);
		});
		currentScopeBlock.delete(valueIteratorName);
	}

	@Override
	public GioviObject visitBreakStatement(GioviParser.BreakStatementContext ctx) {
		shouldBreak = true;
		return null;
	}

	@Override
	public GioviObject visitRepeatStatement(GioviParser.RepeatStatementContext ctx) {
		shouldBreak = false;
		if (ctx.IF() == null) {
			while (!shouldBreak) {
				visit(ctx.block());
			}
		} else {
			while (!shouldBreak && visit(ctx.expression()).asBool()) {
				visit(ctx.block());
			}

		}
		return null;
	}


	@Override
	public GioviObject visitVarDeclaration(GioviParser.VarDeclarationContext ctx) {
		if (ctx.type().collectionType() != null) {
			declareCollection(ctx.identifier().getText(), ctx.type().collectionType(), null);
		} else {
			declareVariable(ctx.identifier().getText(), ctx.type().getText(), null);
		}
		return null;
	}

	private void declareCollection(String identifier, GioviParser.CollectionTypeContext collectionType, Value<?> value) {
		if (collectionType.list() != null) {
			declareList(identifier, collectionType.list().type().getText(), value);
		} else if (collectionType.map() != null) {
			declareMap(identifier, collectionType.map().type(), value);
		}
	}

	private void declareMap(String identifier, List<GioviParser.TypeContext> type, Value<?> value) {
		currentScopeBlock.declare(identifier, new MapValue<>());
	}

	private void declareList(String identifier, String type, Value<?> value) {
		currentScopeBlock.declare(identifier, switch (type) {
			case "Natural" -> new ListValue<NaturalValue>();
			case "Int" -> new ListValue<IntValue>();
			case "Real" -> new ListValue<RealValue>();
			case "Bool" -> new ListValue<BoolValue>();
			case "Text" -> new ListValue<TextValue>();
			default -> throw new TypeNotFound(type);
		});
	}


	@Override
	public GioviObject visitRedirectionToVarDeclaration(GioviParser.RedirectionToVarDeclarationContext ctx) {
		declareVariable(
			ctx.varDeclaration().identifier().getText(),
			ctx.varDeclaration().type().getText(),
			visit(ctx.expression()).asValue()
		);
		return null;
	}


	private void declareVariable(String identifier, String type, Value<?> value) {
		currentScopeBlock.declare(identifier, switch (type) {
			case "Natural" -> value != null ? value : new NaturalValue();
			case "Int" -> value != null ? value : new IntValue();
			case "Real" -> value != null ? value : new RealValue();
			case "Bool" -> value != null ? value : new BoolValue();
			case "Text" -> value != null ? value : new TextValue();
			default -> throw new TypeNotFound(type);
		});
	}

	@Override
	public GioviObject visitRedirectionToIdentifier(GioviParser.RedirectionToIdentifierContext ctx) {
		String identifier = ctx.identifier().getText();
		currentScopeBlock.setVariableValue(identifier, switch (ctx.bop.getText()) {
			case ">" -> visit(ctx.expression()).asValue();
			case ">+" -> currentScopeBlock.getVariableValue(identifier).plus(visit(ctx.expression())).asValue();
			case ">-" -> currentScopeBlock.getVariableValue(identifier).minus(visit(ctx.expression())).asValue();
			case ">*" -> currentScopeBlock.getVariableValue(identifier).multiply(visit(ctx.expression())).asValue();
			case ">/" -> currentScopeBlock.getVariableValue(identifier).divide(visit(ctx.expression())).asValue();
			default -> throw new RedirectionException("Unknown redirection operator: " + ctx.bop.getText());
		});
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
		GioviObject value = visit(ctx.expression());
		return switch (ctx.prefix.getText()) {
			case "+" -> value.positive();
			case "-" -> value.negative();
			default -> throw new OperatorNotSupported(ctx.prefix.getText());
		};
	}

	@Override
	public GioviObject visitAdditiveExpression(GioviParser.AdditiveExpressionContext ctx) {
		GioviObject left = visit(ctx.expression(0));
		GioviObject right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "+" -> left.plus(right);
			case "-" -> left.minus(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public GioviObject visitMultiplicativeExpression(GioviParser.MultiplicativeExpressionContext ctx) {
		GioviObject left = visit(ctx.expression(0));
		GioviObject right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "*" -> left.multiply(right);
			case "/" -> left.divide(right);
			case "%" -> left.modulo(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public GioviObject visitPowerExpression(GioviParser.PowerExpressionContext ctx) {
		return visit(ctx.expression(0)).power(visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitRelationalExpression(GioviParser.RelationalExpressionContext ctx) {
		GioviObject left = visit(ctx.expression(0));
		GioviObject right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "<=" -> left.lessEqual(right);
			case ">=" -> left.greaterEqual(right);
			case ">" -> left.greater(right);
			case "<" -> left.less(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public GioviObject visitNotExpression(GioviParser.NotExpressionContext ctx) {
		return visit(ctx.expression()).not();
	}

	@Override
	public GioviObject visitBoolExpression(GioviParser.BoolExpressionContext ctx) {
		GioviObject left = visit(ctx.expression(0));
		GioviObject right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "&" -> left.and(right);
			case "|" -> left.or(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public GioviObject visitEqualityExpression(GioviParser.EqualityExpressionContext ctx) {
		GioviObject left = visit(ctx.expression(0));
		GioviObject right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "=" -> left.equal(right);
			case "!=" -> left.notEqual(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public GioviObject visitContainmentExpression(GioviParser.ContainmentExpressionContext ctx) {
		GioviObject left = visit(ctx.expression(0));
		GioviObject right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "~=" -> left.isContained(right);
			case "=~" -> left.contains(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public GioviObject visitIdentifier(GioviParser.IdentifierContext ctx) {
		return getCurrentScope().getVariableValue(ctx.getText());
	}

	@Override
	public GioviObject visitSequence(GioviParser.SequenceContext ctx) {
		return Sequence.of(
			visit(ctx.start).asValue(),
			visit(ctx.end).asValue(),
			ctx.step == null ? null : visit(ctx.step).asValue());
	}

	@Override
	public GioviObject visitListCreation(GioviParser.ListCreationContext ctx) {
		ListValue<Value<?>> list = new ListValue<>();
		for (GioviParser.ListEntryContext entryCtx : ctx.listEntries().listEntry()) {
			GioviObject entry = visit(entryCtx);
			switch(entry) {
				case Sequence<?> seq -> list.addAll(seq.getValues());
				case Value<?> v -> list.add(v);
				default -> throw new IncompatibleTypes(entry.getClass(), list.getClass());
			}
		}
		return list;
	}

	@Override
	public GioviObject visitMapCreation(GioviParser.MapCreationContext ctx) {
		MapValue<Value<?>, Value<?>> map = new MapValue<>();
		for (GioviParser.MapEntryContext entryCtx : ctx.mapEntries().mapEntry()) {
			map.put(visit(entryCtx.mapKey).asValue(), visit(entryCtx.mapValue).asValue());
		}
		return map;
	}

	@Override
	public GioviObject visitIntegerLiteral(GioviParser.IntegerLiteralContext ctx) {
		return new IntValue(Integer.parseInt(ctx.getText()));
	}

	@Override
	public GioviObject visitNaturalLiteral(GioviParser.NaturalLiteralContext ctx) {
		return new NaturalValue(new Natural(Integer.parseInt(ctx.getText())));
	}

	@Override
	public GioviObject visitRealLiteral(GioviParser.RealLiteralContext ctx) {
		return new RealValue(Double.parseDouble(ctx.getText()));
	}

	@Override
	public GioviObject visitBoolLiteral(GioviParser.BoolLiteralContext ctx) {
		return new BoolValue(Boolean.parseBoolean(ctx.getText()));
	}

	@Override
	public GioviObject visitTextLiteral(GioviParser.TextLiteralContext ctx) {
		return new TextValue((new Text(ctx.getText()).toString()));
	}

	@Override
	public GioviObject visitTextBlockLiteral(GioviParser.TextBlockLiteralContext ctx) {
		return new TextValue(new TextBlock(ctx.getText()).toString());
	}

	@Override
	public GioviObject visitExactTextLiteral(GioviParser.ExactTextLiteralContext ctx) {
		return new TextValue(new ExactText(ctx.getText()).toString());
	}

	@Override
	public GioviObject visitExactTextBlockLiteral(GioviParser.ExactTextBlockLiteralContext ctx) {
		return new TextValue(new ExactTextBlock(ctx.getText()).toString());
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