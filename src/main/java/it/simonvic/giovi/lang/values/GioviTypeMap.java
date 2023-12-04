package it.simonvic.giovi.lang.values;

import java.util.Objects;

import it.simonvic.giovi.lang.values.natives.Type;

public final class GioviTypeMap extends GioviType<Type.Map> {

	private final GioviType<?> keyType;
	private final GioviType<?> elementType;

	public GioviTypeMap(
		Type.Map value,
		Type.Map defaultValue,
		GioviType<?> keyType,
		GioviType<?> elementType) {
		super(value, defaultValue);
		this.keyType = keyType;
		this.elementType = elementType;
	}

	public GioviTypeMap(
		Type.Map value,
		GioviType<?> keyType,
		GioviType<?> elementType) {
		super(value, value.copy());
		this.keyType = keyType;
		this.elementType = elementType;
	}

	public GioviTypeMap(GioviType<?> keyType, GioviType<?> elementType) {
		this(new Type.Map(keyType.unbox(), elementType.unbox()), keyType, elementType);
	}

	public GioviType<?> getKeyType() {
		return keyType;
	}

	public GioviType<?> getElementType() {
		return elementType;
	}

	@Override
	public String toString() {
		return "[" + keyType + " : " + elementType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
		result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		GioviTypeMap other = (GioviTypeMap) obj;
		return Objects.equals(this.keyType, other.keyType)
			&& Objects.equals(this.elementType, other.elementType);
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		return switch (to) {
			case GioviTypeMap t -> getKeyType().canUpcast(t.getKeyType())
				&& getElementType().canUpcast(t.getElementType());
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


}
