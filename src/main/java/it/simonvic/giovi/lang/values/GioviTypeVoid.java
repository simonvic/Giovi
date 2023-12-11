package it.simonvic.giovi.lang.values;

public final class GioviTypeVoid extends GioviTypePrimitive<GioviVoid> {

	public GioviTypeVoid() {
		super(GioviVoid.class);
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		return false;
	}

	@Override
	public GioviTypeVoid copy() {
		throw new UnsupportedOperationException("Unimplemented method 'copy'");
	}

	@Override
	public GioviVoid spawn() {
		throw new UnsupportedOperationException("Unimplemented method 'spawn'");
	}

}

