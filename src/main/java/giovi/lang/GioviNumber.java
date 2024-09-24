package giovi.lang;

import giovi.GioviParserVisitResult;

public sealed interface GioviNumber<T extends Number>
	extends GioviPrimitive<T>
	permits GioviNatural, GioviInt, GioviReal {

	GioviNatural naturalValue();

	GioviInt intValue();

	GioviReal realValue();

	@Override
	default public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviTypeReal _ -> realValue();
			case GioviTypeInt _ -> intValue();
			case GioviTypeNatural _ -> naturalValue();
			default -> GioviPrimitive.super.as(right);
		};
	}

}
