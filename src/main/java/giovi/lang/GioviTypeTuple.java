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
	public String toString() {
		return definition.toStringDefinition();
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
		GioviTypeTuple other = (GioviTypeTuple) obj;
		if (definition == null) {
			if (other.definition != null) return false;
		} else if (!definition.equals(other.definition)) return false;
		return true;
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

