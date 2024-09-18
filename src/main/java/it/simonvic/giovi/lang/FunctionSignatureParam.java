package it.simonvic.giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.VarD;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;

public record FunctionSignatureParam(
	String identifier,
	GioviType<?> type,
	VarD<?> param,
	boolean isOptional)
	implements GioviParserVisitResult {

	public FunctionSignatureParam(
		String identifier,
		GioviType<?> type,
		GioviValue<?> defaultValue) {
		this(identifier, type, new VarD<>(defaultValue, defaultValue.copy()), true);
	}

	public FunctionSignatureParam(
		String identifier,
		GioviValue<?> defaultValue) {
		this(identifier, defaultValue.getGioviType(), defaultValue);
	}

	public FunctionSignatureParam(String identifier, GioviType<?> type) {
		this(identifier, type, new VarD<>(type.spawn()), false);
	}

	@Override
	public String toString() {
		return String.format("%s %s%s", identifier, type, isOptional ? "?" + param : "");
	}

	public FunctionSignatureParam copy() {
		return new FunctionSignatureParam(identifier, type.copy(), param.copy(), isOptional);
	}

	public VarD<?> spawn() {
		return param.copy();
	}

}
