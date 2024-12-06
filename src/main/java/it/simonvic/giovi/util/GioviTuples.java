package it.simonvic.giovi.util;

import java.util.ArrayList;

import giovi.Tuple;
import giovi.lang.GioviTuple;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.IncompatibleFieldsCount;
import it.simonvic.giovi.exceptions.NoSuchField;

public class GioviTuples {

	public static Tuple signatureFrom(Tuple value) {
		var fields = new ArrayList<VarD<?>>();
		value.forEach(field -> fields
			.add(new VarD<>(field.defaultValue().copy(), field.defaultValue().copy())));
		return new Tuple(fields);
	}

	public static GioviTuple of(Object... fields) {
		var value = Tuples.ofNative(fields);
		return new GioviTuple(signatureFrom(value), value);
	}

	public static GioviTuple redirection(GioviTuple dest, GioviTuple content) {
		if (content.value().getFieldsCount() != dest.value().getFieldsCount()) {
			throw new IncompatibleFieldsCount(dest.value(), content.value());
		}
		var newTuple = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newTuple
			.getField(i).orElseThrow(() -> new NoSuchField(i))
			.setValueWithRedirection(f.value()));
		return new GioviTuple(dest.signature(), newTuple);
	}

}
