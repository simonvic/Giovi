package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;

// TODO: is generic needed? Why not just extends GioviPrimitive<Class<? extends GioviValue<?>>>
// TODO: better integrate types with values instead of having two separate hierarchies (maybe with nested Annotated classes?)
public sealed interface GioviType<T extends GioviValue<?>>
	extends GioviPrimitive<Class<? extends T>>
	permits GioviTypePrimitive, GioviTypeList, GioviTypeMap, GioviTypeTuple, GioviTypeRecord,
	GioviTypeStruct, GioviTypeFunction {

	default String getFullyQualifiedName() {
		return value().getName().replace("Giovi", "");
	}

	default String getSimpleName() {
		return value().getSimpleName().replace("Giovi", "");
	}

	@Override
	default GioviTypeType getGioviType() {
		return new GioviTypeType();
	}

	@Override
	default GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		if (type.equals(this)) {
			return this;
		}
		throw new NonWideneable(this, type);
	}

	boolean canWidenTo(GioviType<?> to);

	// TODO: add canNarrowTo(type)

	T spawn();

	@Override
	GioviType<T> copy();

}
