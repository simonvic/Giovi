package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.values.natives.Type;

public abstract sealed class GioviType<T extends Type<?>>
	extends GioviPrimitive<T>
	permits GioviTypePrimitive, GioviTypeList, GioviTypeMap, GioviTypeStruct {

	public GioviType(T value, T defaultValue) {
		super(value, defaultValue);
	}

	@Override
	public String toString() {
		return value.getSimpleName();
	}

	public abstract boolean canUpcast(GioviType<?> to);

	public abstract GioviValue<?> spawn();

	@Override
	public abstract GioviType<T> copy();

}
