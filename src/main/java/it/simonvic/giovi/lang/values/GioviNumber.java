package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public abstract sealed class GioviNumber<T extends Number> extends GioviValue<T> permits GioviNatural, GioviInt, GioviReal{

	public GioviNumber(T value) {
		super(value);
	}

//	public abstract GioviNatural naturalValue();
//	public abstract GioviInt intValue();
//	public abstract GioviReal realValue();


}
