package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;

// TODO: is generic needed? Why not just extends GioviPrimitive<Class<? extends GioviValue<?>>>
// TODO: better integrate types with values instead of having two separate hierarchies (maybe with nested Annotated classes?)
//@DescribedBy(GioviTypeType.class)
public sealed interface GioviType<T extends GioviValue<?>>
	extends GioviPrimitive<Class<? extends T>>
	permits GioviTypePrimitive, GioviTypeList, GioviTypeMap, GioviTypeTuple, GioviTypeRecord,
	GioviTypeStruct, GioviTypeSelf, GioviTypeFunction {

	default String getFullyQualifiedName() {
		return value().getName().replace("Giovi", "");
	}

	default String getSimpleName() {
		return value().getSimpleName().replace("Giovi", "");
	}

	@Override
	default GioviTypeType<T> getGioviType() {
		return new GioviTypeType<>();
	}

	/**
	 * Check if this type is widenable to the provided type.
	 * @param type
	 * @return true if this type is equal or narrower than the provided type, false otherwise
	 */
	boolean isWidenableTo(GioviType<?> type);

	// TODO: add isWider(type) or isNarrowableTo(type)

	/**
	 * Widen the provided value to a value whose type is described by this class
	 * @param value
	 * @return widened value
	 * @throws NonWideneable
	 */
	T widen(GioviValue<?> value) throws NonWideneable;

	T spawn();

	@Override
	GioviType<T> copy();

}
