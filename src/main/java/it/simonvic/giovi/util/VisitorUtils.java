package it.simonvic.giovi.util;

import java.util.function.Supplier;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviBool;
import it.simonvic.giovi.GioviToken;
import it.simonvic.giovi.Op;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.exceptions.RedirectionException;

public class VisitorUtils {

	public static GioviParserVisitResult prefixed(GioviToken prefix, GioviParserVisitResult value) {
		return switch (prefix) {
			case PLUS -> Op.positive(value);
			case MINUS -> Op.negative(value);
			default -> throw new OperatorNotSupported(value, prefix);
		};
	}

	public static GioviParserVisitResult additive(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case PLUS -> Op.plus(left, right);
			case MINUS -> Op.minus(left, right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult multiplicative(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case MULT -> Op.multiply(left, right);
			case DIV -> Op.divide(left, right);
			case MODULO -> Op.modulo(left, right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult relational(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case LESS_EQUAL -> Op.lessEqual(left, right);
			case GREATER_EQUAL -> Op.greaterEqual(left, right);
			case GREATER -> Op.greater(left, right);
			case LESS -> Op.less(left, right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult bool(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case AND, AND_SYM -> Op.and(left, right);
			case OR, OR_SYM -> Op.or(left, right);
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
			case EQUAL -> Op.equal(left, right);
			case NOT_EQUAL -> Op.notEqual(left, right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult containment(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case CONTAINED, IN -> Op.isContained(left, right);
			case CONTAINS -> Op.contains(left, right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult redirection(
		GioviParserVisitResult content,
		GioviToken operator,
		GioviParserVisitResult destination
	) {
		return switch (operator) {
			case REDIR, REDIR_RTL -> Op.redirection(destination, content);
			case REDIR_PLUS, REDIR_PLUS_RTL -> Op.redirectionPlus(destination, content);
			case REDIR_MINUS, REDIR_MINUS_RTL -> Op.redirectionMinus(destination, content);
			case REDIR_MULT, REDIR_MULT_RTL -> Op.redirectionMultiply(destination, content);
			case REDIR_DIV, REDIR_DIV_RTL -> Op.redirectionDivide(destination, content);
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
						"Unkown primitive destination " + destination);
				}
			}
			case REDIR_PLUS, REDIR_PLUS_RTL -> {
				switch (destination) {
					case "_out" -> System.out.print(content);
					case "_err" -> System.err.print(content);
					default -> throw new GioviException(
						"Unkown primitive destination " + destination);
				}
			}
			default -> throw new RedirectionException(
				"Operator not supported for primitive destination: " + operator);
		}
	}

}
