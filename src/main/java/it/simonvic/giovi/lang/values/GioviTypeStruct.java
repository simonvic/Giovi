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
		return switch (to) {
			case GioviTypeStruct s -> {
				if (to.equals(s)) {
					yield true;
				}
				for (var entry : this.definition.fields().entrySet()) {
					if (!s.definition.fields().containsKey(entry.getKey())) {
						yield false;
					}
					var otherFieldType = s.definition.fields().get(entry.getKey()).type();
					var thisFieldType = entry.getValue().type();
					if (!thisFieldType.equals(otherFieldType)
						|| !thisFieldType.canUpcast(otherFieldType)) {
						yield false;
					}
				}
				yield true;
			}
			case GioviTypeTuple t -> {
				// TODO: what about named tuples?
				var tupleTypes = t.getFieldTypes();
				int tupleSize = tupleTypes.size();
				var thisFields = this.definition.fields().values().stream().toList();
				if (thisFields.size() > tupleSize) {
					yield false;
				}
				for (int i = 0; i < thisFields.size(); i++) {
					var tupleFieldType = tupleTypes.get(i);
					var thisFieldType = thisFields.get(i).type();
					if (!tupleFieldType.equals(thisFieldType)
						|| !tupleFieldType.canUpcast(thisFieldType)) {
						yield false;
					}
				}
				yield true;
			}
			default -> false;
		};
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
