package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.GioviLists;
import it.simonvic.giovi.GioviMaps;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.exceptions.IncompatibleTypes;
import it.simonvic.giovi.lang.type.Natural;

import java.util.List;
import java.util.Map;

public abstract sealed class Value<T> extends GioviObject permits BoolValue, IntValue, ListValue, MapValue, NaturalValue, RealValue, TextValue {

	private Class<?> type;
	protected T value;

	public Value(T value) {
		this.value = value;
		this.type = value.getClass();
	}

	public static <U> Value<?> of(U value) {
		return switch(value) {
			case Natural v -> new NaturalValue(v);
			case Integer v -> new IntValue(v);
			case Double v -> new RealValue(v);
			case Boolean v -> new BoolValue(v);
			case String v -> new TextValue(v);
			case List<?> v -> GioviLists.of(v);
			case Map<?,?> v -> GioviMaps.of(v);
			default -> throw new GioviException("Unknown type " + value);
		};
	}


	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	public void set(Value<?> value) {
		throw new IncompatibleTypes(type, value.getType());
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Value<?> other = (Value<?>) obj;
		return this.value.equals(other.value);
	}
}
