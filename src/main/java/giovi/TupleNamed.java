package giovi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.UnknownTupleField;

public sealed class TupleNamed extends Tuple permits Struct {

	protected final LinkedHashMap<String, GioviValue<?>> fields;

	public TupleNamed() {
		super();
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
	public TupleNamed widenTo(TupleNamed to) throws NonWideneable {
		var widened = new LinkedHashMap<String, GioviValue<?>>();
		for (var entry : to.fields.entrySet()) {
			GioviValue<?> thisField;
			try {
				thisField = getField(entry.getKey());
			} catch (UnknownTupleField e) {
				throw new NonWideneable(e);
			}
			widened.put(
				entry.getKey(),
				thisField.widenTo(entry.getValue().getGioviType()));
		}
		return new TupleNamed(widened);
	}

	public boolean containsField(String identifier) {
		return fields.containsKey(identifier);
	}

	public GioviValue<?> getField(String identifier) throws UnknownTupleField {
		var v = fields.get(identifier);
		if (v == null) {
			throw new UnknownTupleField(this, identifier);
		}
		return v;
	}

	public List<String> getFieldNames() {
		return new ArrayList<>(fields.keySet());
	}

	public GioviValue<?> get(String identifier) {
		return fields.get(identifier);
	}

	public Set<Entry<String, GioviValue<?>>> entrySet() {
		return fields.entrySet();
	}

	public void forEach(BiConsumer<String, GioviValue<?>> valueConsumer) {
		fields.forEach(valueConsumer);
	}

}

