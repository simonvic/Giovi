package giovi;

import java.util.LinkedHashMap;
import java.util.Objects;

import it.simonvic.giovi.GField;

public final class GStruct extends GRecord {

	private final String typename;

	public GStruct(String typename, LinkedHashMap<String, GField<?>> fields) {
		super(fields);
		this.typename = typename;
	}

	public GStruct(String typename) {
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
		GStruct other = (GStruct) obj;
		return Objects.equals(typename, other.typename);
	}

	@Override
	public GStruct copy() {
		var a = new LinkedHashMap<String, GField<?>>(fields.size());
		fields.forEach((identifier, value) -> a.put(identifier, value.copy()));
		return new GStruct(typename, a);
	}

}
