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
	public String toString() {
		return "[" + entryType.toString() + "]";
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entryType == null) ? 0 : entryType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		GioviTypeList other = (GioviTypeList) obj;
		if (entryType == null) {
			if (other.entryType != null) return false;
		} else if (!entryType.equals(other.entryType)) return false;
		return true;
	}


}
