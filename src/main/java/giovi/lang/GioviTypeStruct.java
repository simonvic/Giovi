package giovi.lang;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import giovi.GioviParserVisitResult;
import giovi.Struct;
import it.simonvic.giovi.lang.GioviStructDefinition;

public final class GioviTypeStruct extends GioviType<GioviStruct> {

	private final GioviStructDefinition definition;

	public GioviTypeStruct(GioviStructDefinition definition) {
		super(GioviStruct.class);
		this.definition = definition;
	}

	public int getFieldsCount() {
		return definition.fields().size();
	}

	public List<GioviType<?>> getFieldTypes() {
		var fieldTypes = new ArrayList<GioviType<?>>();
		definition.fields()
			.forEach((__, fieldDefinition) -> fieldTypes.add(fieldDefinition.type()));
		return fieldTypes;
	}

	public LinkedHashMap<String, GioviType<?>> getNamedFieldTypes() {
		var fieldTypes = new LinkedHashMap<String, GioviType<?>>();
		definition.fields().forEach(
			(identifier, fieldDefinition) -> fieldTypes.put(identifier, fieldDefinition.type()));
		return fieldTypes;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder(definition.typename() + "<");
		definition.fields().forEach((identifier, fieldDefinition) -> sb
			.append(fieldDefinition + ","));
		return sb + ">";
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
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStruct s -> {
				for (var entry : this.definition.fields().entrySet()) {
					if (!s.definition.fields().containsKey(entry.getKey())) {
						yield false;
					}
					var otherFieldType = s.definition.fields().get(entry.getKey()).type();
					var thisFieldType = entry.getValue().type();
					if (!thisFieldType.equals(otherFieldType)
						&& !thisFieldType.canWidenTo(otherFieldType)) {
						yield false;
					}
				}
				yield true;
			}
			case GioviTypeTuple other -> {
				if (this.getFieldsCount() > other.getFieldsCount()) {
					yield false;
				}
				var otherNamedFieldTypes = other.getNamedFieldTypes();
				if (otherNamedFieldTypes.isPresent()) {
					var otherFieldTypes = otherNamedFieldTypes.get();
					var thisFieldTypes = this.getNamedFieldTypes();
					for (var entry : thisFieldTypes.entrySet()) {
						if (!otherFieldTypes.containsKey(entry.getKey())) {
							yield false;
						}
						var thisFieldType = entry.getValue();
						var otherFieldType = otherFieldTypes.get(entry.getKey());
						if (!thisFieldType.equals(otherFieldType)
							&& !thisFieldType.canWidenTo(otherFieldType)) {
							yield false;
						}
					}
				} else {
					var tupleTypes = other.getFieldTypes();
					var thisFields = this.definition.fields().values().stream().toList();
					for (int i = 0; i < thisFields.size(); i++) {
						var tupleFieldType = tupleTypes.get(i);
						var thisFieldType = thisFields.get(i).type();
						if (!tupleFieldType.equals(thisFieldType)
							&& !tupleFieldType.canWidenTo(thisFieldType)) {
							yield false;
						}
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
		definition.fields().forEach((identifier, fieldDefinition) -> values.put(
			identifier,
			fieldDefinition.field().copy()));
		return new GioviStruct(new Struct(definition.typename(), values));
	}

	@Override
	public GioviTypeStruct copy() {
		return new GioviTypeStruct(definition);
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return definition.spawn();
		}
		return super.dot(right);
	}


}
