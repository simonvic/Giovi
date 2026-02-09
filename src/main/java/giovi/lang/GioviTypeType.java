package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeType<T extends GioviValue<?>>(GioviType<T> describedType)
	implements GioviTypePrimitive<GioviType<T>> {

	@Override
	public String toString() {
		return "Type{" + describedType + "}";
	}

	@Override
	public Class<? extends GioviType<T>> value() {
		return (Class<? extends GioviType<T>>) describedType.getClass();
	}

	@Override
	public Class<? extends GioviType<T>> zero() {
		throw new UnsupportedOperationException("Unimplemented method 'zero'");
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
		// TODO: clarify requirements. Should we check for describedType widenability?
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
		return describedType;
	}

	@Override
	public GioviTypeType<T> copy() {
		return this;
	}

}

