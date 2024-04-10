package giovi;

import java.util.LinkedHashMap;

import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.UnknownTupleField;

public final class Struct extends TupleNamed {

	private final String typename;

	public Struct(String typename, LinkedHashMap<String, GioviValue<?>> fields) {
		super(fields);
		this.typename = typename;
	}

	public Struct(String typename) {
		this.typename = typename;
	}

	public String getTypename() {
		return typename;
	}

	@Override
	public String toString() {
		return typename + super.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((typename == null) ? 0 : typename.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		Struct other = (Struct) obj;
		if (typename == null) {
			if (other.typename != null) return false;
		} else if (!typename.equals(other.typename)) return false;
		return true;
	}

	@Override
	public Struct copy() {
		var a = new LinkedHashMap<String, GioviValue<?>>();
		forEach((identifier, value) -> a.put(identifier, value.copy()));
		return new Struct(typename, a);
	}

	@Override
	public Struct widenTo(Struct to) throws NonWideneable {
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
		return new Struct(typename, widened);
	}

}
