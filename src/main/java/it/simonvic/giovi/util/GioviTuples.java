package it.simonvic.giovi.util;

import java.util.ArrayList;
import java.util.stream.Stream;

import giovi.Tuple;
import giovi.TupleFieldSignature;
import giovi.TupleSignature;
import giovi.lang.GioviSelf;
import giovi.lang.GioviTuple;
import giovi.lang.GioviType;
import giovi.lang.GioviTypeTuple;
import it.simonvic.giovi.exceptions.IncompatibleFieldsCount;
import it.simonvic.giovi.exceptions.NoSuchField;

public class GioviTuples {

	public static GioviTypeTuple typeFrom(Tuple v) {
		return new GioviTypeTuple(signatureFrom(v));
	}

	// TODO: delete this
	public static TupleSignature signatureFrom(Tuple value) {
		var fields = new ArrayList<TupleFieldSignature>(value.getFieldsCount());
		value.forEach(field -> fields.add(new TupleFieldSignature(
			field.value().getGioviType(),
			field.defaultValue())));
		return new TupleSignature(fields);
	}

	public static GioviTuple of(Object... fields) {
		var value = Tuples.ofNative(fields);
		return new GioviTuple(typeFrom(value), value);
	}

	public static GioviTypeTuple typeOf(GioviType<?>... types) {
		return new GioviTypeTuple(new TupleSignature(Stream.of(types)
			.map(f -> new TupleFieldSignature(f))
			.toList()));
	}

	public static GioviTuple redirection(GioviTuple dest, GioviTuple content) {
		if (content.value().getFieldsCount() != dest.value().getFieldsCount()) {
			throw new IncompatibleFieldsCount(dest.value(), content.value());
		}
		var newTuple = dest.value().copy();
		for (int i = 0; i < content.value().getFieldsCount(); i++) {
			final int j = i;
			var tupleField = content.value().getField(j).orElseThrow();
			var newTupleField = newTuple.getField(j).orElseThrow(() -> new NoSuchField(j));
			if (newTupleField.value() instanceof GioviSelf) {
				newTupleField.setValueUnsafe(dest.type().spawn());
			}
			newTupleField.setValueWithRedirection(tupleField.value());
		}
		return new GioviTuple(dest.type(), newTuple);
	}

}
