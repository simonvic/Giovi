package giovi.lang;

import java.util.List;
import java.util.Map;

import giovi.Function;
import giovi.Natural;
import giovi.Record;
import giovi.Struct;
import giovi.Tuple;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.util.*;

public sealed interface GioviValue<T>
	extends GioviObject
	permits GioviPrimitive, GioviList, GioviMap, GioviTuple, GioviRecord, GioviStruct, GioviSelf,
	GioviFunction {

	public static <U> GioviValue<?> of(U value) {
		return switch (value) {
			case Natural v -> new GioviNatural(v);
			case Integer v -> new GioviInt(v);
			case Double v -> new GioviReal(v);
			case Boolean v -> new GioviBool(v);
			case String v -> new GioviText(v);
			case List<?> v -> GioviLists.of(v);
			case Map<?, ?> v -> GioviMaps.of(v);
			case Struct v -> new GioviStruct(GioviStructs.typeFrom(v), v);
			case Record v -> new GioviRecord(GioviRecords.typeFrom(v), v);
			case Tuple v -> new GioviTuple(GioviTuples.typeFrom(v), v);
			case Function v -> new GioviFunction(v);
			default -> throw new GioviException(
				"Unknown type " + value + " (" + value.getClass() + ")");
		};
	}

	default Class<?> getValueType() {
		return value().getClass();
	}

	/**
	 * TODO: maybe rename to unbox() or native() to avoid confusion from the Var.value()
	 * @return
	 */
	T value();

	/**
	 * TODO: remove this and move it to the Type hierarchy (when eventually it
	 * gets cached in a Var)
	 * @return default value of the type
	 */
	T defaultValue();

	/**
	 * Get the Type (in Giovi space) of this value
	 * @return Type
	 */
	GioviType<? extends GioviValue<T>> getGioviType();

	/**
	 * Widen the value to the specified type if it's wider (e.g. Natural <= Int <= Real)
	 * Lossless conversion
	 * @param type
	 * @return widened value
	 * @throws NonWideneable
	 */
	default <V extends GioviValue<?>> V widenTo(GioviType<V> type) throws NonWideneable {
		return type.widen(this);
	}

	// TODO: add narrowTo(type) which should be used along the widenTo(type) inside the as(type)

	/**
	 * Perform a <b>deep copy</b>
	 * @return copy of the object
	 */
	GioviValue<T> copy();
	//public abstract <U extends GioviValue<T>> U copy();

}
