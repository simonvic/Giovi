package giovi.lang;

import giovi.GioviParserVisitResult;

public record GioviTypeInt() implements GioviTypeNumber<GioviInt> {

	@Override
	public Class<? extends GioviInt> value() {
		return GioviInt.class;
	}

	@Override
	public Class<? extends GioviInt> defaultValue() {
		return GioviInt.class;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeInt _ -> true;
			case GioviTypeReal _ -> true;
			default -> false;
		};
	}

	@Override
	public GioviInt spawn() {
		return new GioviInt();
	}

	@Override
	public GioviInt spawn(GioviInt gioviValue) {
		return new GioviInt(gioviValue.value(), gioviValue.value());
	}

	@Override
	public GioviTypeInt copy() {
		return new GioviTypeInt();
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviInt();
		}
		return GioviTypeNumber.super.dot(right);
	}


}

