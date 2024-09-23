package giovi.lang;

import giovi.TupleNamed;

public record GioviTypeTupleNamed(TupleNamed definition) implements GioviType<GioviTupleNamed> {

	@Override
	public String toString() {
		return definition.toStringDefinition();
	}

	@Override
	public Class<? extends GioviTupleNamed> value() {
		return GioviTupleNamed.class;
	}

	@Override
	public Class<? extends GioviTupleNamed> defaultValue() {
		return GioviTupleNamed.class;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStruct(var definition) -> this.definition.canWidenTo(definition);
			case GioviTypeTupleNamed(var definition) -> this.definition.canWidenTo(definition);
			default -> false;
		};
	}

	@Override
	public GioviTupleNamed spawn() {
		return new GioviTupleNamed(definition.copy());
	}

	@Override
	public GioviTypeTupleNamed copy() {
		return new GioviTypeTupleNamed(definition.copy());
	}

}

