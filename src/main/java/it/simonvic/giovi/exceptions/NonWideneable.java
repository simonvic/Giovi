package it.simonvic.giovi.exceptions;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;

public class NonWideneable extends Exception {

	public NonWideneable(GioviValue<?> value, GioviType<?> type) {
		super("Can't widen " + value + " to " + type);
	}

}

