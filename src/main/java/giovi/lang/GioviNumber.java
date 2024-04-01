package giovi.lang;

import giovi.GioviParserVisitResult;

public abstract sealed class GioviNumber<T extends Number>
	extends GioviPrimitive<T>
	permits GioviNatural, GioviInt, GioviReal {

	public GioviNumber(T value, T defaultValue) {
		super(value, defaultValue);
	}

	public abstract GioviNatural naturalValue();

	public abstract GioviInt intValue();

	public abstract GioviReal realValue();

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (right instanceof GioviTypeNumber<?> t) {
			var typeClass = t.unbox();
			if (typeClass == GioviInt.class) return intValue();
			if (typeClass == GioviReal.class) return realValue();
			if (typeClass == GioviNatural.class) return naturalValue();
		}
		return super.as(right);
	}

}
