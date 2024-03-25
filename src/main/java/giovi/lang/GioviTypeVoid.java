package giovi.lang;

public final class GioviTypeVoid extends GioviTypePrimitive<GioviVoid> {

	public GioviTypeVoid() {
		super(GioviVoid.class);
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return true;
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

