package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.exceptions.RedirectionException;
import it.simonvic.giovi.exceptions.TypeNotFound;
import it.simonvic.giovi.lang.*;

import java.util.ArrayList;
import java.util.List;

public class GioviVisitor extends GioviParserBaseVisitor<Object> {

	private static final Block globalBlock = new Block(null);
	private static Block currentScopeBlock = globalBlock;

	public static Block getCurrentScope() {
		return currentScopeBlock;
	}

	@Override
	public Object visitIfStatement(GioviParser.IfStatementContext ctx) {
		if ((boolean) visit(ctx.expression())) {
			visit(ctx.ifBlock);
		} else if (ctx.ELSE() != null) {
			visit(ctx.elseBlock);
		}
		return null;
	}


	// lol
	private boolean shouldBreak = false;

	@Override
	public Object visitForStatement(GioviParser.ForStatementContext ctx) {
		Object expression = visit(ctx.expression());
		if (!(expression instanceof List<?> list)) {
			throw new OperatorNotSupported("for");
		} else {
			if (list.size() < 1) return null;
			String iteratorName = ctx.identifier().getText();
			currentScopeBlock.declareVariable(iteratorName, null);
			for (Object o : list) {
				currentScopeBlock.updateVariableValue(iteratorName, o);
				visit(ctx.block());
			}
		}
		return null;
	}

	@Override
	public Object visitBreakStatement(GioviParser.BreakStatementContext ctx) {
		shouldBreak = true;
		return null;
	}

	@Override
	public Object visitRepeatStatement(GioviParser.RepeatStatementContext ctx) {
		shouldBreak = false;
		if (ctx.IF() == null) {
			while (!shouldBreak) {
				visit(ctx.block());
			}
		} else {
			while (!shouldBreak && (boolean) visit(ctx.expression())) {
				visit(ctx.block());
			}

		}
		return null;
	}

	@Override
	public Object visitVarDeclaration(GioviParser.VarDeclarationContext ctx) {
		Variable<?> variable = switch (ctx.type().getText()) {
			case "Text" -> new Variable<>(ctx.identifier().getText(), "");
			case "Int" -> new Variable<>(ctx.identifier().getText(), 0);
			case "Bool" -> new Variable<>(ctx.identifier().getText(), false);
			case "Real" -> new Variable<>(ctx.identifier().getText(), 0.0);
			default -> throw new TypeNotFound(ctx.type().getText());
		};

		currentScopeBlock.declareVariable(variable.getIdentifier(), variable.getValue());
		return variable;
	}

	@Override
	public Object visitRedirectionToIdentifier(GioviParser.RedirectionToIdentifierContext ctx) {
		String identifier = ctx.identifier().getText();
		currentScopeBlock.updateVariableValue(identifier, switch (ctx.bop.getText()) {
			case ">" -> visit(ctx.expression());
			case ">+" -> Operation.add(currentScopeBlock.getVariableValue(identifier), visit(ctx.expression()));
			case ">-" -> Operation.subtract(currentScopeBlock.getVariableValue(identifier), visit(ctx.expression()));
			case ">*" -> Operation.multiply(currentScopeBlock.getVariableValue(identifier), visit(ctx.expression()));
			case ">/" -> Operation.divide(currentScopeBlock.getVariableValue(identifier), visit(ctx.expression()));
			default -> throw new RedirectionException("Unknown redirection operator: " + ctx.bop.getText());
		});
		return null;
	}

	@Override
	public Object visitRedirectionToVarDeclaration(GioviParser.RedirectionToVarDeclarationContext ctx) {
		currentScopeBlock.declareVariable(ctx.varDeclaration().identifier().getText(), visit(ctx.expression()));
		return null;
	}

	@Override
	public Object visitRedirectionToPrimitiveDestination(GioviParser.RedirectionToPrimitiveDestinationContext ctx) {
		Object content = visit(ctx.expression());
		switch (ctx.primitiveDestination().getText()) {
			case "\\", "\\out" -> System.out.println(content);
			case "\\error" -> System.err.println(content);
		}
		return null;
	}

