package it.simonvic.giovi.util;

import java.util.function.Supplier;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.exceptions.RedirectionException;
import it.simonvic.giovi.lang.GioviParserVisitResult;
import it.simonvic.giovi.lang.values.GioviBool;

public class OperationEvaluator {

	public static GioviParserVisitResult prefixed(String prefix, GioviParserVisitResult value) {
		return switch (prefix) {
			case "+" -> value.positive();
			case "-" -> value.negative();
			default -> throw new OperatorNotSupported(prefix);
		};
	}

	public static GioviParserVisitResult additive(GioviParserVisitResult left, String operator, GioviParserVisitResult right) {
		return switch (operator) {
			case "+" -> left.plus(right);
			case "-" -> left.minus(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static GioviParserVisitResult multiplicative(GioviParserVisitResult left, String operator, GioviParserVisitResult right) {
		return switch (operator) {
			case "*" -> left.multiply(right);
			case "/" -> left.divide(right);
			case "%" -> left.modulo(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static GioviParserVisitResult relational(GioviParserVisitResult left, String operator, GioviParserVisitResult right) {
		return switch (operator) {
			case "<=" -> left.lessEqual(right);
			case ">=" -> left.greaterEqual(right);
			case ">" -> left.greater(right);
			case "<" -> left.less(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static GioviParserVisitResult bool(GioviParserVisitResult left, String operator, GioviParserVisitResult right) {
		return switch (operator) {
			case "and", "&" -> left.and(right);
			case "or", "|" -> left.or(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static GioviParserVisitResult bool(
		GioviParserVisitResult lhs,
		String bop,
		Supplier<GioviParserVisitResult> rhsVisit) {
		if (lhs instanceof GioviBool b) {
			switch (bop) {
				case "and", "&" -> {
					if (!b.unbox()) {
						return new GioviBool(false);
					}
				}
				case "or", "|" -> {
					if (b.unbox()) {
						return new GioviBool(true);
					}
				}
			}
		}
		return OperationEvaluator.bool(lhs, bop, rhsVisit.get());
	}


	public static GioviParserVisitResult equality(GioviParserVisitResult left, String operator, GioviParserVisitResult right) {
		return switch (operator) {
			case "=" -> left.equal(right);
			case "!=" -> left.notEqual(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static GioviParserVisitResult containment(GioviParserVisitResult left, String operator, GioviParserVisitResult right) {
		return switch (operator) {
			case "~=" -> left.isContained(right);
			case "=~" -> left.contains(right);
			default -> throw new OperatorNotSupported(operator);
		};
	}

	public static void redirection(GioviParserVisitResult content, String operator, GioviParserVisitResult destination) {
		switch (operator) {
			case "->", "<-" -> destination.redirection(content);
			case "->+", "+<-" -> destination.redirectionPlus(content);
			case "->-", "-<-" -> destination.redirectionMinus(content);
			case "->*", "*<-" -> destination.redirectionMultiply(content);
			case "->/", "/<-" -> destination.redirectionDivide(content);
			default -> throw new RedirectionException("Unknown redirection operator: " + operator);
		}
	}

	// TODO: move to visitor
	public static void redirectionToPrimitive(
		GioviParserVisitResult content,
		String operator,
		String destination) {
		switch (operator) {
			case "->", "<-" -> {
				switch (destination) {
					case "\\out" -> System.out.println(content);
					case "\\err" -> System.err.println(content);
					default -> throw new GioviException(
						"Unkown primitive destination " + destination);
				}
			}
			case "->+", "+<-" -> {
				switch (destination) {
					case "\\out" -> System.out.print(content);
					case "\\err" -> System.err.print(content);
					default -> throw new GioviException(
						"Unkown primitive destination " + destination);
				}
			}
			default -> throw new RedirectionException(
				"Operator not supported for primitive destination: " + operator);
		}
	}
}
