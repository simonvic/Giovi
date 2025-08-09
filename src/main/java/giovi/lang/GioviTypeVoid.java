package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;

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
	public boolean isWidenableTo(GioviType<?> type) {
		return true;
	}

	@Override
	public GioviVoid widen(GioviValue<?> value) throws NonWideneable {
		if (value instanceof GioviVoid v) {
			return v;
		}
		throw new NonWideneable(value, this);
	}

	@Override
	public GioviTypeVoid copy() {
		return GioviVoid.TYPE;
	}

	@Override
	public GioviVoid spawn() {
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

