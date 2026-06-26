package it.simonvic.giovi.util;

import java.util.function.Supplier;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviBool;
import it.simonvic.giovi.GioviToken;
import it.simonvic.giovi.antlr.GioviParser.RedirectionPatternContext;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.exceptions.RedirectionException;

public class VisitorUtils {

	public static GioviParserVisitResult bitwiseNot(GioviParserVisitResult value) {
		return value.bitwiseNot();
	}

	public static GioviParserVisitResult bitwiseBool(
		GioviParserVisitResult lhs,
		GioviToken bop,
		GioviParserVisitResult rhs
	) {
		return switch (bop) {
			case BIT_AND -> lhs.bitwiseAnd(rhs);
			case BIT_OR -> lhs.bitwiseOr(rhs);
			case BIT_XOR -> lhs.bitwiseXor(rhs);
			default -> throw new OperatorNotSupported(lhs, bop, rhs);
		};
	}

	public static GioviParserVisitResult bitwiseShift(
		GioviParserVisitResult lhs,
		GioviToken bop,
		GioviParserVisitResult rhs
	) {
		return switch (bop) {
			case BIT_LSHIFT -> lhs.bitwiseLeftShift(rhs);
			case BIT_RSHIFT -> lhs.bitwiseRightShift(rhs);
			case BIT_RUSHIFT -> lhs.bitwiseRightUnsignedShift(rhs);
			default -> throw new OperatorNotSupported(lhs, bop, rhs);
		};
	}

	public static GioviParserVisitResult prefixed(GioviToken prefix, GioviParserVisitResult value) {
		return switch (prefix) {
			case PLUS -> value.positive();
			case MINUS -> value.negative();
			default -> throw new OperatorNotSupported(value, prefix);
		};
	}

	public static GioviParserVisitResult additive(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case PLUS -> left.plus(right);
			case MINUS -> left.minus(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult multiplicative(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case MULT -> left.multiply(right);
			case DIV -> left.divide(right);
			case MODULO -> left.modulo(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult relational(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case LESS_EQUAL -> left.lessEqual(right);
			case GREATER_EQUAL -> left.greaterEqual(right);
			case GREATER -> left.greater(right);
			case LESS -> left.less(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult bool(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case AND, AND_SYM -> left.and(right);
			case OR, OR_SYM -> left.or(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	@SuppressWarnings("incomplete-switch")
	public static GioviParserVisitResult bool(
		GioviParserVisitResult lhs,
		GioviToken operator,
		Supplier<GioviParserVisitResult> rhsVisit
	) {
		if (lhs instanceof GioviBool b) {
			switch (operator) {
				case AND, AND_SYM -> {
					if (!b.value()) {
						return new GioviBool(false);
					}
				}
				case OR, OR_SYM -> {
					if (b.value()) {
						return new GioviBool(true);
					}
				}
			}
		}
		return VisitorUtils.bool(lhs, operator, rhsVisit.get());
	}


	public static GioviParserVisitResult equality(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case EQUAL -> left.equal(right);
			case NOT_EQUAL -> left.notEqual(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult containment(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case CONTAINED, IN -> left.isContained(right);
			case CONTAINS -> left.contains(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult redirection(
		GioviParserVisitResult content,
		GioviToken operator,
		GioviParserVisitResult destination
	) {
		// TODO: if destination is constant (not a Var) throw error
		return switch (operator) {
			case REDIR, REDIR_RTL -> destination.redirection(content);
			case REDIR_PLUS, REDIR_PLUS_RTL -> destination.redirectionPlus(content);
			case REDIR_MINUS, REDIR_MINUS_RTL -> destination.redirectionMinus(content);
			case REDIR_MULT, REDIR_MULT_RTL -> destination.redirectionMultiply(content);
			case REDIR_DIV, REDIR_DIV_RTL -> destination.redirectionDivide(content);
			default -> throw new RedirectionException("Unknown redirection operator: " + operator);
		};
	}

	public static void redirectionToPrimitive(
		GioviParserVisitResult content,
		GioviToken operator,
		String destination
	) {
		switch (operator) {
			case REDIR, REDIR_RTL -> {
				switch (destination) {
					case "_out" -> System.out.println(content);
					case "_err" -> System.err.println(content);
					default -> throw new GioviException(
						"Unkown primitive destination " + destination
					);
				}
			}
			case REDIR_PLUS, REDIR_PLUS_RTL -> {
				switch (destination) {
					case "_out" -> System.out.print(content);
					case "_err" -> System.err.print(content);
					default -> throw new GioviException(
						"Unkown primitive destination " + destination
					);
				}
			}
			default -> throw new RedirectionException(
				"Operator not supported for primitive destination: " + operator
			);
		}
	}

	public static void redirectionToPattern(
		GioviParserVisitResult content,
		GioviToken operator,
		RedirectionPatternContext destination
	) {
		if (destination.literalPlaceholder() != null) {
			// do nothing
			return;
		}
		throw new GioviException(
			"Redirection not implemented yet for pattern " + destination.getText()
		);
	}

}
