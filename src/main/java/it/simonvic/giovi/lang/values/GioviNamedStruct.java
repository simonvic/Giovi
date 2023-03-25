package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.primitives.Struct;

public non-sealed class GioviNamedStruct extends GioviStruct {

	public GioviNamedStruct(Struct.Named value, Struct.Named defaultValue) {
		super(value, defaultValue);
	}

	public GioviNamedStruct(Struct.Named value) {
		super(value, Struct.copyOf(value));
	}

}
