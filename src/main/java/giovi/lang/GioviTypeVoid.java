package giovi.lang;

import giovi.GioviParserVisitResult;

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
		return new GioviTypeVoid();
	}

	@Override
	public GioviVoid spawn() {
		throw new UnsupportedOperationException("Unimplemented method 'spawn'");
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviVoid(); // TODO: emh...
		}
		return super.dot(right);
	}


}

