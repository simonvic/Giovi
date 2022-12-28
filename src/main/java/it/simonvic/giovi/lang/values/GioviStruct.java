package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.util.GioviStructs;

public non-sealed class GioviStruct extends GioviValue<Struct> {

	public GioviStruct(Struct value) {
		super(value, GioviStructs.defaultOf(value));
	}

}
