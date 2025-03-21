package giovi.lang;

import giovi.Tuple;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeTuple(Tuple definition) implements GioviType<GioviTuple> {

	@Override
	public Class<? extends GioviTuple> value() {
		return GioviTuple.class;
	}

	@Override
	public Class<? extends GioviTuple> defaultValue() {
		return GioviTuple.class;
	}

	@Override
	public String toString() {
		return definition.toStringDefinition();
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeStruct(var definition) -> this.definition.isWidenableTo(definition);
			case GioviTypeRecord(var definition) -> this.definition.isWidenableTo(definition);
			case GioviTypeTuple(var definition) -> this.definition.isWidenableTo(definition);
			default -> false;
		};
	}

	@Override
	public GioviTuple widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviTuple v -> new GioviTuple(definition, v.value().widenTo(definition));
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviTuple spawn() {
		return new GioviTuple(definition, definition.copy());
	}

	@Override
	public GioviType<GioviTuple> copy() {
		return new GioviTypeTuple(definition.copy());
	}

}

