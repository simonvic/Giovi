package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.primitives.Type;

public abstract sealed class GioviPrimitive<T>
	extends GioviValue<T>
	permits GioviNumber, GioviBool, GioviText, GioviType {

	public GioviPrimitive(T value, T defaultValue) {
		super(value, defaultValue);
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypePrimitive(new Type.Primitive<>(this.getClass()));
	}

}
