package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public class Accessor extends GioviObject {
	private final String s;

	public Accessor(String s) {
		this.s = s;
	}

	public String get() {
		return s;
	}

	@Override
	public String toString() {
		return s;
	}
}
