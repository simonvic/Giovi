package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.type.GioviType;

public abstract sealed class GioviNumber<T extends Number> extends GioviValue<T> permits GioviNatural, GioviInt, GioviReal{

	public GioviNumber(T value, T defaultValue) {
		super(value, defaultValue);
	}

	public abstract GioviNatural naturalValue();
	public abstract GioviInt intValue();
	public abstract GioviReal realValue();

	@Override
	public GioviValue<?> castTo(GioviType type) {
		Class<? extends GioviValue<?>> typeClass = type.unbox();
		if (typeClass == GioviNatural.class) return naturalValue();
		if (typeClass == GioviInt.class) return intValue();
		if (typeClass == GioviReal.class) return realValue();
		return super.castTo(type);
	}
}
