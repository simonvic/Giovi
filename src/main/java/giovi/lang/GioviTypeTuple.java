package giovi.lang;

import giovi.Tuple;

public record GioviTypeTuple(Tuple definition) implements GioviType<GioviTuple> {

	public Tuple getDefinition() {
		return definition;
	}

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
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStruct t -> this.definition.canWidenTo(t.getDefinition());
			case GioviTypeTupleNamed t -> this.definition.canWidenTo(t.getDefinition());
			case GioviTypeTuple t -> this.definition.canWidenTo(t.definition);
			default -> false;
		};
	}

	@Override
	public GioviTuple spawn() {
		return new GioviTuple(definition.copy());
	}

	@Override
	public GioviTuple spawn(GioviTuple gioviValue) {
		return new GioviTuple(gioviValue.value());
	}

	@Override
	public GioviType<GioviTuple> copy() {
		return new GioviTypeTuple(definition.copy());
	}

}

