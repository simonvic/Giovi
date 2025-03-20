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

	// TODO: wtf?! This was canWiden(), so it should be named isNarrower() or
	// even better fitsIn() since it currently computes if this is narrower or
	// equal to type
	boolean isWider(GioviType<?> type);

	T widen(GioviValue<?> value) throws NonWideneable;

	// TODO: add isNarrower(type)

	T spawn();

	@Override
	GioviType<T> copy();

}
