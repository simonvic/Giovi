package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.IncompatibleTypes;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.util.GioviCollections;

import java.util.List;
import java.util.Map;

public abstract sealed class GioviValue<T> extends GioviObject permits GioviNumber, GioviBool, GioviText, GioviList, GioviMap {

	private Class<?> type;
	protected T value;
	protected T defaultValue;

	public GioviValue(T value, T defaultValue) {
		this.value = value;
		this.defaultValue = defaultValue;
		this.type = value.getClass();
	}

	public static <U> GioviValue<?> of(U value) {
		return switch (value) {
			case Natural v -> new GioviNatural(v);
			case Integer v -> new GioviInt(v);
			case Double v -> new GioviReal(v);
			case Boolean v -> new GioviBool(v);
			case String v -> new GioviText(v);
			case List<?> v -> GioviCollections.of(v);
			case Map<?, ?> v -> GioviCollections.of(v);
			default -> throw new GioviException("Unknown type " + value);
		};
	}


	public Class<?> getType() {
		return type;
	}

	public T unbox() {
		return value;
	}

	public void box(T value) {
		this.value = value;
	}

	public void box(GioviValue<?> value) {
		throw new IncompatibleTypes(type, value.getType());
	}


	public T getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(GioviValue<?> defaultValue) {
		throw new IncompatibleTypes(type, defaultValue.getType());
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
		final GioviValue<?> other = (GioviValue<?>) obj;
		return this.value.equals(other.value);
	}
}
