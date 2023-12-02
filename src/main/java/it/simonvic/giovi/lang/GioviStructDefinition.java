package it.simonvic.giovi.lang;

import it.simonvic.giovi.lang.primitives.Struct;

public record GioviStructDefinition(String typename, Struct.Named struct) {

	// TODO: make struct immutable; only allow copy and fields queries

	public boolean isSubset(GioviStructDefinition definition) {
		return struct.isSubset(definition.struct());
	}
}
