package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.primitives.Struct;

public non-sealed class GioviStructNamed extends GioviStruct {

	public GioviStructNamed(Struct.Named value, Struct.Named defaultValue) {
		super(value, defaultValue);
	}

	public GioviStructNamed(Struct.Named value) {
		super(value, Struct.copyOf(value));
	}

	@Override
	public GioviStructNamed copy() {
		return new GioviStructNamed(
			(Struct.Named) this.value.copy(),
			(Struct.Named) this.defaultValue.copy());
	}


}
