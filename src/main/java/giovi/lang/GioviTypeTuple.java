package giovi.lang;

import giovi.Tuple;

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
	public boolean isWider(GioviType<?> type) {
		return switch (type) {
			case GioviTypeStruct(var definition) -> this.definition.isWider(definition);
			case GioviTypeRecord(var definition) -> this.definition.isWider(definition);
			case GioviTypeTuple(var definition) -> this.definition.isWider(definition);
			default -> false;
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

