package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.exceptions.IncompatibleTypes;

public class Value<T> extends GioviObject {

	private Class<?> type;
	protected T value;

	public Value(T value) {
		this.value = value;
		this.type = value.getClass();
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
}
