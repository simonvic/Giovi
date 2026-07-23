package it.simonvic.giovi.util;

import giovi.lang.GioviValue;
import it.simonvic.giovi.Member;

public class Members {

	public static <J> Member<? extends GioviValue<?>> of(J value) {
		return new Member<>(GioviValue.of(value));
	}

	public static <J> Member<? extends GioviValue<?>> of(J value, J defaultValue) {
		return new Member<>(GioviValue.of(value), GioviValue.of(defaultValue));
	}

}

