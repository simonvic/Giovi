package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.MemberKey;
import giovi.Struct;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeStruct(Struct definition) implements GioviType<GioviStruct> {

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
	public boolean isWider(GioviType<?> type) {
		return switch (type) {
			case GioviTypeStruct t -> t.definition.isWider(this.definition);
			default -> false;
		};
	}

	@Override
	public GioviStruct widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviTuple v -> new GioviStruct(definition, v.value().widenTo(definition));
			case GioviRecord v -> new GioviStruct(definition, v.value().widenTo(definition));
			case GioviStruct v -> new GioviStruct(definition, v.value().widenTo(definition));
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviStruct spawn() {
		return new GioviStruct(definition, definition.copy());
	}

	@Override
	public GioviTypeStruct copy() {
		// TODO: unnecessary copy?
		return new GioviTypeStruct(definition.copy());
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviStruct(definition, definition.copy());
			case MemberKey(var identifier) when definition.containsField(identifier)
				-> definition.get(identifier).copy();
			case MemberKey(var identifier) when definition.containsConstant(identifier)
				-> definition.getConstant(identifier);
			case MemberKey(var identifier) when definition.containsFunction(identifier)
				-> definition.getFunction(identifier);
			default -> GioviType.super.dot(right);
		};
	}

}
