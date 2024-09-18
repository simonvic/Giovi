package giovi.lang;

import giovi.GioviParserVisitResult;

public record GioviTypeNatural() implements GioviTypeNumber<GioviNatural> {

	@Override
	public Class<? extends GioviNatural> value() {
		return GioviNatural.class;
	}

	@Override
	public Class<? extends GioviNatural> defaultValue() {
		return GioviNatural.class;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeNatural _ -> true;
			case GioviTypeInt _ -> true;
			case GioviTypeReal _ -> true;
			default -> false;
		};
	}

	@ Override
	public GioviNatural spawn() {
		return new GioviNatural();
	}

	@Override
	public GioviNatural spawn(GioviNatural gioviValue) {
		return new GioviNatural(gioviValue.value());
	}

	@Override
	public GioviTypeNatural copy() {
		return new GioviTypeNatural();
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviNatural();
		}
		return GioviTypeNumber.super.dot(right);
	}


}
