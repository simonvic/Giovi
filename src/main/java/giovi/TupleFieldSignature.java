package giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;

public record TupleFieldSignature(
	GioviType<?> type,
	GioviValue<?> defaultValue
) implements FieldSignature {

	public TupleFieldSignature(GioviType<?> type) {
		this(type, type.spawn());
	}

	public TupleFieldSignature copy() {
		return new TupleFieldSignature(type.copy(), defaultValue.copy());
	}

}

