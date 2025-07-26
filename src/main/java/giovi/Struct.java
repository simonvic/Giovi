package giovi;

import java.util.LinkedHashMap;
import java.util.Objects;

import it.simonvic.giovi.VarD;

public final class Struct extends Record {

	private final String typename;

	public Struct(String typename, LinkedHashMap<String, VarD<?>> fields) {
		super(fields);
		this.typename = typename;
	}

	public Struct(String typename) {
		super();
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(typename);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		Struct other = (Struct) obj;
		return Objects.equals(typename, other.typename);
	}

	@Override
	public Struct copy() {
		var a = new LinkedHashMap<String, VarD<?>>(fields.size());
		fields.forEach((identifier, value) -> a.put(identifier, value.copy()));
		return new Struct(typename, a);
	}

}
