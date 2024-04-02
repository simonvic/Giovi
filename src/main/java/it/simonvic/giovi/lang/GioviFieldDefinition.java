package it.simonvic.giovi.lang;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;

public record GioviFieldDefinition(
	String identifier,
	GioviType<?> type,
	GioviValue<?> field) {

	public GioviFieldDefinition(String identifier, GioviType<?> type) {
		this(identifier, type, type.spawn());
	}

	@Override
	public String toString() {
		return identifier + " " + type.getSimpleName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GioviFieldDefinition other = (GioviFieldDefinition) obj;
		if (identifier == null) {
			if (other.identifier != null) return false;
		} else if (!identifier.equals(other.identifier)) return false;
		if (type == null) {
			if (other.type != null) return false;
		} else if (!type.equals(other.type)) return false;
		return true;
	}

}
