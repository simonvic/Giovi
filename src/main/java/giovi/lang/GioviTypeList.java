package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.util.GioviLists;

public record GioviTypeList(GioviType<?> entryType, Integer initSize)
	implements GioviType<GioviList> {

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
	public Class<? extends GioviList> value() {
		return GioviList.class;
	}

	@Override
	public Class<? extends GioviList> defaultValue() {
		return GioviList.class;
	}

	@Override
	public String toString() {
		return "[" + entryType + "]";
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

	@Override
	public GioviList spawn(GioviList gioviValue) {
		return new GioviList(this.entryType, gioviValue.value(),
			GioviLists.copyOf(gioviValue.value()));
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviList(entryType);
		}
		return GioviType.super.dot(right);
	}


}
