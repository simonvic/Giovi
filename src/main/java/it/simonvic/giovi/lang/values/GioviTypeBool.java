package it.simonvic.giovi.lang.values;

public final class GioviTypeBool extends GioviTypePrimitive<GioviBool> {

	public GioviTypeBool() {
		super(GioviBool.class);
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return to instanceof GioviTypeBool;
	}

	@Override
	public GioviBool spawn() {
		return new GioviBool();
	}

	@Override
	public GioviTypeBool copy() {
		return new GioviTypeBool();
	}

}
