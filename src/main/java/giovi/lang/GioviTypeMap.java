package giovi.lang;

import giovi.GioviParserVisitResult;

public record GioviTypeMap(GioviType<?> keyType, GioviType<?> elementType)
	implements GioviType<GioviMap> {

	public GioviType<?> getKeyType() {
		return keyType;
	}

	public GioviType<?> getElementType() {
		return elementType;
	}

	@Override
	public Class<? extends GioviMap> value() {
		return GioviMap.class;
	}

	@Override
	public Class<? extends GioviMap> defaultValue() {
		return GioviMap.class;
	}

	@Override
	public String toString() {
		return "[" + keyType + " : " + elementType + "]";
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeMap t
				-> getKeyType().canWidenTo(t.getKeyType())
					&& getElementType().canWidenTo(t.getElementType());
			default -> false;
		};
	}

	@Override
	public GioviMap spawn() {
		return new GioviMap(this.keyType, this.elementType);
	}

	@Override
	public GioviTypeMap copy() {
		return new GioviTypeMap(this.keyType.copy(), this.elementType.copy());
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviMap(keyType, elementType);
		}
		return GioviType.super.dot(right);
	}


}
