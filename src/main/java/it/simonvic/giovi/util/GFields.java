package it.simonvic.giovi.util;

import giovi.lang.GioviValue;
import it.simonvic.giovi.GField;

public class GFields {

	public static <J> GField<? extends GioviValue<?>> of(J value) {
		return new GField<>(GioviValue.of(value));
	}

	public static <J> GField<? extends GioviValue<?>> of(J value, J defaultValue) {
		return new GField<>(GioviValue.of(value), GioviValue.of(defaultValue));
	}

}

