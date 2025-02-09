package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeInt() implements GioviTypeNumber<GioviInt> {

	@Override
	public String toString() {
		return "Int";
	}

	@Override
	public Class<? extends GioviInt> value() {
		return GioviInt.class;
	}

	@Override
	public Class<? extends GioviInt> defaultValue() {
		return GioviInt.class;
	}

	@Override
	public boolean isWider(GioviType<?> type) {
		return switch (type) {
			case GioviTypeInt _, GioviTypeReal _ -> true;
			default -> false;
		};
	}

	@Override
	public GioviInt widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviNatural v -> v.intValue();
			case GioviInt v -> v;
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviInt spawn() {
		return new GioviInt();
	}

	@Override
	public GioviTypeInt copy() {
		return new GioviTypeInt();
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviInt();
		}
		return GioviTypeNumber.super.dot(right);
	}


}

