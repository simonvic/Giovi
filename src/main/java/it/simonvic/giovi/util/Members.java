package it.simonvic.giovi.util;

import giovi.lang.GioviValue;
import it.simonvic.giovi.Member;

public class Members {

	public static <T> Member<? extends GioviValue<?>> of(T value) {
		return new Member<>(GioviValue.of(value));
	}

	public static <T> Member<? extends GioviValue<?>> of(T value, T defaultValue) {
		return new Member<>(GioviValue.of(value), GioviValue.of(defaultValue));
	}

}

