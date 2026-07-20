package it.simonvic.giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;

public record FormalParameter(
	String label,
	String identifier,
	GioviType<?> type,
	GioviValue<?> defaultValue,
	boolean isOptional
) implements GioviParserVisitResult {

	public FormalParameter(String identifier, GioviType<?> type, GioviValue<?> defaultValue) {
		this(identifier, identifier, type, defaultValue, true);
	}

	public FormalParameter(String identifier, GioviValue<?> defaultValue) {
		this(identifier, identifier, defaultValue.getGioviType(), defaultValue, true);
	}

	public FormalParameter(String identifier, GioviType<?> type) {
		this(identifier, identifier, type, type.spawn(), false);
	}

	public FormalParameter(String label, String identifier, GioviType<?> type) {
		this(label, identifier, type, type.spawn(), false);
	}

	public FormalParameter(String label, String identifier, GioviValue<?> defaultValue) {
		this(label, identifier, defaultValue.getGioviType(), defaultValue, true);
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		if (!label.equals(identifier)) {
			sb.append(label).append(": ");
		}
		sb.append(identifier).append(" ").append(type);
		if (isOptional) {
			sb.append(" ? ").append(defaultValue);
		}
		return sb.toString();
	}

	public FormalParameter copy() {
		return new FormalParameter(label, identifier, type.copy(), defaultValue.copy(), isOptional);
	}

	public VarD<?> spawn() {
		return new VarD<>(defaultValue.copy(), defaultValue.copy());
	}

}
