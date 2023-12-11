package it.simonvic.giovi.lang.values;

public abstract sealed class GioviPrimitive<T>
	extends GioviValue<T>
	permits GioviNumber, GioviBool, GioviText, GioviType, GioviVoid {

	public GioviPrimitive(T value, T defaultValue) {
		super(value, defaultValue);
	}

}
