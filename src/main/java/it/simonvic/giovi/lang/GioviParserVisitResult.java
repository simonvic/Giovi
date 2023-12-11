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

	public default GioviParserVisitResult redirection(GioviParserVisitResult content) { throw new OperatorNotSupported("->"); }
	public default GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) { throw new OperatorNotSupported("->+"); }
	public default GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) { throw new OperatorNotSupported("->-"); }
	public default GioviParserVisitResult redirectionMultiply(GioviParserVisitResult right) { throw new OperatorNotSupported("->*"); }
	public default GioviParserVisitResult redirectionDivide(GioviParserVisitResult right) { throw new OperatorNotSupported("->/"); }

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	public default GioviParserVisitResult positive() { throw new OperatorNotSupported("+"); }
	public default GioviParserVisitResult negative() { throw new OperatorNotSupported("-"); }
	public default GioviParserVisitResult plus(GioviParserVisitResult right) { throw new OperatorNotSupported("+"); }
	public default GioviParserVisitResult minus(GioviParserVisitResult right) { throw new OperatorNotSupported("-"); }
	public default GioviParserVisitResult multiply(GioviParserVisitResult right) { throw new OperatorNotSupported("*"); }
	public default GioviParserVisitResult divide(GioviParserVisitResult right) { throw new OperatorNotSupported("/"); }
	public default GioviParserVisitResult modulo(GioviParserVisitResult right) { throw new OperatorNotSupported("%"); }
	public default GioviParserVisitResult power(GioviParserVisitResult right) { throw new OperatorNotSupported("^"); }


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	public default GioviParserVisitResult not() { throw new OperatorNotSupported("!"); }
	public default GioviParserVisitResult and(GioviParserVisitResult right) { throw new OperatorNotSupported("&"); }
	public default GioviParserVisitResult or(GioviParserVisitResult right) { throw new OperatorNotSupported("|"); }

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	public default GioviParserVisitResult equal(GioviParserVisitResult right) { throw new OperatorNotSupported("="); }
	public default GioviParserVisitResult notEqual(GioviParserVisitResult right) { throw new OperatorNotSupported("!="); }
	public default GioviParserVisitResult less(GioviParserVisitResult right) { throw new OperatorNotSupported("<"); }
	public default GioviParserVisitResult lessEqual(GioviParserVisitResult right) { throw new OperatorNotSupported("<="); }
	public default GioviParserVisitResult greater(GioviParserVisitResult right) { throw new OperatorNotSupported(">"); }
	public default GioviParserVisitResult greaterEqual(GioviParserVisitResult right) { throw new OperatorNotSupported(">="); }
	public default GioviParserVisitResult contains(GioviParserVisitResult right) { throw new OperatorNotSupported("=~"); }
	public default GioviParserVisitResult isContained(GioviParserVisitResult right) { return right.contains(this); }

	//////////////////////////////////////////////////////////////////////////////////////////////// access
	public default GioviParserVisitResult access(GioviParserVisitResult right) { throw new OperatorNotSupported("@"+right); }
	public default GioviParserVisitResult dot(GioviParserVisitResult right) { throw new OperatorNotSupported(". (dot)"); }

	//////////////////////////////////////////////////////////////////////////////////////////////// CASTS

	public default GioviValue<?> asGioviValue() { return (GioviValue<?>) this; }
	public default GioviType<?> asGioviType() { return (GioviType<?>) this; }
	public default GioviTuple asGioviStruct() { return (GioviTuple) this; }
	public default GioviList asGioviList() { return (GioviList) this; }
	public default GioviMap asGioviMap() { return (GioviMap) this; }
	public default GioviNumber<?> asGioviNumber() { return (GioviNumber<?>) this; }
	public default GioviNatural asGioviNatural() { return asGioviNumber().naturalValue(); }
	public default GioviInt asGioviInt() { return asGioviNumber().intValue(); }
	public default GioviReal asGioviReal() { return asGioviNumber().realValue(); }
	public default GioviBool asGioviBool() { return (GioviBool) this; }
	public default GioviText asGioviText() { return (GioviText) this; }
	public default Number asNativeNumber() { return asGioviNumber().unbox(); }
	public default Natural asNativeNatural() { return asGioviNatural().unbox(); }
	public default Integer asNativeInt() { return asGioviInt().unbox(); }
	public default Double asNativeReal() { return asGioviReal().unbox(); }
	public default Boolean asNativeBool() { return asGioviBool().unbox(); }
	public default String asNativeText() { return asGioviText().unbox(); }
	public default List<?> asNativeList() { return asGioviList().unbox(); }
	public default LinkedHashMap<?, ?> asNativeMap() { return asGioviMap().unbox(); }

	// @formatter:on

}

