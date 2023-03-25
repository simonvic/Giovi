package it.simonvic.giovi.lang.values;


public final class GioviTypeMap extends GioviTypePrimitive {

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
	public boolean canUpcast(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStruct t -> false;
			case GioviTypeList t -> false;
			case GioviTypeMap t -> getKeyType().canUpcast(t.getKeyType())
				&& getElementType().canUpcast(t.getElementType());
			case GioviTypePrimitive t -> false;
		};
	}

}
