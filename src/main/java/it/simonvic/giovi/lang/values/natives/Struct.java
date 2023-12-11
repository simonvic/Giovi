package it.simonvic.giovi.lang.values.natives;

import java.util.LinkedHashMap;
import java.util.List;

import it.simonvic.giovi.lang.Range;
import it.simonvic.giovi.lang.values.GioviValue;

public final record Struct(String typename, Tuple tuple) {

	public Struct(String typename) {
		this(typename, new Tuple.Named());
	}

	public Struct copy() {
		return new Struct(typename, tuple.copy());
	}

	public Struct copy(Range<Integer> range) {
		var copiedFields = new LinkedHashMap<String, GioviValue<?>>();
		this.tuple.forEach((identifier, value) -> copiedFields.put(identifier, value.copy()));
		return new Struct(typename, new Tuple.Named(copiedFields));
	}

	@Override
	public String toString() {
		return typename + tuple;
	}

	public GioviValue<?> getField(String identifier) {
		return tuple.getField(identifier);
	}

	public GioviValue<?> getField(int index) {
		return tuple.getField(index);
	}

	public void setField(String identifier, GioviValue<?> fieldValue) {
		tuple.getField(identifier).box(fieldValue);
	}

	public void setField(int index, GioviValue<?> fieldValue) {
		tuple.getField(index).box(fieldValue);
	}

	public int getFieldsCount() {
		return tuple.getFieldsCount();
	}

	public boolean containsField(String identifier) {
		return tuple.containsField(identifier);
	}

	public List<String> getFieldNames() {
		return tuple.getFieldNames();
	}

}
