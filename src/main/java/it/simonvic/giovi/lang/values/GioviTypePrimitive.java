package it.simonvic.giovi.lang.values;

public abstract sealed class GioviTypePrimitive<T extends GioviPrimitive<?>>
	extends GioviType<T>
	permits GioviTypeVoid, GioviTypeBool, GioviTypeNumber, GioviTypeText {

	public GioviTypePrimitive(Class<T> value, Class<T> defaultValue) {
		super(value, defaultValue);
	}

	public GioviTypePrimitive(Class<T> value) {
		super(value);
	}

}

