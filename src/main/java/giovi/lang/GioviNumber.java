package giovi.lang;

public sealed interface GioviNumber<T extends Number>
	extends GioviPrimitive<T>
	permits GioviNatural, GioviInt, GioviReal {

	GioviNatural naturalValue();

	GioviInt intValue();

	GioviReal realValue();

	// @Override
	// public GioviParserVisitResult as(GioviParserVisitResult right) {
	// 	if (right instanceof GioviTypeNumber<?> t) {
	// 		var typeClass = t.unbox();
	// 		if (typeClass == GioviInt.class) return intValue();
	// 		if (typeClass == GioviReal.class) return realValue();
	// 		if (typeClass == GioviNatural.class) return naturalValue();
	// 	}
	// 	return super.as(right);
	// }
	//
}
