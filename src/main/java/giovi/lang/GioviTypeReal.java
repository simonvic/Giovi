package giovi.lang;

import giovi.GioviParserVisitResult;

public record GioviTypeReal() implements GioviTypeNumber<GioviReal> {

	@Override
	public Class<? extends GioviReal> value() {
		return GioviReal.class;
	}

	@Override
	public Class<? extends GioviReal> defaultValue() {
		return GioviReal.class;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return to instanceof GioviTypeReal;
	}

	@Override
	public GioviReal spawn() {
		return new GioviReal();
	}

	@Override
	public GioviReal spawn(GioviReal gioviValue) {
		return new GioviReal(gioviValue.value(), gioviValue.value());
	}

	@Override
	public GioviTypeReal copy() {
		return new GioviTypeReal();
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviReal();
		}
		return GioviTypeNumber.super.dot(right);
	}

}
