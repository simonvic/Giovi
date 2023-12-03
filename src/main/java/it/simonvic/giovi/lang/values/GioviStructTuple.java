package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.values.natives.Struct;

public final class GioviStructTuple extends GioviStruct {

	public GioviStructTuple(Struct value, Struct defaultValue) {
		super(value, defaultValue);
	}

	public GioviStructTuple(Struct value) {
		this(value, value.copy());
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypeStructTuple();
	}

	@Override
	public GioviValue<Struct> copy() {
		return new GioviStructTuple(value.copy(), defaultValue.copy());
	}

}
