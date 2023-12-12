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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((definition == null) ? 0 : definition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		GioviTypeStruct other = (GioviTypeStruct) obj;
		if (definition == null) {
			if (other.definition != null) return false;
		} else if (!definition.equals(other.definition)) return false;
		return true;
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		// TODO: clarify requirements. Should subsets be allowed?
		return this.equals(to);
	}

	@Override
	public GioviStruct spawn() {
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
