package giovi.lang;

import giovi.GioviParserVisitResult;

public final class GioviTypeText extends GioviTypePrimitive<GioviText> {

	public GioviTypeText() {
		super(GioviText.class);
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return to instanceof GioviTypeText;
	}

	@Override
	public GioviText spawn() {
		return new GioviText();
	}

	@Override
	public GioviTypeText copy() {
		return new GioviTypeText();
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviText();
		}
		return super.dot(right);
	}


}
