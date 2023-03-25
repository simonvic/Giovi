package it.simonvic.giovi.lang.values;

public abstract sealed class GioviNumber<T extends Number> extends GioviValue<T>
	permits GioviNatural, GioviInt, GioviReal {

	public GioviNumber(T value, T defaultValue) {
		super(value, defaultValue);
	}

	public abstract GioviNatural naturalValue();

	public abstract GioviInt intValue();

	public abstract GioviReal realValue();

	@Override
	public GioviValue<?> castTo(GioviType<?> type) {
		if (type instanceof GioviTypePrimitive t) {
			var typeClass = t.unbox();
			if (typeClass == GioviInt.class) return intValue();
			if (typeClass == GioviReal.class) return realValue();
			if (typeClass == GioviNatural.class) return naturalValue();
			if (typeClass == GioviText.class) return new GioviText(value.toString());
			// if (typeClass == GioviBool.class) return new GioviBool(value == 1);
		}
		return super.castTo(type);
	}

}
