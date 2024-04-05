package giovi.lang;

public final class GioviTypeTuple extends GioviType<GioviTuple> {

	// TODO: add signature?

	public GioviTypeTuple() {
		super(GioviTuple.class);
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'canWidenTo'");
	}

	@Override
	public GioviValue<?> spawn() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'spawn'");
	}

	@Override
	public GioviType<GioviTuple> copy() {
		return new GioviTypeTuple();
	}

}

