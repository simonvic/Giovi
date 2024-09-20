package giovi.lang;

import giovi.TupleNamed;

public record GioviTypeTupleNamed(TupleNamed definition) implements GioviType<GioviTupleNamed> {

	@Override
	public String toString() {
		return definition.toString();
	}

	@Override
	public Class<? extends GioviTupleNamed> value() {
		return GioviTupleNamed.class;
	}

	@Override
	public Class<? extends GioviTupleNamed> defaultValue() {
		return GioviTupleNamed.class;
	}

	public TupleNamed getDefinition() {
		return definition;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStruct t -> this.definition.canWidenTo(t.getDefinition());
			case GioviTypeTupleNamed t -> this.definition.canWidenTo(t.definition);
			default -> false;
		};
	}

	@Override
	public GioviTupleNamed spawn() {
		return new GioviTupleNamed(definition.copy());
	}

	@Override
	public GioviTupleNamed spawn(GioviTupleNamed gioviValue) {
		return new GioviTupleNamed(gioviValue.value());
	}

	@Override
	public GioviTypeTupleNamed copy() {
		return new GioviTypeTupleNamed(definition.copy());
	}

}

