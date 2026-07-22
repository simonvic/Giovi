package giovi;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import giovi.lang.GioviValue;
import it.simonvic.giovi.Member;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.OperatorNotSupported;

public sealed class GRecord extends GTuple permits GStruct {

	protected final LinkedHashMap<String, Member<?>> fields;

	public GRecord() {
		super();
		this.fields = new LinkedHashMap<>();
	}

	public GRecord(LinkedHashMap<String, Member<?>> fields) {
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
		GRecord other = (GRecord) obj;
		return Objects.equals(fields, other.fields);
	}

	@Override
	public GRecord copy() {
		var a = new LinkedHashMap<String, Member<?>>(fields.size());
		this.fields.forEach((identifier, value) -> a.put(identifier, value.copy()));
		return new GRecord(a);
	}

	@Override
	public GRecord copy(Range<Integer> range) {
		var a = new LinkedHashMap<String, Member<?>>();
		var fieldNames = new ArrayList<>(this.fields.keySet());
		range.iterate(i -> {
			Member<?> fieldValue = getField(i).orElseThrow(() -> new NoSuchField(i));
			String fieldName = fieldNames.get(i);
			a.put(fieldName, fieldValue);
		});
		return new GRecord(a);
	}

	@Override
	public GRecord as(RecordSignature signature) {
		var casted = signature.spawn();
		casted.forEach((fieldName, castedField) -> {
			this.getField(fieldName).ifPresent(
				field -> castedField.setValueWithRedirection(
					field.as(castedField.getGioviType())
				)
			);
		});
		return casted;
	}

	@Override
	public GStruct as(StructSignature signature) {
		var casted = signature.spawn();
		casted.forEach((fieldName, castedField) -> {
			this.getField(fieldName).ifPresent(
				field -> castedField.setValueWithRedirection(
					field.as(castedField.getGioviType())
				)
			);
		});
		return casted;
	}

	@Override
	public boolean equal(GRecord other) {
		if (this.fields.size() != other.fields.size()) return false;
		for (var entry : this.fields.entrySet()) {
			try {
				var otherFieldOpt = other.getField(entry.getKey());
				if (otherFieldOpt.isEmpty()) {
					return false;
				}
				if (!entry.getValue().equal(otherFieldOpt.orElseThrow()).asNativeBool()) {
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

	public Optional<Member<?>> getField(String identifier) {
		return Optional.ofNullable(fields.get(identifier));
	}

	@SuppressWarnings("unchecked")
	public <G extends GioviValue<?>> Optional<Member<G>> getField(String identifier, Class<G> clazz) {
		var field = fields.get(identifier);
		if (field == null) {
			return Optional.empty();
		}
		clazz.cast(field.unwrap());
		// TODO: return empty if class cast exception?
		return Optional.of((Member<G>) field);
	}

	public List<String> getFieldNames() {
		return new ArrayList<>(fields.keySet());
	}

	public void forEach(BiConsumer<String, Member<?>> valueConsumer) {
		fields.forEach(valueConsumer);
	}

	public Set<Entry<String, Member<?>>> entrySet() {
		return this.fields.entrySet();
	}

}

