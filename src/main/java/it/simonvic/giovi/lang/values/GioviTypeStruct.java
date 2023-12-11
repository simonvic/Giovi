package it.simonvic.giovi.lang.values;

import java.util.LinkedHashMap;

import it.simonvic.giovi.lang.GioviStructDefinition;
import it.simonvic.giovi.lang.values.natives.Struct;
import it.simonvic.giovi.lang.values.natives.Tuple;

public final class GioviTypeStruct extends GioviType<GioviStruct> {

	private final GioviStructDefinition definition;

	public GioviTypeStruct(GioviStructDefinition definition) {
		super(GioviStruct.class);
		this.definition = definition;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder(definition.typename() + "<");
		definition.fields().forEach((identifier, fieldDefinition) -> sb
			.append(fieldDefinition + ","));
		return sb.toString() + ">";
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		return switch (to) {
			default -> false;
		};
	}

	@Override
	public GioviValue<?> spawn() {
		var values = new LinkedHashMap<String, GioviValue<?>>();
		definition.fields().forEach((identifier, field) -> values.put(
			identifier,
			field.defaultValue().copy()));
		return new GioviStruct(new Struct(definition.typename(), new Tuple.Named(values)));
	}

	@Override
	public GioviTypeStruct copy() {
		return new GioviTypeStruct(definition);
	}

}
