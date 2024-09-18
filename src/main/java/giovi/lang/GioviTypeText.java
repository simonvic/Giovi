package giovi.lang;

import giovi.GioviParserVisitResult;

public record GioviTypeText() implements GioviTypePrimitive<GioviText> {

	@Override
	public Class<? extends GioviText> value() {
		return GioviText.class;
	}

	@Override
	public Class<? extends GioviText> defaultValue() {
		return GioviText.class;
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
	public GioviText spawn(GioviText gioviValue) {
		return new GioviText(gioviValue.value());
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
		return GioviTypePrimitive.super.dot(right);
	}


}
