package it.simonvic.giovi.utils;

import giovi.lang.GioviValue;
import it.simonvic.giovi.Const;

public class Consts {

	public static <J> Const<? extends GioviValue<?>> of(J value) {
		return new Const<>(GioviValue.of(value));
	}

}
