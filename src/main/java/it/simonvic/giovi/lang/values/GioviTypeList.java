package it.simonvic.giovi.lang.values;

import java.util.Objects;

public final class GioviTypeList extends GioviType<GioviList> {

	private final GioviType<?> entryType;
	private final Integer initSize;

	public GioviTypeList(GioviType<?> entryType, Integer initSize) {
		super(GioviList.class);
		this.entryType = entryType;
		this.initSize = initSize;
	}

	public GioviTypeList(GioviType<?> entryType) {
		this(entryType, 0);
	}

	public GioviType<?> getEntryType() {
		return entryType;
	}

	public Integer getInitSize() {
		return initSize;
	}

	@Override
	public String toString() {
		return "[" + entryType + "]";
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
		return Objects.equals(this.entryType, other.entryType);
	}

	@Override
	public GioviTypeList copy() {
		return new GioviTypeList(this.entryType.copy(), this.initSize);
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeList t -> getEntryType().canWidenTo(t.getEntryType());
			default -> false;
		};
	}

	@Override
	public GioviList spawn() {
		var list = new GioviList(this.entryType);
		var rawList = list.unbox();
		for (int i = 0; i < this.initSize; i++) {
			rawList.add(this.entryType.spawn());
		}
		return list;
	}

}
