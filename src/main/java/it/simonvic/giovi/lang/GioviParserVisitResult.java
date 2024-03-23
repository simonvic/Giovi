package it.simonvic.giovi.lang;

import java.util.LinkedHashMap;
import java.util.List;

import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.lang.values.natives.Natural;

public interface GioviParserVisitResult {
	// @formatter:off

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS
	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	default GioviParserVisitResult redirection(GioviParserVisitResult content) { throw new OperatorNotSupported("->"); }
	default GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) { throw new OperatorNotSupported("->+"); }
	default GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) { throw new OperatorNotSupported("->-"); }
	default GioviParserVisitResult redirectionMultiply(GioviParserVisitResult right) { throw new OperatorNotSupported("->*"); }
	default GioviParserVisitResult redirectionDivide(GioviParserVisitResult right) { throw new OperatorNotSupported("->/"); }

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	default GioviParserVisitResult positive() { throw new OperatorNotSupported("+"); }
	default GioviParserVisitResult negative() { throw new OperatorNotSupported("-"); }
	default GioviParserVisitResult plus(GioviParserVisitResult right) { throw new OperatorNotSupported("+"); }
	default GioviParserVisitResult minus(GioviParserVisitResult right) { throw new OperatorNotSupported("-"); }
	default GioviParserVisitResult multiply(GioviParserVisitResult right) { throw new OperatorNotSupported("*"); }
	default GioviParserVisitResult divide(GioviParserVisitResult right) { throw new OperatorNotSupported("/"); }
	default GioviParserVisitResult modulo(GioviParserVisitResult right) { throw new OperatorNotSupported("%"); }
	default GioviParserVisitResult power(GioviParserVisitResult right) { throw new OperatorNotSupported("^"); }


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	default GioviParserVisitResult not() { throw new OperatorNotSupported("!"); }
	default GioviParserVisitResult and(GioviParserVisitResult right) { throw new OperatorNotSupported("&"); }
	default GioviParserVisitResult or(GioviParserVisitResult right) { throw new OperatorNotSupported("|"); }

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	default GioviParserVisitResult equal(GioviParserVisitResult right) { throw new OperatorNotSupported("="); }
	default GioviParserVisitResult notEqual(GioviParserVisitResult right) { throw new OperatorNotSupported("!="); }
	default GioviParserVisitResult less(GioviParserVisitResult right) { throw new OperatorNotSupported("<"); }
	default GioviParserVisitResult lessEqual(GioviParserVisitResult right) { throw new OperatorNotSupported("<="); }
	default GioviParserVisitResult greater(GioviParserVisitResult right) { throw new OperatorNotSupported(">"); }
	default GioviParserVisitResult greaterEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(">="); }
	default GioviParserVisitResult contains(GioviParserVisitResult right) { throw new OperatorNotSupported("=~"); }
	default GioviParserVisitResult isContained(GioviParserVisitResult right) { return right.contains(this); }

	// TODO: not contain

	//////////////////////////////////////////////////////////////////////////////////////////////// access
	default GioviParserVisitResult access(GioviParserVisitResult right) { throw new OperatorNotSupported("@"+right); }
	default GioviParserVisitResult dot(GioviParserVisitResult right) { throw new OperatorNotSupported(". (dot)"); }

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
	default Number asNativeNumber() { return asGioviNumber().unbox(); }
	default Natural asNativeNatural() { return asGioviNatural().unbox(); }
	default Integer asNativeInt() { return asGioviInt().unbox(); }
	default Double asNativeReal() { return asGioviReal().unbox(); }
	default Boolean asNativeBool() { return asGioviBool().unbox(); }
	default String asNativeText() { return asGioviText().unbox(); }
	default List<?> asNativeList() { return asGioviList().unbox(); }
	default LinkedHashMap<?, ?> asNativeMap() { return asGioviMap().unbox(); }

	// @formatter:on

}

