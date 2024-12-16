package it.simonvic.giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;

public record FormalParameter(
	String label,
	String identifier,
	GioviType<?> type,
	VarD<?> param,
	boolean isOptional
) implements GioviParserVisitResult {

	public FormalParameter(String identifier, GioviType<?> type, GioviValue<?> defaultValue) {
		this(identifier, identifier, type, new VarD<>(defaultValue, defaultValue.copy()), true);
	}

	public FormalParameter(String identifier, GioviValue<?> defaultValue) {
		this(identifier, defaultValue.getGioviType(), defaultValue);
	}

	public FormalParameter(String identifier, GioviType<?> type) {
		this(identifier, identifier, type, new VarD<>(type.spawn()), false);
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		if (!label.equals(identifier)) {
			sb.append(label).append(": ");
		}
		sb.append(identifier).append(" ").append(type);
		if (isOptional) {
			sb.append(" ? ").append(param);
		}
		return sb.toString();
	}

	public FormalParameter copy() {
		return new FormalParameter(label, identifier, type.copy(), param.copy(), isOptional);
	}

	public VarD<?> spawn() {
		return param.copy();
	}

}
