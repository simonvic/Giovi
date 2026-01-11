package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeType<T extends GioviValue<?>>() implements GioviTypePrimitive<GioviType<T>> {

	@Override
	public String toString() {
		return "Type";
	}

	@Override
	public Class<? extends GioviType<T>> value() {
		throw new UnsupportedOperationException("Unimplemented method 'value'");
	}

	@Override
	public Class<? extends GioviType<T>> zero() {
		throw new UnsupportedOperationException("Unimplemented method 'defaultValue'");
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
	public boolean isWidenableTo(GioviType<?> type) {
		return type instanceof GioviTypeType;
	}

	@Override
	public GioviType<T> widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviType<?> v -> (GioviType<T>) v;
			default -> throw new NonWideneable(value, this);
			// TODO: more robust
		};
	}

	@Override
	public GioviType<T> spawn() {
		return (GioviType<T>) GioviVoid.TYPE;
	}

	@Override
	public GioviTypeType<T> copy() {
		return new GioviTypeType<>();
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return GioviVoid.TYPE; // TODO: emh...
		}
		return GioviTypePrimitive.super.dot(right);
	}

}

