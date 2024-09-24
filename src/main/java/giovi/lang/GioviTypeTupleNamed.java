package giovi.lang;

import giovi.TupleNamed;

public sealed class GioviTypeTupleNamed extends GioviTypeTuple permits GioviTypeStruct {

	private final TupleNamed definition;

	public GioviTypeTupleNamed(TupleNamed definition) {
		super(GioviTupleNamed.class, definition);
		this.definition = definition;
	}

	public GioviTypeTupleNamed(Class<? extends GioviTupleNamed> clazz, TupleNamed definition) {
		super(clazz, definition);
		this.definition = definition;
	}

	@Override
	public TupleNamed getDefinition() {
		return definition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((definition == null) ? 0 : definition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		GioviTypeTupleNamed other = (GioviTypeTupleNamed) obj;
		if (definition == null) {
			if (other.definition != null) return false;
		} else if (!definition.equals(other.definition)) return false;
		return true;
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
	public GioviTypeTupleNamed copy() {
		return new GioviTypeTupleNamed(definition.copy());
	}

}

