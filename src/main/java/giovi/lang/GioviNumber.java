package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.Op.*;

public sealed interface GioviNumber<T extends Number>
	extends GioviPrimitive<T>,
	As, Redirection, RedirectionPlus, RedirectionMinus, RedirectionMultiply,
	RedirectionDivide,
	Positive, Negative, Plus, Minus, Multiply, Divide, Modulo, Power, Equal, NotEqual, Less,
	LessEqual, Greater, GreaterEqual, Dot
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
			default -> As.super.as(right);
		};
	}

	@Override
	default public GioviBool notEqual(GioviParserVisitResult right) {
		return new GioviBool(!equal(right).value());
	}

}
