package it.simonvic.giovi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import giovi.GioviParserVisitResult;
import giovi.JFunction;
import giovi.MemberKey;
import giovi.lang.GioviBool;
import giovi.lang.GioviFunction;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.Func;

public sealed interface Op extends GioviParserVisitResult {

	// TODO: move 'this instanceof Var' here?
	// TODO: generic interfaces?
	// TODO: add implementation groups (relational, arithmetic etc)
	// TODO: still need to find a better way to handle Vars... Maybe handle unwrapping outside of here?

	non-sealed interface As extends Op {

		default GioviParserVisitResult as(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> as(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.AS, right);
			};
		}

	}

	non-sealed interface Redirection extends Op {

		default GioviParserVisitResult redirection(GioviParserVisitResult content) {
			return switch (content) {
				case Var<?> v -> redirection(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.REDIR, content);
			};
		}

	}

	non-sealed interface RedirectionPlus extends Op {

		default GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) {
			return switch (content) {
				case Var<?> v -> redirectionPlus(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.REDIR_PLUS, content);
			};
		}

	}

	non-sealed interface RedirectionMinus extends Op {

		default GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) {
			return switch (content) {
				case Var<?> v -> redirectionMinus(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.REDIR_MINUS, content);
			};
		}

	}

	non-sealed interface RedirectionMultiply extends Op {

		default GioviParserVisitResult redirectionMultiply(GioviParserVisitResult content) {
			return switch (content) {
				case Var<?> v -> redirectionMultiply(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.REDIR_MULT, content);
			};
		}

	}

	non-sealed interface RedirectionDivide extends Op {

		default GioviParserVisitResult redirectionDivide(GioviParserVisitResult content) {
			return switch (content) {
				case Var<?> v -> redirectionDivide(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.REDIR_DIV, content);
			};
		}

	}

	non-sealed interface Positive extends Op {

		default GioviParserVisitResult positive() {
			throw new OperatorNotSupported(this, GioviToken.PLUS);
		}

	}

	non-sealed interface Negative extends Op {

		default GioviParserVisitResult negative() {
			throw new OperatorNotSupported(this, GioviToken.MINUS);
		}

	}

	non-sealed interface Plus extends Op {

		default GioviParserVisitResult plus(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> plus(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.PLUS, right);
			};
		}

	}

	non-sealed interface Minus extends Op {

		default GioviParserVisitResult minus(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> minus(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.MINUS, right);
			};
		}

	}

	non-sealed interface Multiply extends Op {

		default GioviParserVisitResult multiply(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> multiply(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.MULT, right);
			};
		}

	}

	non-sealed interface Divide extends Op {

		default GioviParserVisitResult divide(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> divide(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.DIV, right);
			};
		}

	}

	non-sealed interface Modulo extends Op {

		default GioviParserVisitResult modulo(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> modulo(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.MODULO, right);
			};
		}

	}

	non-sealed interface Power extends Op {

		default GioviParserVisitResult power(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> power(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.EXPONENTIAL, right);
			};
		}

	}

	non-sealed interface Not extends Op {

		default GioviParserVisitResult not() {
			throw new OperatorNotSupported(this, GioviToken.NOT_SYM);
		}

	}

	non-sealed interface And extends Op {

		default GioviParserVisitResult and(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> and(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.AND_SYM, right);
			};
		}

	}

	non-sealed interface Or extends Op {

		default GioviParserVisitResult or(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> or(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.OR_SYM, right);
			};
		}

	}

	non-sealed interface Equal extends Op {

		default GioviBool equal(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> equal(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.EQUAL, right);
			};
		}

	}

	non-sealed interface NotEqual extends Op {

		default GioviBool notEqual(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> notEqual(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.NOT_EQUAL, right);
			};
		}

	}

	non-sealed interface Less extends Op {

		default GioviParserVisitResult less(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> less(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.LESS, right);
			};
		}

	}

	non-sealed interface LessEqual extends Op {

		default GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> lessEqual(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.LESS_EQUAL, right);
			};
		}

	}

	non-sealed interface Greater extends Op {

		default GioviParserVisitResult greater(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> greater(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.GREATER, right);
			};
		}

	}

	non-sealed interface GreaterEqual extends Op {

		default GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> greaterEqual(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.GREATER_EQUAL, right);
			};
		}

	}

	non-sealed interface Contains extends Op {

		default GioviParserVisitResult contains(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> contains(v.value());
				default -> throw new OperatorNotSupported(this, GioviToken.CONTAINS, right);
			};
		}

	}

	non-sealed interface IsContained extends Op {

		default GioviParserVisitResult isContained(GioviParserVisitResult right) {
			return Op.contains(right, this);
		}

	}

	non-sealed interface Access extends Op {

		default GioviParserVisitResult access(GioviParserVisitResult right) {
			throw new OperatorNotSupported(this, GioviToken.AT, right);
		}

	}

	non-sealed interface Dot extends Op {

		default GioviParserVisitResult dot(GioviParserVisitResult right) {
			return switch (right) {
				case Var<?> v -> dot(v.value());
				case MemberKey(var identifier) -> {
					// Field
					try {
						Field field = this.getClass().getField(identifier);
						if (field.isAnnotationPresent(Const.class)
							&& field.get(this) instanceof GioviParserVisitResult v) {
							yield v;
						}
					} catch (NoSuchFieldException
						| IllegalArgumentException
						| IllegalAccessException _) {
					}

					// Method
					for (Method method : this.getClass().getMethods()) {
						if (method.getName().equals(identifier)
							&& method.isAnnotationPresent(Func.class)) {
							// TODO: fails on overloaded methods
							yield new GioviFunction(new JFunction(method));
						}
					}

					throw new GioviException("No member with name: " + identifier);
				}
				default -> throw new OperatorNotSupported(this, GioviToken.DOT, right);
			};
		}

	}

	interface All extends As, Redirection, RedirectionPlus, RedirectionMinus, RedirectionMultiply,
		RedirectionDivide, Positive, Negative, Plus, Minus, Multiply, Divide, Modulo, Power, Not,
		And, Or, Equal, NotEqual, Less, LessEqual, Greater, GreaterEqual, Contains, IsContained,
		Access, Dot {
	}

	public static GioviParserVisitResult as(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof As l) {
			return l.as(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.AS, rhs);
	}

	public static GioviParserVisitResult redirection(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Redirection l) {
			return l.redirection(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.REDIR, rhs);
	}

	public static GioviParserVisitResult redirectionPlus(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof RedirectionPlus l) {
			return l.redirectionPlus(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.REDIR_PLUS, rhs);
	}

	public static GioviParserVisitResult redirectionMinus(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof RedirectionMinus l) {
			return l.redirectionMinus(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.REDIR_MINUS, rhs);
	}

	public static GioviParserVisitResult redirectionMultiply(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof RedirectionMultiply l) {
			return l.redirectionMultiply(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.REDIR_MULT, rhs);
	}

	public static GioviParserVisitResult redirectionDivide(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof RedirectionDivide l) {
			return l.redirectionDivide(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.REDIR_DIV, rhs);
	}

	public static GioviParserVisitResult positive(GioviParserVisitResult operand) {
		if (operand instanceof Positive o) {
			return o.positive();
		}
		throw new OperatorNotSupported(operand, GioviToken.PLUS);
	}

	public static GioviParserVisitResult negative(GioviParserVisitResult operand) {
		if (operand instanceof Negative o) {
			return o.negative();
		}
		throw new OperatorNotSupported(operand, GioviToken.MINUS);
	}

	public static GioviParserVisitResult plus(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Plus l) {
			return l.plus(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.PLUS, rhs);
	}

	public static GioviParserVisitResult minus(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Minus l) {
			return l.minus(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.MINUS, rhs);
	}

	public static GioviParserVisitResult multiply(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Multiply l) {
			return l.multiply(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.MULT, rhs);
	}

	public static GioviParserVisitResult divide(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Divide l) {
			return l.divide(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.DIV, rhs);
	}

	public static GioviParserVisitResult modulo(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Modulo l) {
			return l.modulo(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.MODULO, rhs);
	}

	public static GioviParserVisitResult power(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Power l) {
			return l.power(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.EXPONENTIAL, rhs);
	}

	public static GioviParserVisitResult not(GioviParserVisitResult operand) {
		if (operand instanceof Not o) {
			return o.not();
		}
		throw new OperatorNotSupported(operand, GioviToken.NOT);
	}

	public static GioviParserVisitResult and(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof And l) {
			return l.and(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.AND, rhs);
	}

	public static GioviParserVisitResult or(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Or l) {
			return l.or(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.OR, rhs);
	}

	public static GioviBool equal(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Equal l) {
			return l.equal(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.EQUAL, rhs);
	}

	public static GioviBool notEqual(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof NotEqual l) {
			return l.notEqual(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.NOT_EQUAL, rhs);
	}

	public static GioviParserVisitResult less(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Less l) {
			return l.less(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.LESS, rhs);
	}

	public static GioviParserVisitResult lessEqual(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof LessEqual l) {
			return l.lessEqual(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.LESS_EQUAL, rhs);
	}

	public static GioviParserVisitResult greater(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Greater l) {
			return l.greater(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.GREATER, rhs);
	}

	public static GioviParserVisitResult greaterEqual(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof GreaterEqual l) {
			return l.greaterEqual(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.GREATER_EQUAL, rhs);
	}

	public static GioviParserVisitResult contains(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Contains l) {
			return l.contains(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.CONTAINS, rhs);
	}

	public static GioviParserVisitResult isContained(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		return contains(rhs, lhs);
	}

	public static GioviParserVisitResult access(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Access l) {
			return l.access(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.AT, rhs);
	}

	public static GioviParserVisitResult dot(
		GioviParserVisitResult lhs,
		GioviParserVisitResult rhs
	) {
		if (lhs instanceof Dot l) {
			return l.dot(rhs);
		}
		throw new OperatorNotSupported(lhs, GioviToken.DOT, rhs);
	}

}
