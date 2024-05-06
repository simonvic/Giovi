package giovi.lang;

import giovi.FieldKey;
import giovi.GioviParserVisitResult;
import giovi.Struct;

public final class GioviTypeStruct extends GioviType<GioviStruct> {

	private final Struct definition;

	public GioviTypeStruct(Struct definition) {
		super(GioviStruct.class);
		this.definition = definition;
	}

	public Struct getDefinition() {
		return definition;
	}

	public String getTypename() {
		return definition.getTypename();
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
		GioviTypeStruct other = (GioviTypeStruct) obj;
		if (definition == null) {
			if (other.definition != null) return false;
		} else if (!definition.equals(other.definition)) return false;
		return true;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStruct t -> t.definition.equals(this.definition);
			default -> false;
		};
	}

	@Override
	public GioviValue<?> spawn() {
		return new GioviStruct(definition.copy());
	}

	@Override
	public GioviType<GioviStruct> copy() {
		return new GioviTypeStruct(definition.copy());
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviStruct(definition.copy());
			case FieldKey(var identifier) -> definition.get(identifier).copy();
			default -> super.dot(right);
		};
	}

}