	@Override
	public Object visitParenthesizedExpression(GioviParser.ParenthesizedExpressionContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public Object visitPrefixedExpression(GioviParser.PrefixedExpressionContext ctx) {
		Object value = visit(ctx.expression());
		return switch (ctx.prefix.getText()) {
			case "+" -> switch (value) {
				case Integer v -> v;
				case Double v -> v;
				default -> throw new OperatorNotSupported("+");
			};
			case "-" -> switch (value) {
				case Integer v -> -v;
				case Double v -> -v;
				default -> throw new OperatorNotSupported("-");
			};
			default -> throw new OperatorNotSupported(ctx.prefix.getText());
		};
	}

	@Override
	public Object visitAdditiveExpression(GioviParser.AdditiveExpressionContext ctx) {
		Object left = visit(ctx.expression(0));
		Object right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "+" -> Operation.add(left, right);
			case "-" -> Operation.subtract(left, right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public Object visitMultiplicativeExpression(GioviParser.MultiplicativeExpressionContext ctx) {
		Object left = visit(ctx.expression(0));
		Object right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "*" -> Operation.multiply(left, right);
			case "/" -> Operation.divide(left, right);
			case "%" -> Operation.modulo(left, right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public Object visitPowerExpression(GioviParser.PowerExpressionContext ctx) {
		return Operation.power(visit(ctx.expression(0)), visit(ctx.expression(1)));
	}

	@Override
	public Object visitRelationalExpression(GioviParser.RelationalExpressionContext ctx) {
		Object left = visit(ctx.expression(0));
		Object right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "<=" -> Operation.lessEqual(left, right);
			case ">=" -> Operation.greaterEqual(left, right);
			case ">" -> Operation.greater(left, right);
			case "<" -> Operation.less(left, right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public Object visitNotExpression(GioviParser.NotExpressionContext ctx) {
		Object expression = visit(ctx.expression());
		if (expression instanceof Boolean b) {
			return !b;
		}
		throw new OperatorNotSupported("!");
	}

	@Override
	public Object visitBoolExpression(GioviParser.BoolExpressionContext ctx) {
		Object left = visit(ctx.expression(0));
		Object right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		if (left instanceof Boolean l && right instanceof Boolean r) {
			return switch (operator) {
				case "&" -> l && r;
				case "|" -> l || r;
				default -> throw new OperatorNotSupported(operator);
			};
		}
		throw new OperatorNotSupported(operator);
	}

	@Override
	public Object visitEqualityExpression(GioviParser.EqualityExpressionContext ctx) {
		Object left = visit(ctx.expression(0));
		Object right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "=" -> Operation.equal(left, right);
			case "!=" -> Operation.notEqual(left, right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public Object visitContainmentExpression(GioviParser.ContainmentExpressionContext ctx) {
		Object left = visit(ctx.expression(0));
		Object right = visit(ctx.expression(1));
		String operator = ctx.bop.getText();
		return switch (operator) {
			case "~=" -> Operation.isContained(left, right);
			case "=~" -> Operation.contains(left, right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	@Override
	public Object visitIdentifier(GioviParser.IdentifierContext ctx) {
		return getCurrentScope().getVariableValue(ctx.getText());
	}

	@Override
	public Object visitSequence(GioviParser.SequenceContext ctx) {
		return Sequence.of(
			visit(ctx.start),
			visit(ctx.end),
			ctx.step == null ? null : visit(ctx.step));
	}

	@Override
	public Object visitListCreation(GioviParser.ListCreationContext ctx) {
		List<Object> list = new ArrayList<>();
		for (GioviParser.ListEntryContext entryCtx : ctx.listEntries().listEntry()) {
			Object entry = visit(entryCtx);
			if (entry instanceof List<?> l) {
				list.addAll(l);
			} else {
				list.add(entry);
			}
		}
		return list;
	}

	@Override
	public Object visitIntegerLiteral(GioviParser.IntegerLiteralContext ctx) {
		return Integer.parseInt(ctx.getText());
	}

	@Override
	public Object visitNaturalLiteral(GioviParser.NaturalLiteralContext ctx) {
		return Integer.parseInt(ctx.getText());
	}

	@Override
	public Object visitRealLiteral(GioviParser.RealLiteralContext ctx) {
		return Double.parseDouble(ctx.getText());
	}

	@Override
	public Object visitBoolLiteral(GioviParser.BoolLiteralContext ctx) {
		return Boolean.parseBoolean(ctx.getText());
	}

	@Override
	public Object visitTextLiteral(GioviParser.TextLiteralContext ctx) {
		return new Text(ctx.getText()).toString();
	}

	@Override
	public Object visitTextBlockLiteral(GioviParser.TextBlockLiteralContext ctx) {
		return new TextBlock(ctx.getText()).toString();
	}

	@Override
	public Object visitExactTextLiteral(GioviParser.ExactTextLiteralContext ctx) {
		return new ExactText(ctx.getText()).toString();
	}

	@Override
	public Object visitExactTextBlockLiteral(GioviParser.ExactTextBlockLiteralContext ctx) {
		return new ExactTextBlock(ctx.getText()).toString();
	}

	@Override
	public Object visitNullLiteral(GioviParser.NullLiteralContext ctx) {
		return null;
	}

	@Override
	public Object visitDefaultLiteral(GioviParser.DefaultLiteralContext ctx) {
		return "_";
	}

}