package giovi.lang;

import giovi.GioviParserVisitResult;

public record GioviTypeNatural() implements GioviTypeNumber<GioviNatural> {

	@Override
	public String toString() {
		return "Natural";
	}

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
			case GioviTypeNatural _, GioviTypeInt _, GioviTypeReal _ -> true;
            default -> false;
		};
	}

	@ Override
	public GioviNatural spawn() {
		return new GioviNatural();
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
