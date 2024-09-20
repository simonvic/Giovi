package giovi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.exceptions.UnknownTupleField;

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
				VarD<?> otherField = other.getField(entry.getKey());
				if (!entry.getValue().equal(otherField).asNativeBool()) {
					return false;
				}
			} catch (UnknownTupleField | OperatorNotSupported e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public TupleNamed widenTo(TupleNamed to) throws NonWideneable {
		var widenedFields = new LinkedHashMap<String, VarD<?>>();
		for (var widerEntry : to.fields.entrySet()) {
			VarD<?> thisField;
			try {
				thisField = getField(widerEntry.getKey());
			} catch (UnknownTupleField e) {
				throw new NonWideneable(e);
			}
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
			VarD<?> thisField;
			try {
				thisField = getField(widerEntry.getKey());
			} catch (UnknownTupleField e) {
				throw new NonWideneable(e);
			}
			var widerField = widerEntry.getValue();
			widenedFields.put(
				widerEntry.getKey(),
				thisField.widenTo(widerField.getGioviType(), widerField.defaultValue().copy()));
		}
		return new Struct(to.getTypename(), widenedFields);
	}

	public boolean containsField(String identifier) {
		return fields.containsKey(identifier);
	}

	// TODO: use optional
	public VarD<?> getField(String identifier) throws UnknownTupleField {
		var v = fields.get(identifier);
		if (v == null) {
			throw new UnknownTupleField(this, identifier);
		}
		return v;
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

	public void boxField(String fieldIdentifier, GioviValue<?> gioviValue) {
		try {
			getField(fieldIdentifier).setValue(gioviValue);
		} catch (UnknownTupleField e) {
			throw new GioviException(e);
		}
	}

	public void boxField(String fieldIdentifier, VarD<?> var) { // TODO: remove?
		boxField(fieldIdentifier, var.value());
	}

	public void forEach(BiConsumer<String, VarD<?>> valueConsumer) {
		fields.forEach(valueConsumer);
	}

}

