package it.simonvic.giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;

public record FunctionSignatureParam(
	String identifier,
	GioviType<?> type,
	GioviValue<?> param,
	boolean isOptional)
	implements GioviParserVisitResult {

	public FunctionSignatureParam(
		String identifier,
		GioviType<?> type,
		GioviValue<?> defaultValue) {
		this(identifier, type, defaultValue, true);
	}

	public FunctionSignatureParam(String identifier, GioviType<?> type) {
		this(identifier, type, type.spawn(), false);
	}

	@Override
	public String toString() {
		return String.format("%s %s%s", identifier, type, isOptional ? "?" + param : "");
	}

	public FunctionSignatureParam copy() {
		return new FunctionSignatureParam(identifier, type.copy(), param.copy(), isOptional);
	}

	public GioviValue<?> spawn() {
		return param.copy();
	}

}
