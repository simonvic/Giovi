package giovi;

import java.util.LinkedHashMap;
import java.util.List;

import giovi.lang.*;
import it.simonvic.giovi.Var;

public interface GioviParserVisitResult {
	// @formatter:off

	// TODO: add toText() and keep toString() conventional

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
	default Var<?> asVar() { return (Var<?>) this; }
	// @formatter:on

	/**
	 * To be used ONLY when we know we can discard/ignore the default value of a variable
	 */
	default GioviValue<?> unwrapAsGioviValue() {
		return switch (this) {
			case Var<?> v -> v.value();
			default -> (GioviValue<?>) this;
		};
	}


}

