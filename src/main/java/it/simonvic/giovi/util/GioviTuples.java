package it.simonvic.giovi.util;

import java.util.ArrayList;
import java.util.stream.Stream;

import giovi.GTuple;
import giovi.TupleFieldSignature;
import giovi.TupleSignature;
import giovi.lang.GioviSelf;
import giovi.lang.GioviTuple;
import giovi.lang.GioviType;
import giovi.lang.GioviTypeTuple;
import it.simonvic.giovi.exceptions.IncompatibleFieldsCount;
import it.simonvic.giovi.exceptions.NoSuchField;

public class GioviTuples {

	public static GioviTypeTuple typeFrom(GTuple v) {
		return new GioviTypeTuple(signatureFrom(v));
	}

	// TODO: delete this
	public static TupleSignature signatureFrom(GTuple value) {
		var fields = new ArrayList<TupleFieldSignature>(value.getFieldsCount());
		value.forEach(
			field -> fields.add(
				new TupleFieldSignature(
					field.unwrap().getGioviType(),
					field.defaultValue()
				)
			)
		);
		return new TupleSignature(fields);
	}

	public static GioviTuple of(Object... fields) {
		var value = GTuples.ofNative(fields);
		return new GioviTuple(typeFrom(value), value);
	}

	public static GioviTypeTuple typeOf(GioviType<?>... types) {
		return new GioviTypeTuple(
			new TupleSignature(
				Stream.of(types)
					.map(f -> new TupleFieldSignature(f))
					.toList()
			)
		);
	}

	public static GioviTuple redirection(GioviTuple dest, GioviTuple content) {
		if (content.value().getFieldsCount() != dest.value().getFieldsCount()) {
			throw new IncompatibleFieldsCount(dest.value(), content.value());
		}
		var newDest = dest.value().copy();
		for (int i = 0; i < content.value().getFieldsCount(); i++) {
			final int j = i;
			var contentField = content.value().getField(j).orElseThrow();
			var newField = newDest.getField(j).orElseThrow(() -> new NoSuchField(j));
			if (newField.unwrap() instanceof GioviSelf) {
				newField.setValueUnsafe(dest.type().spawn());
			}
			newField.setValueWithRedirection(contentField.unwrap());
		}
		return new GioviTuple(dest.type(), newDest);
	}

}
