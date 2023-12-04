package it.simonvic.giovi.lang;

import it.simonvic.giovi.lang.values.GioviValue;

public record GioviFunctionParameter(GioviValue<?> value, boolean hasDefault) {

	public GioviFunctionParameter(GioviValue<?> value) {
		this(value, false);
	}

}
