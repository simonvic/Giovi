package it.simonvic.giovi.lang.values;

public abstract sealed class GioviNumber<T extends Number> extends GioviValue<T> permits GioviNatural, GioviInt, GioviReal{

	public GioviNumber(T value, T defaultValue) {
		super(value, defaultValue);
	}

	public abstract GioviNatural naturalValue();
	public abstract GioviInt intValue();
	public abstract GioviReal realValue();


}
