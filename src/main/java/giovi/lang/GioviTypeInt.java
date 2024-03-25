package giovi.lang;

public final class GioviTypeInt extends GioviTypeNumber<GioviInt> {

	public GioviTypeInt() {
		super(GioviInt.class);
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeInt __ -> true;
			case GioviTypeReal __ -> true;
			default -> false;
		};
	}

	@Override
	public GioviInt spawn() {
		return new GioviInt();
	}

	@Override
	public GioviTypeInt copy() {
		return new GioviTypeInt();
	}

}

