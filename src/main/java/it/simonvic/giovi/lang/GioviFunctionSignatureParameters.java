package it.simonvic.giovi.lang;

import it.simonvic.giovi.lang.values.GioviType;
import it.simonvic.giovi.lang.values.GioviValue;

public record GioviFunctionSignatureParameters(
	FormalParameter... parameters)
	implements GioviParserVisitResult {

	public static record FormalParameter(
		String identifier,
		GioviType<?> type,
		GioviValue<?> defaultValue) {

		public FormalParameter(String identifier, GioviType<?> type) {
			this(identifier, type, type.spawn());
		}

	}

}
