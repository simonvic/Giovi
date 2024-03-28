package giovi.lang;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import giovi.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.IncompatibleTypes;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviMaps;

public abstract sealed class GioviValue<T>
	extends GioviObject
	permits GioviPrimitive, GioviList, GioviMap, GioviTuple, GioviStruct, GioviFunction {

	public static <U> GioviValue<?> of(U value) {
		return switch (value) {
			case Natural v -> new GioviNatural(v);
			case Integer v -> new GioviInt(v);
			case Double v -> new GioviReal(v);
			case Boolean v -> new GioviBool(v);
			case String v -> new GioviText(v);
			case List<?> v -> GioviLists.of(v);
			case Map<?, ?> v -> GioviMaps.of(v);
			case Struct v -> new GioviStruct(v);
			case Tuple v -> new GioviTuple(v);
			default -> throw new GioviException(
				"Unknown type " + value + " (" + value.getClass() + ")");
		};
	}

	protected T value;
	protected T defaultValue;

	public GioviValue(T value, T defaultValue) {
		this.value = value;
		this.defaultValue = defaultValue;
	}

	public Class<?> getValueType() {
		return value.getClass();
	}

	public T unbox() {
		return value;
	}

	public void box(T value) {
		this.value = value;
	}

	// TODO: remove this
	public void box(GioviValue<?> gioviValue) {
		throw new IncompatibleTypes(this.getGioviType(), gioviValue.getGioviType());
	}

	public void boxDefault() {
		box(defaultValue);
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(GioviValue<?> defaultValue) {
		throw new IncompatibleTypes(this.getGioviType(), defaultValue.getGioviType());
	}

	public abstract GioviType<?> getGioviType();

	public abstract GioviValue<T> copy();

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GioviValue<?> other = (GioviValue<?>) obj;
		return Objects.equals(this.value, other.value);
	}

}
