package it.simonvic.giovi.lang.values;

public abstract sealed class GioviTypeNumber<T extends GioviNumber<?>>
	extends GioviTypePrimitive<T>
	permits GioviTypeNatural, GioviTypeInt, GioviTypeReal {

	public GioviTypeNumber(Class<T> value, Class<T> defaultValue) {
		super(value, defaultValue);
	}

	public GioviTypeNumber(Class<T> value) {
		super(value);
	}

}
