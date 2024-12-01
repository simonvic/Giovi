package giovi;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.OperatorNotSupported;

public sealed class TupleNamed extends Tuple permits Struct {

	protected final LinkedHashMap<String, VarD<?>> fields;

	public TupleNamed() {
		super();
		this.fields = new LinkedHashMap<>();
	}

	public TupleNamed(LinkedHashMap<String, VarD<?>> fields) {
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
	public String toStringDefinition() {
		var i = this.entrySet().iterator();
		if (!i.hasNext()) return "<>";
		StringBuilder sb = new StringBuilder("Tuple");
		sb.append('<');
		while (i.hasNext()) {
			var next = i.next();
			sb.append(next.getKey());
			sb.append(" ");
			sb.append(next.getValue().getGioviType());
			sb.append(i.hasNext() ? ", " : ">");
		}
		return sb.toString();
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
		var a = new LinkedHashMap<String, VarD<?>>();
		this.fields.forEach((identifier, value) -> a.put(identifier, value.copy()));
		return new TupleNamed(a);
	}

	@Override
	public boolean equal(TupleNamed other) {
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

	@Override
	public TupleNamed widenTo(TupleNamed to) throws NonWideneable {
		var widenedFields = new LinkedHashMap<String, VarD<?>>();
		for (var widerEntry : to.fields.entrySet()) {
			var thisField = getField(widerEntry.getKey()).orElseThrow(
				() -> new NonWideneable(new NoSuchField(widerEntry.getKey())));
			var widerField = widerEntry.getValue();
			widenedFields.put(
				widerEntry.getKey(),
				thisField.widenTo(widerField.getGioviType(), widerField.defaultValue().copy()));
		}
		return new TupleNamed(widenedFields);
	}

	@Override
	public Struct widenTo(Struct to) throws NonWideneable {
		var widenedFields = new LinkedHashMap<String, VarD<?>>();
		for (var widerEntry : to.fields.entrySet()) {
			var thisField = getField(widerEntry.getKey()).orElseThrow(
				() -> new NonWideneable(new NoSuchField(widerEntry.getKey())));
			var widerField = widerEntry.getValue();
			widenedFields.put(
				widerEntry.getKey(),
				thisField.widenTo(widerField.getGioviType(), widerField.defaultValue().copy()));
		}
		// TODO: this is wrong; fix once we rework Tuple, Record and Struct
		return new Struct(to.getTypename(), widenedFields, to.getConstants(), to.getFunctions());
	}

	public boolean containsField(String identifier) {
		return fields.containsKey(identifier);
	}

	public Optional<VarD<?>> getField(String identifier) {
		return Optional.ofNullable(fields.get(identifier));
	}

	public List<String> getFieldNames() {
		return new ArrayList<>(fields.keySet());
	}

	public VarD<?> get(String identifier) {
		return fields.get(identifier);
	}

	public Set<Entry<String, VarD<?>>> entrySet() {
		return fields.entrySet();
	}

	public void forEach(BiConsumer<String, VarD<?>> valueConsumer) {
		fields.forEach(valueConsumer);
	}

}

