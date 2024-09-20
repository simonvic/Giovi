package giovi.lang;

import giovi.GioviParserVisitResult;

public record GioviTypeVoid() implements GioviTypePrimitive<GioviVoid> {

	@Override
	public String toString() {
		return "Void";
	}

	@Override
	public Class<? extends GioviVoid> value() {
		return GioviVoid.class;
	}

	@Override
	public Class<? extends GioviVoid> defaultValue() {
		return GioviVoid.class;
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
		return new GioviVoid();
	}

	@Override
	public GioviVoid spawn(GioviVoid gioviValue) {
		return new GioviVoid();
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviVoid();
		}
		return GioviTypePrimitive.super.dot(right);
	}


}

