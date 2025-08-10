package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.Record;
import giovi.RecordFieldSignature;
import giovi.RecordSignature;
import giovi.lang.*;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.IncompatibleFieldsCount;
import it.simonvic.giovi.exceptions.NoSuchField;

public class GioviRecords {

	public static GioviTypeRecord typeFrom(Record v) {
		return new GioviTypeRecord(signatureFrom(v));
	}

	public static RecordSignature signatureFrom(Record value) {
		var fields = new LinkedHashMap<String, RecordFieldSignature>(value.getFieldsCount());
		value.forEach((identifier, field) -> fields.put(identifier, new RecordFieldSignature(
			identifier,
			field.value().getGioviType(),
			field.defaultValue())));
		return new RecordSignature(fields);
	}

	public static <T> GioviRecord of(
		String i1, T v1
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		var value = new Record(s);
		return new GioviRecord(typeFrom(value), value);
	}

	public static <T> GioviRecord of(
		String i1, T v1,
		String i2, T v2
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		var value = new Record(s);
		return new GioviRecord(typeFrom(value), value);
	}

	public static <T> GioviRecord of(
		String i1, T v1,
		String i2, T v2,
		String i3, T v3
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		s.put(i3, VarD.of(v3));
		var value = new Record(s);
		return new GioviRecord(typeFrom(value), value);
	}

	public static <T> GioviRecord of(
		String i1, T v1,
		String i2, T v2,
		String i3, T v3,
		String i4, T v4
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		s.put(i3, VarD.of(v3));
		s.put(i4, VarD.of(v4));
		var value = new Record(s);
		return new GioviRecord(typeFrom(value), value);
	}

	public static <T> GioviRecord of(
		String i1, T v1,
		String i2, T v2,
		String i3, T v3,
		String i4, T v4,
		String i5, T v5
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		s.put(i3, VarD.of(v3));
		s.put(i4, VarD.of(v4));
		s.put(i5, VarD.of(v5));
		var value = new Record(s);
		return new GioviRecord(typeFrom(value), value);
	}

	public static RecordSignature signatureOf() {
		return new RecordSignature();
	}

	public static RecordSignature signatureOf(
		String i1, GioviType<?> t1
	) {
		return new RecordSignature(
			new RecordFieldSignature(i1, t1));
	}

	public static RecordSignature signatureOf(
		String i1, GioviType<?> t1,
		String i2, GioviType<?> t2
	) {
		return new RecordSignature(
			new RecordFieldSignature(i1, t1),
			new RecordFieldSignature(i2, t2));
	}

	public static RecordSignature signatureOf(
		String i1, GioviType<?> t1,
		String i2, GioviType<?> t2,
		String i3, GioviType<?> t3
	) {
		return new RecordSignature(
			new RecordFieldSignature(i1, t1),
			new RecordFieldSignature(i2, t2),
			new RecordFieldSignature(i3, t3));
	}

	public static RecordSignature signatureOf(
		String i1, GioviType<?> t1,
		String i2, GioviType<?> t2,
		String i3, GioviType<?> t3,
		String i4, GioviType<?> t4
	) {
		return new RecordSignature(
			new RecordFieldSignature(i1, t1),
			new RecordFieldSignature(i2, t2),
			new RecordFieldSignature(i3, t3),
			new RecordFieldSignature(i4, t4));
	}

	public static RecordSignature signatureOf(
		String i1, GioviType<?> t1,
		String i2, GioviType<?> t2,
		String i3, GioviType<?> t3,
		String i4, GioviType<?> t4,
		String i5, GioviType<?> t5
	) {
		return new RecordSignature(
			new RecordFieldSignature(i1, t1),
			new RecordFieldSignature(i2, t2),
			new RecordFieldSignature(i3, t3),
			new RecordFieldSignature(i4, t4),
			new RecordFieldSignature(i5, t5));
	}

	public static GioviRecord redirection(GioviRecord dest, GioviTuple content) {
		if (content.value().getFieldsCount() != dest.value().getFieldsCount()) {
			throw new IncompatibleFieldsCount(dest.value(), content.value());
		}
		var newDest = dest.value().copy();
		for (int i = 0; i < content.value().getFieldsCount(); i++) {
			final int j = i;
			var contentField = content.value().getField(j).orElseThrow();
			var newField = newDest.getField(j).orElseThrow(() -> new NoSuchField(j));
			if (newField.value() instanceof GioviSelf) {
				newField.setValueUnsafe(dest.type().spawn());
			}
			newField.setValueWithRedirection(contentField.value());
		}
		return new GioviRecord(dest.type(), newDest);
	}

	public static GioviRecord redirection(GioviRecord dest, GioviRecord content) {
		var newDest = dest.value().copy();
		for (var contentEntry : content.value().entrySet()) {
			var fieldIdentifier = contentEntry.getKey();
			var contentField = contentEntry.getValue();
			var newField = newDest.getField(fieldIdentifier)
				.orElseThrow(() -> new NoSuchField(fieldIdentifier));
			if (newField.value() instanceof GioviSelf) {
				newField.setValueUnsafe(dest.type().spawn());
			}
			newField.setValueWithRedirection(contentField.value());
		}
		return new GioviRecord(dest.type(), newDest);
	}

}
