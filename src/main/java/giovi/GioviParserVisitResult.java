package giovi;

import java.util.LinkedHashMap;
import java.util.List;

import giovi.lang.*;
import it.simonvic.giovi.ValueWrapper;
import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.exceptions.OperatorNotSupported;

public interface GioviParserVisitResult {
	// @formatter:off

	// TODO: convert to interfaces
	// TODO: use intersections <T extends Serializable & Comparable<T>>
	// TODO: add toText() and keep toString() conventional

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	// TODO: add typecheck and pattern matching operator?

	default GioviParserVisitResult as(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.AS, right); }

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	// Redirection methods will produce a new value based on the assignement
	default GioviParserVisitResult redirection(GioviParserVisitResult content) { throw new OperatorNotSupported(this, GioviLexer.REDIRECTION, content); }
	default GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) { return redirection(plus(content)); }
	default GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) { return redirection(minus(content)); }
	default GioviParserVisitResult redirectionMultiply(GioviParserVisitResult content) { return redirection(multiply(content)); }
	default GioviParserVisitResult redirectionDivide(GioviParserVisitResult content) { return redirection(divide(content)); }

	//////////////////////////////////////////////////////////////////////////////////////////////// bitwise

	default GioviParserVisitResult bitwiseNot() { throw new OperatorNotSupported(this, GioviLexer.BIT_NOT); }
	default GioviParserVisitResult bitwiseAnd(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.BIT_AND, right); }
	default GioviParserVisitResult bitwiseOr(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.BIT_OR, right); }
	default GioviParserVisitResult bitwiseXor(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.BIT_XOR, right); }
	default GioviParserVisitResult bitwiseLeftShift(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.BIT_LSHIFT, right); }
	default GioviParserVisitResult bitwiseRightShift(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.BIT_RSHIFT, right); }
	default GioviParserVisitResult bitwiseRightUnsignedShift(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.BIT_RUSHIFT, right); }

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	default GioviParserVisitResult positive() { throw new OperatorNotSupported(this, GioviLexer.PLUS); }
	default GioviParserVisitResult negative() { throw new OperatorNotSupported(this, GioviLexer.MINUS); }
	default GioviParserVisitResult plus(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.PLUS, right); }
	default GioviParserVisitResult minus(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.MINUS, right); }
	default GioviParserVisitResult multiply(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.MULTIPLICATION, right); }
	default GioviParserVisitResult divide(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.DIVISION, right); }
	default GioviParserVisitResult modulo(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.MODULO, right); }
	default GioviParserVisitResult power(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.EXPONENTIAL, right); }


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	default GioviParserVisitResult not() { throw new OperatorNotSupported(this, GioviLexer.NOT_SYM); }
	default GioviParserVisitResult and(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.AND_SYM, right); }
	default GioviParserVisitResult or(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.OR_SYM, right); }

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	default GioviParserVisitResult equal(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.EQUAL, right); }
	default GioviParserVisitResult notEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.NOT_EQUAL, right); }
	default GioviParserVisitResult less(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.BRACKET_ANGULAR_OPEN, right); }
	default GioviParserVisitResult lessEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.LESS_EQUAL, right); }
	default GioviParserVisitResult greater(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.BRACKET_ANGULAR_CLOSE, right); }
	default GioviParserVisitResult greaterEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.GREATER_EQUAL, right); }
	default GioviParserVisitResult contains(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.EQUAL_TILDE, right); }
	default GioviParserVisitResult isContained(GioviParserVisitResult right) { return right.contains(this); }

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	default GioviParserVisitResult dot(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.DOT, right); }
	default GioviParserVisitResult dotRef(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviLexer.REF, right); }

	//////////////////////////////////////////////////////////////////////////////////////////////// CASTS

	default GioviValue<?> asGioviValue() { return (GioviValue<?>) this; }
	default GioviType<?> asGioviType() { return (GioviType<?>) this; }
	default GioviTuple asGioviTuple() { return (GioviTuple) this; }
	default GioviStruct asGioviStruct() { return (GioviStruct) this; }
	default GioviList asGioviList() { return (GioviList) this; }
	default GioviMap asGioviMap() { return (GioviMap) this; }
	default GioviNumber<?> asGioviNumber() { return (GioviNumber<?>) this; }
	default GioviNatural asGioviNatural() { return (GioviNatural) this; }
	default GioviInt asGioviInt() { return (GioviInt) this; }
	default GioviReal asGioviReal() { return (GioviReal) this; }
	default GioviBool asGioviBool() { return (GioviBool) this; }
	default GioviText asGioviText() { return (GioviText) this; }
	default GioviFunction asGioviFunction() { return (GioviFunction) this; }
	default Number asNativeNumber() { return asGioviNumber().value(); }
	default Natural asNativeNatural() { return asGioviNatural().value(); }
	default Integer asNativeInt() { return asGioviInt().value(); }
	default Double asNativeReal() { return asGioviReal().value(); }
	default Boolean asNativeBool() { return asGioviBool().value(); }
	default String asNativeText() { return asGioviText().value(); }
	default List<?> asNativeList() { return asGioviList().value(); }
	default LinkedHashMap<?, ?> asNativeMap() { return asGioviMap().value(); }
	default Function asNativeFunction() { return asGioviFunction().value(); }

	/**
	 * Get the underlying GioviValue.
	 * To be used ONLY when we know we can discard/ignore the default value of a variable
	 * @return
	 */
	default GioviValue<?> unwrapAsGioviValue() {
		return switch (this) {
			case ValueWrapper<?> v -> v.unwrap();
			default -> (GioviValue<?>) this;
		};
	}

	// @formatter:on

}

