package giovi.lang;

import giovi.Record;

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
	public boolean isWider(GioviType<?> type) {
		return switch (type) {
			case GioviTypeStruct(var definition) -> this.definition.isWider(definition);
			case GioviTypeRecord(var definition) -> this.definition.isWider(definition);
			default -> false;
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

