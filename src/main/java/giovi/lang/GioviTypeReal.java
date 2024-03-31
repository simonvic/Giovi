package giovi.lang;

import giovi.GioviParserVisitResult;

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

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviReal();
		}
		return super.dot(right);
	}



}
