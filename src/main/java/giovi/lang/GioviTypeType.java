package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeType<G extends GioviValue<?>>(GioviType<G> describedType)
	implements GioviTypePrimitive<GioviType<G>> {

	@Override
	public String toString() {
		return "Type{" + describedType + "}";
	}

	@Override
	public Class<? extends GioviType<G>> value() {
		return (Class<? extends GioviType<G>>) describedType.getClass();
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
		return switch (type) {
			case GioviTypeType<?> _ -> true;
			default -> GioviTypePrimitive.super.isWidenableTo(type);
		};
	}

	@Override
	public GioviType<G> widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviType<?> v -> (GioviType<G>) v;
			default -> GioviTypePrimitive.super.widen(value);
			// TODO: more robust
		};
	}

	@Override
	public GioviType<G> spawn() {
		return describedType;
	}

	@Override
	public GioviTypeType<G> copy() {
		return this;
	}

}

