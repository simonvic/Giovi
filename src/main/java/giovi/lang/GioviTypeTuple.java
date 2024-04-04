package giovi.lang;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import giovi.GioviParserVisitResult;
import giovi.Tuple;
import giovi.TupleNamed;

public final class GioviTypeTuple extends GioviType<GioviTuple> {

	private final List<GioviType<?>> fieldTypes;
	private final LinkedHashMap<String, GioviType<?>> namedFieldTypes;

	public GioviTypeTuple(List<GioviType<?>> fieldTypes) {
		super(GioviTuple.class);
		this.fieldTypes = fieldTypes;
		this.namedFieldTypes = null;
	}

	public GioviTypeTuple(LinkedHashMap<String, GioviType<?>> namedFieldTypes) {
		super(GioviTuple.class);
		this.fieldTypes = null;
		this.namedFieldTypes = namedFieldTypes;
	}

	public GioviTypeTuple() {
		this(new ArrayList<>());
	}

	public List<GioviType<?>> getFieldTypes() {
		return fieldTypes;
	}

	public Optional<LinkedHashMap<String, GioviType<?>>> getNamedFieldTypes() {
		return Optional.ofNullable(namedFieldTypes);
	}

	public int getFieldsCount() {
		return fieldTypes != null
			? fieldTypes.size()
			: namedFieldTypes.size();
	}

	@Override
	public String toString() {
		var sb = new StringBuilder("Tuple<");
		if (fieldTypes != null) {
			fieldTypes.forEach(type -> sb.append(type.getSimpleName() + ","));
		} else {
			namedFieldTypes.forEach(
				(identifier, type) -> sb.append(identifier + " " + type.getSimpleName() + ","));
		}
		return sb + ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fieldTypes == null) ? 0 : fieldTypes.hashCode());
		result = prime * result + ((namedFieldTypes == null) ? 0 : namedFieldTypes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		GioviTypeTuple other = (GioviTypeTuple) obj;
		if (fieldTypes == null) {
			if (other.fieldTypes != null) return false;
		} else if (!fieldTypes.equals(other.fieldTypes)) return false;
		if (namedFieldTypes == null) {
			if (other.namedFieldTypes != null) return false;
		} else if (!namedFieldTypes.equals(other.namedFieldTypes)) return false;
		return true;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return switch (to) {
			case GioviTypeTuple other -> {
				if (this.getFieldsCount() > other.getFieldsCount()) {
					yield false;
				}
				var thisNamedFieldTypes = this.getNamedFieldTypes();
				var otherNamedFieldTypes = other.getNamedFieldTypes();
				if (thisNamedFieldTypes.isPresent() && otherNamedFieldTypes.isPresent()) {
					var thisFieldTypes = thisNamedFieldTypes.get();
					var otherFieldTypes = otherNamedFieldTypes.get();
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
					var thisFieldTypes = this.getFieldTypes();
					var otherFieldTypes = other.getFieldTypes();
					for (int i = 0; i < thisFieldTypes.size(); i++) {
						var thisFieldType = thisFieldTypes.get(i);
						var otherFieldType = otherFieldTypes.get(i);
						if (!thisFieldType.equals(otherFieldType)
							&& !thisFieldType.canWidenTo(otherFieldType)) {
							yield false;
						}
					}
				}
				yield true;
			}
			case GioviTypeStruct other -> {
				if (this.getFieldsCount() > other.getFieldsCount()) {
					yield false;
				}
				var thisNamedFieldTypes = this.getNamedFieldTypes();
				if (thisNamedFieldTypes.isPresent()) {
					var thisFieldTypes = thisNamedFieldTypes.get();
					var otherFieldTypes = other.getNamedFieldTypes();
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
					var thisFieldTypes = this.getFieldTypes();
					var otherFieldTypes = other.getFieldTypes();
					for (int i = 0; i < thisFieldTypes.size(); i++) {
						var thisFieldType = thisFieldTypes.get(i);
						var otherFieldType = otherFieldTypes.get(i);
						if (!thisFieldType.equals(otherFieldType)
							&& !thisFieldType.canWidenTo(otherFieldType)) {
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
	public GioviTuple spawn() {
		if (fieldTypes != null) {
			var fields = new ArrayList<GioviValue<?>>();
			for (var fieldType : fieldTypes) {
				fields.add(fieldType.spawn());
			}
			return new GioviTuple(new Tuple(fields));
		} else {
			var fields = new LinkedHashMap<String, GioviValue<?>>();
			for (var entry : namedFieldTypes.entrySet()) {
				fields.put(entry.getKey(), entry.getValue().spawn());
			}
			return new GioviTuple(new TupleNamed(fields));
		}
	}

	@Override
	public GioviTypeTuple copy() {
		return new GioviTypeTuple(fieldTypes);
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviTuple();
		}
		return super.dot(right);
	}


}

