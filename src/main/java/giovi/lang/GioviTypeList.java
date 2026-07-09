package giovi.lang;

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
			default -> GioviType.super.isWidenableTo(type);
		};
	}

	@Override
	public GioviList widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviList v when v.type().entryType().equals(this.entryType) -> v;
			case GioviList v -> {
				var widened = new GioviList(this);
				for (var entry : v.value()) {
					widened.value().add(new Var<>(this.entryType.widen(entry.unwrap())));
				}
				yield widened;
			}
			default -> GioviType.super.widen(value);
		};
	}

	@Override
	public GioviList spawn() {
		var list = new GioviList(this);
		// TODO: remove initSize from the type signature
		// HACK: it doesn't belong to the type and will lead to unexpected behaviour,
		// it should be handled by the visitor appropriately
		var rawList = list.value();
		for (int i = 0; i < this.initSize; i++) {
			rawList.add(new Var<>(this.entryType.spawn()));
		}
		return list;
	}

}
