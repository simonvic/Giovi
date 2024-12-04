package giovi;

import java.util.LinkedHashMap;
import java.util.List;

import giovi.lang.*;
import it.simonvic.giovi.GioviToken;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.OperatorNotSupported;

public interface GioviParserVisitResult {
	// @formatter:off

	// TODO: convert to interfaces
	
	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	default GioviParserVisitResult as(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.AS, right); }

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	// Redirection methods will produce a new value based on the assignement
	default GioviParserVisitResult redirection(GioviParserVisitResult content) { throw new OperatorNotSupported(this, GioviToken.REDIR, content); }
	default GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) { throw new OperatorNotSupported(this, GioviToken.REDIR_PLUS, content); }
	default GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) { throw new OperatorNotSupported(this, GioviToken.REDIR_MINUS, content); }
	default GioviParserVisitResult redirectionMultiply(GioviParserVisitResult content) { throw new OperatorNotSupported(this, GioviToken.REDIR_MULT, content); }
	default GioviParserVisitResult redirectionDivide(GioviParserVisitResult content) { throw new OperatorNotSupported(this, GioviToken.REDIR_DIV, content); }

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	default GioviParserVisitResult positive() { throw new OperatorNotSupported(this, GioviToken.PLUS); }
	default GioviParserVisitResult negative() { throw new OperatorNotSupported(this, GioviToken.MINUS); }
	default GioviParserVisitResult plus(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.PLUS, right); }
	default GioviParserVisitResult minus(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.MINUS, right); }
	default GioviParserVisitResult multiply(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.MULT, right); }
	default GioviParserVisitResult divide(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.DIV, right); }
	default GioviParserVisitResult modulo(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.MODULO, right); }
	default GioviParserVisitResult power(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.EXPONENTIAL, right); }


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	default GioviParserVisitResult not() { throw new OperatorNotSupported(this, GioviToken.NOT_SYM); }
	default GioviParserVisitResult and(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.AND_SYM, right); }
	default GioviParserVisitResult or(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.OR_SYM, right); }

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	default GioviParserVisitResult equal(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.EQUAL, right); }
	default GioviParserVisitResult notEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.NOT_EQUAL, right); }
	default GioviParserVisitResult less(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.LESS, right); }
	default GioviParserVisitResult lessEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.LESS_EQUAL, right); }
	default GioviParserVisitResult greater(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.GREATER, right); }
	default GioviParserVisitResult greaterEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.GREATER_EQUAL, right); }
	default GioviParserVisitResult contains(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.CONTAINS, right); }
	default GioviParserVisitResult isContained(GioviParserVisitResult right) { return right.contains(this); }

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	default GioviParserVisitResult access(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.AT, right); }
	default GioviParserVisitResult dot(GioviParserVisitResult right) { throw new OperatorNotSupported(this, GioviToken.DOT, right); }

	//////////////////////////////////////////////////////////////////////////////////////////////// CASTS

	default GioviValue<?> asGioviValue() { return (GioviValue<?>) this; }
	default GioviType<?> asGioviType() { return (GioviType<?>) this; }
	default GioviTuple asGioviTuple() { return (GioviTuple) this; }
	default GioviStruct asGioviStruct() { return (GioviStruct) this; }
	default GioviList asGioviList() { return (GioviList) this; }
	default GioviMap asGioviMap() { return (GioviMap) this; }
	default GioviNumber<?> asGioviNumber() { return (GioviNumber<?>) this; }
	default GioviNatural asGioviNatural() { return asGioviNumber().naturalValue(); }
	default GioviInt asGioviInt() { return asGioviNumber().intValue(); }
	default GioviReal asGioviReal() { return asGioviNumber().realValue(); }
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
	default Var<?> asVar() { return (Var<?>) this; }

	/**
	 * To be used ONLY when we know we can discard/ignore the default value of a variable
	 */
	default GioviValue<?> unwrapAsGioviValue() {
		return switch (this) {
			case Var<?> v -> v.value();
			default -> (GioviValue<?>) this;
		};
	}

	// @formatter:on

}

