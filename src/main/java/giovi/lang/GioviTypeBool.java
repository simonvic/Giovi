package giovi.lang;

import giovi.GioviParserVisitResult;

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

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviBool();
		}
		return super.dot(right);
	}


}
