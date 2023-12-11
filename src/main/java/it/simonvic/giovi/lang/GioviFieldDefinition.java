package it.simonvic.giovi.lang;

import it.simonvic.giovi.lang.values.GioviType;
import it.simonvic.giovi.lang.values.GioviValue;

public record GioviFieldDefinition(
	String identifier,
	GioviType<?> type,
	GioviValue<?> defaultValue) {

	public GioviFieldDefinition(String identifier, GioviType<?> type) {
		this(identifier, type, type.spawn());
	}

	@Override
	public String toString() {
		return identifier + " " + type + " default " + defaultValue;
	}

}
