package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviMap.class)
public record GioviTypeMap(GioviType<?> keyType, GioviType<?> elementType)
	implements GioviTypeComposite<GioviMap> {

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
	public String toString() {
		return "[" + keyType + " : " + elementType + "]";
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeMap(var keyType, var elementType)
				-> this.keyType.isWidenableTo(keyType)
					&& this.elementType.isWidenableTo(elementType);
			default -> GioviTypeComposite.super.isWidenableTo(type);
		};
	}

	@Override
	public GioviMap widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviMap(_, var v) -> {
				try {
					yield new GioviMap(this, v);
				} catch (IllegalArgumentException ex) {
					throw new NonWideneable(ex);
				}
			}
			default -> GioviTypeComposite.super.widen(value);
		};
	}

	@Override
	public GioviMap spawn() {
		return new GioviMap(this);
	}

	@Override
	public GioviTypeMap copy() {
		return new GioviTypeMap(this.keyType.copy(), this.elementType.copy());
	}

}
