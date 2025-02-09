package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeBool() implements GioviTypePrimitive<GioviBool> {

	@Override
	public String toString() {
		return "Bool";
	}

	@Override
	public Class<? extends GioviBool> value() {
		return GioviBool.class;
	}

	@Override
	public Class<? extends GioviBool> defaultValue() {
		return GioviBool.class;
	}

	@Override
	public boolean isWider(GioviType<?> type) {
		return type instanceof GioviTypeBool;
	}

	@Override
	public GioviBool widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviBool v -> v;
			default -> throw new NonWideneable(value, this);
		};
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
		return GioviTypePrimitive.super.dot(right);
	}

}
