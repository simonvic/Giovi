package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.primitives.Type;

public abstract sealed class GioviTypeStruct<T extends Type.Struct<?>>
	extends GioviType<T>
	permits GioviTypeStructTyped, GioviTypeStructTuple {


	public GioviTypeStruct(T value, T defaultValue) {
		super(value, defaultValue);
	}

}
