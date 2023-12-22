package it.simonvic.giovi.lang.values.natives;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;

import it.simonvic.giovi.lang.values.GioviValue;

public sealed class TupleNamed extends Tuple permits Struct {

	private final LinkedHashMap<String, GioviValue<?>> fields;

	public TupleNamed() {
		this.fields = new LinkedHashMap<>();
	}

	public TupleNamed(LinkedHashMap<String, GioviValue<?>> fields) {
		super(new ArrayList<>(fields.values()));
		this.fields = fields;
	}

	@Override
	public String toString() {
		var i = fields.entrySet().iterator();
		if (!i.hasNext())
			return "<>";

		StringBuilder sb = new StringBuilder();
		sb.append('<');
		for (;;) {
			var e = i.next();
			var key = e.getKey();
			var value = e.getValue();
			sb.append(key);
			sb.append(':');
			sb.append(value);
			if (!i.hasNext())
				return sb.append('>').toString();
			sb.append(',').append(' ');
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		TupleNamed other = (TupleNamed) obj;
		if (fields == null) {
			if (other.fields != null) return false;
		} else if (!fields.equals(other.fields)) return false;
		return true;
	}

	@Override
	public TupleNamed copy() {
		var a = new LinkedHashMap<String, GioviValue<?>>();
		this.fields.forEach((identifier, value) -> a.put(identifier, value.copy()));
		return new TupleNamed(a);
	}

	@Override
	public boolean isSubset(Tuple tuple) {
		if (tuple instanceof TupleNamed other) {
			if (this.getFieldsCount() > other.getFieldsCount()) return false;
			for (var fieldEntry : fields.entrySet()) {
				String fieldName = fieldEntry.getKey();
				var fieldValue = fieldEntry.getValue();
				var thisGioviType = fieldValue.getGioviType();
				if (!other.containsField(fieldName)) {
					return false;
				}
				var otherGioviType = other.getField(fieldName).getGioviType();
				if (!thisGioviType.equals(otherGioviType)
					&& !thisGioviType.canUpcast(otherGioviType))
					return false;

			}
			return true;
		}
		return super.isSubset(tuple);
	}

	public boolean containsField(String identifier) {
		return fields.containsKey(identifier);
	}

	public GioviValue<?> getField(String identifier) {
		return fields.get(identifier);
	}

	public List<String> getFieldNames() {
		var names = new ArrayList<String>();
		names.addAll(fields.keySet());
		return names;
	}

	public GioviValue<?> get(String identifier) {
		return fields.get(identifier);
	}

	public void forEach(BiConsumer<String, GioviValue<?>> valueConsumer) {
		fields.forEach(valueConsumer);
	}

}

