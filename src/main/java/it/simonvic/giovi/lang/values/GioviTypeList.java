package it.simonvic.giovi.lang.values;

public final class GioviTypeList extends GioviTypePrimitive {

	private final GioviType<?> entryType;

	public GioviTypeList(GioviType<?> entryType) {
		super(GioviList.class);
		this.entryType = entryType;
	}

	public GioviType<?> getEntryType() {
		return entryType;
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStruct t -> false;
			case GioviTypeMap t -> false;
			case GioviTypeList t -> getEntryType().canUpcast(t.getEntryType());
			case GioviTypePrimitive t -> false;
		};
	}
	


}
