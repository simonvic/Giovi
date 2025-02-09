package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.NonWideneable;

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
	public boolean isWider(GioviType<?> type) {
		return switch (type) {
			case GioviTypeMap t
				-> getKeyType().isWider(t.getKeyType())
					&& getElementType().isWider(t.getElementType());
			default -> false;
		};
	}

	@Override
	public GioviMap widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviMap v -> {
				var widened = new GioviMap(this.keyType, this.elementType);
				for (var vEntry : v.value().entrySet()) {
					widened.value().put(
						new Var<>(this.keyType.widen(vEntry.getKey().value())),
						new Var<>(this.elementType.widen(vEntry.getValue().value())));
				}
				yield widened;
			}
			default -> throw new NonWideneable(value, this);
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
