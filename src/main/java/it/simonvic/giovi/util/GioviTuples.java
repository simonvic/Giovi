package it.simonvic.giovi.util;

import giovi.lang.GioviTuple;
import it.simonvic.giovi.exceptions.IncompatibleFieldsCount;
import it.simonvic.giovi.exceptions.NoSuchField;

public class GioviTuples {

	public static GioviTuple of(Object... fields) {
		return new GioviTuple(Tuples.ofNative(fields));
	}

	public static GioviTuple redirection(GioviTuple dest, GioviTuple content) {
		if (content.value().getFieldsCount() != dest.value().getFieldsCount()) {
			throw new IncompatibleFieldsCount(dest.value(), content.value());
		}
		var newTuple = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newTuple
			.getField(i).orElseThrow(() -> new NoSuchField(i))
			.setValueWithRedirection(f.value()));
		return new GioviTuple(newTuple);
	}

}
