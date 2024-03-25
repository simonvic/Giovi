package giovi.lang;

public final class GioviTypeNatural extends GioviTypeNumber<GioviNatural> {

	public GioviTypeNatural() {
		super(GioviNatural.class);
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeNatural __ -> true;
			case GioviTypeInt __ -> true;
			case GioviTypeReal __ -> true;
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

}
