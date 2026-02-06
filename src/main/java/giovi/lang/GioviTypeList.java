package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviList.class)
public record GioviTypeList(GioviType<?> entryType, Integer initSize)
	implements GioviType<GioviList> {

	public GioviTypeList(GioviType<?> entryType) {
		this(entryType, 0);
	}

	@Override
	public Class<? extends GioviList> value() {
		return GioviList.class;
	}

	@Override
	public Class<? extends GioviList> zero() {
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
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeList t -> this.entryType.isWidenableTo(t.entryType);
			default -> false;
		};
	}

	@Override
	public GioviList widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviList v when v.entryType().equals(this.entryType) -> v;
			case GioviList v -> {
				var widened = new GioviList(this.entryType);
				for (var entry : v.value()) {
					widened.value().add(new Var<>(this.entryType.widen(entry.unwrap())));
				}
				yield widened;
			}
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviList spawn() {
		var list = new GioviList(this.entryType);
		var rawList = list.value();
		for (int i = 0; i < this.initSize; i++) {
			rawList.add(new Var<>(this.entryType.spawn()));
		}
		return list;
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviList(entryType);
		}
		return GioviType.super.dot(right);
	}


}
