package it.simonvic.giovi.lang.values;

import java.util.List;
import java.util.Map;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.IncompatibleTypes;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.primitives.Natural;
import it.simonvic.giovi.lang.primitives.Struct;
import it.simonvic.giovi.lang.type.GioviType;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviMaps;
import it.simonvic.giovi.util.GioviStructs;

public abstract sealed class GioviValue<T>
	extends GioviObject
	permits GioviNumber, GioviBool, GioviText, GioviCollection, GioviStruct {

	protected T value;
	protected T defaultValue;

	public GioviValue(T value, T defaultValue) {
		this.value = value;
		this.defaultValue = defaultValue;
	}

	public static <U> GioviValue<?> of(U value) {
		return switch (value) {
			case null -> null;
			case Natural v -> new GioviNatural(v);
			case Integer v -> new GioviInt(v);
			case Double v -> new GioviReal(v);
			case Boolean v -> new GioviBool(v);
			case String v -> new GioviText(v);
			case List<?> v -> GioviLists.of(v);
			case Map<?, ?> v -> GioviMaps.of(v);
			case Struct v -> GioviStructs.of(v);
			default -> throw new GioviException("Unknown type " + value);
		};
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



	public GioviType getGioviType() {
		return new GioviType((Class<? extends GioviValue<?>>) this.getClass());
	}

	public GioviValue<?> castTo(Class<? extends GioviValue<?>> type) {
		return castTo(new GioviType(type));
	}

	public GioviValue<?> castTo(GioviType type) {
		throw new GioviException("Cast not implemented: " + this.getGioviType() + " to " + type);
	}

}
