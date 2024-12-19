package giovi.lang;

import giovi.GioviParserVisitResult;

public record GioviTypeText() implements GioviTypePrimitive<GioviText> {

	@Override
	public String toString() {
		return "Text";
	}

	@Override
	public Class<? extends GioviText> value() {
		return GioviText.class;
	}

	@Override
	public Class<? extends GioviText> defaultValue() {
		return GioviText.class;
	}

	@Override
	public boolean isWider(GioviType<?> type) {
		return type instanceof GioviTypeText;
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
		return GioviTypePrimitive.super.dot(right);
	}


}
