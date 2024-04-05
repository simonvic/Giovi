package giovi.lang;

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
		if (right instanceof GioviDefault) {
			return new GioviStruct(definition.copy());
		}
		return super.dot(right);
	}

}
