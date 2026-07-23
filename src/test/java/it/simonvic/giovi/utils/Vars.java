package it.simonvic.giovi.utils;

import giovi.lang.GioviValue;
import it.simonvic.giovi.Var;

public class Vars {

	public static <J> Var<? extends GioviValue<?>> of(J value) {
		return new Var<>(GioviValue.of(value));
	}

	public static <J> Var<? extends GioviValue<?>> of(J value, J defaultValue) {
		return new Var<>(GioviValue.of(value), GioviValue.of(defaultValue));
	}

}
