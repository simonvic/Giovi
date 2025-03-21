package giovi.lang;

import giovi.Record;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeRecord(Record definition) implements GioviType<GioviRecord> {

	@Override
	public String toString() {
		return definition.toStringDefinition();
	}

	@Override
	public Class<? extends GioviRecord> value() {
		return GioviRecord.class;
	}

	@Override
	public Class<? extends GioviRecord> defaultValue() {
		return GioviRecord.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeStruct(var definition) -> this.definition.isWidenableTo(definition);
			case GioviTypeRecord(var definition) -> this.definition.isWidenableTo(definition);
			default -> false;
		};
	}

	@Override
	public GioviRecord widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviTuple v -> new GioviRecord(definition, v.value().widenTo(definition));
			case GioviRecord v -> new GioviRecord(definition, v.value().widenTo(definition));
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviRecord spawn() {
		return new GioviRecord(definition, definition.copy());
	}

	@Override
	public GioviTypeRecord copy() {
		return new GioviTypeRecord(definition.copy());
	}

}

