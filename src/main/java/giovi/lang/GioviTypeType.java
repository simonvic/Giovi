package giovi.lang;

import giovi.GioviParserVisitResult;

public final class GioviTypeType extends GioviTypePrimitive<GioviType<?>> {

	public GioviTypeType() {
		super(null);
	}

	@Override
	public String getFullyQualifiedName() {
		return "giovi.Type";
	}

	@Override
	public String getSimpleName() {
		return "Type";
	}

	@Override
	public String toString() {
		return "Type";
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return to instanceof GioviTypeType;
	}

	@Override
	public GioviValue<?> spawn() {
		return new GioviTypeVoid();
	}

	@Override
	public GioviTypeType copy() {
		return new GioviTypeType();
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviTypeVoid(); // TODO: emh...
		}
		return super.dot(right);
	}


}

