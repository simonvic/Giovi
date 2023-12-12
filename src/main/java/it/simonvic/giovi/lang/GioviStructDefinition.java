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

	public GioviStruct spawn() {
		var values = new LinkedHashMap<String, GioviValue<?>>();
		fields.forEach((identifier, definition) -> values.put(
			identifier,
			definition.defaultValue().copy()));
		return new GioviStruct(new Struct(typename, new Tuple.Named(values)));
	}

}
