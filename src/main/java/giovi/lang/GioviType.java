package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;

// TODO: is generic needed? Why not just extends GioviPrimitive<Class<? extends GioviValue<?>>>
public sealed interface GioviType<T extends GioviValue<?>>
	extends GioviPrimitive<Class<? extends T>>
	permits GioviTypePrimitive, GioviTypeList, GioviTypeMap, GioviTypeTuple, GioviTypeTupleNamed,
	GioviTypeStruct, GioviTypeFunction {

	default String getFullyQualifiedName() {
		return value().getName().replace("Giovi", "");
	}

	default String getSimpleName() {
		return value().getSimpleName().replace("Giovi", "");
	}

	@Override
	default GioviType<? extends GioviValue<Class<? extends T>>> getGioviType() {
		return null; // TODO: implement this for all Types
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

	GioviValue<?> spawn();


	@Override
	GioviType<T> copy();

}
