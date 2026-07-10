package giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;

public record RecordFieldSignature(
	String identifier,
	GioviType<?> type,
	GioviValue<?> defaultValue
) implements ProductFieldSignature {

	public RecordFieldSignature(String identifier, GioviType<?> type) {
		this(identifier, type, type.spawn());
	}

	public RecordFieldSignature copy() {
		return new RecordFieldSignature(identifier, type.copy(), defaultValue.copy());
	}

}

