package giovi;

import java.util.LinkedHashMap;
import java.util.List;

import giovi.lang.*;
import it.simonvic.giovi.GioviToken;
import it.simonvic.giovi.exceptions.OperatorNotSupported;

public interface GioviParserVisitResult {
	// @formatter:off

	// TODO: convert to interfaces
	
	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	default GioviParserVisitResult as(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.AS); }

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	default GioviParserVisitResult redirection(GioviParserVisitResult content) { throw new OperatorNotSupported(GioviToken.REDIR); }
	default GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.REDIR_PLUS); }
	default GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.REDIR_MINUS); }
	default GioviParserVisitResult redirectionMultiply(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.REDIR_MULT); }
	default GioviParserVisitResult redirectionDivide(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.REDIR_DIV); }

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	default GioviParserVisitResult positive() { throw new OperatorNotSupported(GioviToken.PLUS); }
	default GioviParserVisitResult negative() { throw new OperatorNotSupported(GioviToken.MINUS); }
	default GioviParserVisitResult plus(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.PLUS); }
	default GioviParserVisitResult minus(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.MINUS); }
	default GioviParserVisitResult multiply(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.MULT); }
	default GioviParserVisitResult divide(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.DIV); }
	default GioviParserVisitResult modulo(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.MODULO); }
	default GioviParserVisitResult power(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.EXPONENTIAL); }


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	default GioviParserVisitResult not() { throw new OperatorNotSupported(GioviToken.NOT_SYM); }
	default GioviParserVisitResult and(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.AND_SYM); }
	default GioviParserVisitResult or(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.OR_SYM); }

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	default GioviParserVisitResult equal(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.EQUAL); }
	default GioviParserVisitResult notEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.NOT_EQUAL); }
	default GioviParserVisitResult less(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.LESS); }
	default GioviParserVisitResult lessEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.LESS_EQUAL); }
	default GioviParserVisitResult greater(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.GREATER); }
	default GioviParserVisitResult greaterEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.GREATER_EQUAL); }
	default GioviParserVisitResult contains(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.CONTAINS); }
	default GioviParserVisitResult isContained(GioviParserVisitResult right) { return right.contains(this); }

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	default GioviParserVisitResult access(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.AT); }
	default GioviParserVisitResult dot(GioviParserVisitResult right) { throw new OperatorNotSupported(GioviToken.DOT); }

	//////////////////////////////////////////////////////////////////////////////////////////////// CASTS

	default GioviValue<?> asGioviValue() { return (GioviValue<?>) this; }
	default GioviType<?> asGioviType() { return (GioviType<?>) this; }
	default GioviTuple asGioviStruct() { return (GioviTuple) this; }
	default GioviList asGioviList() { return (GioviList) this; }
	default GioviMap asGioviMap() { return (GioviMap) this; }
	default GioviNumber<?> asGioviNumber() { return (GioviNumber<?>) this; }
	default GioviNatural asGioviNatural() { return asGioviNumber().naturalValue(); }
	default GioviInt asGioviInt() { return asGioviNumber().intValue(); }
	default GioviReal asGioviReal() { return asGioviNumber().realValue(); }
	default GioviBool asGioviBool() { return (GioviBool) this; }
	default GioviText asGioviText() { return (GioviText) this; }
	default GioviFunction asGioviFunction() { return (GioviFunction) this; }
	default Number asNativeNumber() { return asGioviNumber().unbox(); }
	default Natural asNativeNatural() { return asGioviNatural().unbox(); }
	default Integer asNativeInt() { return asGioviInt().unbox(); }
	default Double asNativeReal() { return asGioviReal().unbox(); }
	default Boolean asNativeBool() { return asGioviBool().unbox(); }
	default String asNativeText() { return asGioviText().unbox(); }
	default List<?> asNativeList() { return asGioviList().unbox(); }
	default LinkedHashMap<?, ?> asNativeMap() { return asGioviMap().unbox(); }
	default Function asNativeFunction() { return (Function) this; }

	// @formatter:on

}

