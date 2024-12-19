package giovi.lang;

import giovi.GioviParserVisitResult;

public record GioviTypeType() implements GioviTypePrimitive<GioviType<?>> {

	@Override
	public String toString() {
		return "Type";
	}

	@Override
	public Class<? extends GioviType<?>> value() {
		return GioviTypeType.class;
	}

	@Override
	public Class<? extends GioviType<?>> defaultValue() {
		return GioviTypeType.class;
	}

	@Override
	public String getFullyQualifiedName() {
		return "giovi.Type";
	}

	@Override
	public String getSimpleName() {
		return "Type";
	}

	@Override
	public boolean isWider(GioviType<?> type) {
		return type instanceof GioviTypeType;
	}

	@Override
	public GioviTypeVoid spawn() {
		return new GioviTypeVoid();
	}

	@Override
	public GioviTypeType copy() {
		return new GioviTypeType();
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviTypeVoid(); // TODO: emh...
		}
		return GioviTypePrimitive.super.dot(right);
	}

}

