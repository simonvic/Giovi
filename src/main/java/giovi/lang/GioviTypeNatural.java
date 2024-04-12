package giovi.lang;

import giovi.GioviParserVisitResult;

public final class GioviTypeNatural extends GioviTypeNumber<GioviNatural> {

	public GioviTypeNatural() {
		super(GioviNatural.class);
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

	@Override
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
		return super.dot(right);
	}


}
