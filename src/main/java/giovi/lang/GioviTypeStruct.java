package giovi.lang;

import giovi.FieldKey;
import giovi.GioviParserVisitResult;
import giovi.Struct;

public record GioviTypeStruct(Struct definition) implements GioviType<GioviStruct> {

	public Struct getDefinition() {
		return definition;
	}

	public String getTypename() {
		return definition.getTypename();
	}

	@Override
	public String toString() {
		return definition.toStringDefinition();
	}

	@Override
	public Class<? extends GioviStruct> value() {
		return GioviStruct.class;
	}

	@Override
	public Class<? extends GioviStruct> defaultValue() {
		return GioviStruct.class;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStruct t -> t.definition.canWidenTo(this.definition);
			default -> false;
		};
	}

	@Override
	public GioviStruct spawn() {
		return new GioviStruct(definition.copy());
	}

	@Override
	public GioviStruct spawn(GioviStruct gioviValue) {
		return new GioviStruct(gioviValue.value());
	}

	@Override
	public GioviTypeStruct copy() {
		return new GioviTypeStruct(definition.copy());
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviStruct(definition.copy());
			case FieldKey(var identifier) -> definition.get(identifier).copy();
			default -> GioviType.super.dot(right);
		};
	}

}
