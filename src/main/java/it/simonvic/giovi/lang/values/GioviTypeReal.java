package it.simonvic.giovi.lang.values;

public final class GioviTypeReal extends GioviTypeNumber<GioviReal> {

	public GioviTypeReal() {
		super(GioviReal.class);
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
	public GioviTypeReal copy() {
		return new GioviTypeReal();
	}


}
