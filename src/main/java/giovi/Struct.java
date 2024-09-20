package giovi;

import java.util.LinkedHashMap;

import it.simonvic.giovi.exceptions.NonWideneable;

public final class Struct extends TupleNamed {

	private final String typename;

	public Struct(String typename, LinkedHashMap<String, VarD<?>> fields) {
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
		return typename + "." + super.toString();
	}

	@Override
	public String toStringDefinition() {
		var i = this.entrySet().iterator();
		if (!i.hasNext()) return "<>";
		StringBuilder sb = new StringBuilder(typename);
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
		var a = new LinkedHashMap<String, VarD<?>>();
		forEach((identifier, value) -> a.put(identifier, value.copy()));
		return new Struct(typename, a);
	}

	@Override
	public Struct widenTo(Struct to) throws NonWideneable {
		if (!to.typename.equals(this.typename)) {
			throw new NonWideneable("Can't widen " + this.typename + " to " + to.typename);
		}
		return super.widenTo(to);
	}

}
