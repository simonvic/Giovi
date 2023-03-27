package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.primitives.Struct;

public non-sealed class GioviStructUnnamed extends GioviStruct {
	public GioviStructUnnamed(Struct.Unnamed value, Struct.Unnamed defaultValue) {
		super(value, defaultValue);
	}

	public GioviStructUnnamed(Struct.Unnamed value) {
		super(value, Struct.copyOf(value));
	}

	public GioviStructUnnamed() {
		super(new Struct.Unnamed(), new Struct.Unnamed());
	}

}
