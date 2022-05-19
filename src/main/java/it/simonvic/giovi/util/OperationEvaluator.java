package it.simonvic.giovi.util;

import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.exceptions.RedirectionException;
import it.simonvic.giovi.lang.GioviObject;

public class OperationEvaluator {

	public static GioviObject prefixed(GioviObject value, String prefix) {
		return switch (prefix) {
			case "+" -> value.positive();
			case "-" -> value.negative();
			default -> throw new OperatorNotSupported(prefix);
		};
	}

	public static GioviObject additive(GioviObject left, GioviObject right, String operator) {
		return switch (operator) {
			case "+" -> left.plus(right);
			case "-" -> left.minus(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static GioviObject multiplicative(GioviObject left, GioviObject right, String operator) {
		return switch (operator) {
			case "*" -> left.multiply(right);
			case "/" -> left.divide(right);
			case "%" -> left.modulo(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static GioviObject relational(GioviObject left, GioviObject right, String operator) {
		return switch (operator) {
			case "<=" -> left.lessEqual(right);
			case ">=" -> left.greaterEqual(right);
			case ">" -> left.greater(right);
			case "<" -> left.less(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static GioviObject bool(GioviObject left, GioviObject right, String operator) {
		return switch (operator) {
			case "&" -> left.and(right);
			case "|" -> left.or(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static GioviObject equality(GioviObject left, GioviObject right, String operator) {
		return switch (operator) {
			case "=" -> left.equal(right);
			case "!=" -> left.notEqual(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static GioviObject containment(GioviObject left, GioviObject right, String operator) {
		return switch (operator) {
			case "~=" -> left.isContained(right);
			case "=~" -> left.contains(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static void redirection(GioviObject left, String operator, GioviObject right) {
		switch (operator) {
			case ">" -> left.redirection(right);
			case ">+" -> left.redirectionPlus(right);
			case ">-" -> left.redirectionMinus(right);
			case ">*" -> left.redirectionMultiply(right);
			case ">/" -> left.redirectionDivide(right);
			default -> throw new RedirectionException("Unknown redirection operator: " + operator);
		}
	}
}
