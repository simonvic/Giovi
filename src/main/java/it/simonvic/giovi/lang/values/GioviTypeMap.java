package it.simonvic.giovi.lang.values;

public final class GioviTypeMap extends GioviType<GioviMap> {

	private final GioviType<?> keyType;
	private final GioviType<?> elementType;

	public GioviTypeMap(GioviType<?> keyType, GioviType<?> elementType) {
		super(GioviMap.class);
		this.keyType = keyType;
		this.elementType = elementType;
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
		if (keyType == null) {
			if (other.keyType != null) return false;
		} else if (!keyType.equals(other.keyType)) return false;
		if (elementType == null) {
			if (other.elementType != null) return false;
		} else if (!elementType.equals(other.elementType)) return false;
		return true;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeMap t -> getKeyType().canWidenTo(t.getKeyType())
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


}
