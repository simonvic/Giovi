package it.simonvic.giovi.lang;

import it.simonvic.giovi.lang.values.GioviType;
import it.simonvic.giovi.lang.values.GioviValue;

public record GioviFunctionSignatureParameter(
	String identifier,
	GioviType<?> type,
	GioviValue<?> defaultValue,
	boolean isOptional)
	implements GioviParserVisitResult {

	public GioviFunctionSignatureParameter(
		String identifier,
		GioviType<?> type,
		GioviValue<?> defaultValue) {
		this(identifier, type, defaultValue, true);
	}

	public GioviFunctionSignatureParameter(String identifier, GioviType<?> type) {
		this(identifier, type, type.spawn(), false);
	}

}
