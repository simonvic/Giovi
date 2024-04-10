package giovi.lang;

import giovi.Tuple;

public final class GioviTypeTuple extends GioviType<GioviTuple> {

	private final Tuple definition;

	public GioviTypeTuple(Tuple definition) {
		super(GioviTuple.class);
		this.definition = definition;
	}

	public Tuple getDefinition() {
		return definition;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeTuple t -> this.definition.canWidenTo(t.definition);
			case GioviTypeStruct t -> this.definition.canWidenTo(t.getDefinition());
			default -> false;
		};
	}

	@Override
	public GioviValue<?> spawn() {
		return new GioviTuple(definition.copy());
	}

	@Override
	public GioviType<GioviTuple> copy() {
		return new GioviTypeTuple(definition.copy());
	}

}

