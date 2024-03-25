package it.simonvic.giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;

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
