package it.simonvic.giovi.utils;

import giovi.lang.GioviValue;
import it.simonvic.giovi.Const;

public class Consts {

	public static <T> Const<? extends GioviValue<?>> of(T value) {
		return new Const<>(GioviValue.of(value));
	}

}
