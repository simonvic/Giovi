package giovi;

import java.util.*;
import java.util.function.BiConsumer;

import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.OperatorNotSupported;

public sealed class Record extends Tuple permits Struct {

	protected final LinkedHashMap<String, VarD<?>> fields;

	public Record() {
		super();
		this.fields = new LinkedHashMap<>();
	}

	public Record(LinkedHashMap<String, VarD<?>> fields) {
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
		result = prime * result + Objects.hash(fields);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		Record other = (Record) obj;
		return Objects.equals(fields, other.fields);
	}

	@Override
	public Record copy() {
		var a = new LinkedHashMap<String, VarD<?>>(fields.size());
		this.fields.forEach((identifier, value) -> a.put(identifier, value.copy()));
		return new Record(a);
	}

	@Override
	public Record copy(Range<Integer> range) {
		var a = new LinkedHashMap<String, VarD<?>>();
		var fieldNames = new ArrayList<>(this.fields.keySet());
		range.iterate(i -> {
			VarD<?> fieldValue = getField(i).orElseThrow(() -> new NoSuchField(i));
			String fieldName = fieldNames.get(i);
			a.put(fieldName, fieldValue);
		});
		return new Record(a);
	}

	@Override
	public boolean equal(Record other) {
		if (this.fields.size() != other.fields.size()) return false;
		for (var entry : this.fields.entrySet()) {
			try {
				var otherFieldOpt = other.getField(entry.getKey());
				if (otherFieldOpt.isEmpty()) {
					return false;
				}
				if (!entry.getValue().equal(otherFieldOpt.get()).asNativeBool()) {
					return false;
				}
			} catch (OperatorNotSupported e) {
				return false;
			}
		}
		return true;
	}

	public boolean containsField(String identifier) {
		return fields.containsKey(identifier);
	}

	public Optional<VarD<?>> getField(String identifier) {
		return Optional.ofNullable(fields.get(identifier));
	}

	// public <G extends GioviValue<?>> Optional<VarD<G>> getField(String identifier) {
	// 	return Optional.ofNullable((VarD<G>) fields.get(identifier));
	// }

	public List<String> getFieldNames() {
		return new ArrayList<>(fields.keySet());
	}

	public void forEach(BiConsumer<String, VarD<?>> valueConsumer) {
		fields.forEach(valueConsumer);
	}

}

