package it.simonvic.giovi.utils;

import giovi.lang.GioviValue;
import it.simonvic.giovi.Var;

public class Vars {

	public static <T> Var<? extends GioviValue<?>> of(T value) {
		return new Var<>(GioviValue.of(value));
	}

	public static <T> Var<? extends GioviValue<?>> of(T value, T defaultValue) {
		return new Var<>(GioviValue.of(value), GioviValue.of(defaultValue));
	}

}
