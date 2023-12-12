package it.simonvic.giovi.lang;

import java.util.LinkedHashMap;
import java.util.List;

import it.simonvic.giovi.lang.values.GioviStruct;
import it.simonvic.giovi.lang.values.GioviValue;
import it.simonvic.giovi.lang.values.natives.Struct;
import it.simonvic.giovi.lang.values.natives.Tuple;

public record GioviStructDefinition(
	String typename,
	LinkedHashMap<String, GioviFieldDefinition> fields)
	implements GioviParserVisitResult {

	public GioviStructDefinition(String typename) {
		this(typename, new LinkedHashMap<>());
	}

	public GioviStructDefinition(String typename, List<GioviFieldDefinition> fields) {
		this(typename, fieldsFromList(fields));
	}

	private static LinkedHashMap<String, GioviFieldDefinition> fieldsFromList(
		List<GioviFieldDefinition> fieldsList) {
		var fields = new LinkedHashMap<String, GioviFieldDefinition>();
		for (var field : fieldsList) {
			fields.put(field.identifier(), field);
		}
		return fields;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder(typename + "<");
		fields.forEach((identifier, field) -> sb.append(field + ","));
		return sb.toString() + ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((typename == null) ? 0 : typename.hashCode());
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GioviStructDefinition other = (GioviStructDefinition) obj;
		if (typename == null) {
			if (other.typename != null) return false;
		} else if (!typename.equals(other.typename)) return false;
		if (fields == null) {
			if (other.fields != null) return false;
		} else if (!fields.equals(other.fields)) return false;
		return true;
	}

	public GioviStruct spawn() {
		var values = new LinkedHashMap<String, GioviValue<?>>();
		fields.forEach((identifier, definition) -> values.put(
			identifier,
			definition.defaultValue().copy()));
		return new GioviStruct(new Struct(typename, new Tuple.Named(values)));
	}

}
