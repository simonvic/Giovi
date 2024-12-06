package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.Record;
import giovi.lang.GioviRecord;
import giovi.lang.GioviTuple;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.IncompatibleFieldsCount;
import it.simonvic.giovi.exceptions.NoSuchField;

public class GioviRecords {

	public static <T> GioviRecord of(
		String i1, T v1
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		return new GioviRecord(new Record(s));
	}

	public static <T> GioviRecord of(
		String i1, T v1,
		String i2, T v2
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		return new GioviRecord(new Record(s));
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
		return new GioviRecord(new Record(s));
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
		return new GioviRecord(new Record(s));
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
		return new GioviRecord(new Record(s));
	}

	public static GioviRecord redirection(GioviRecord dest, GioviTuple content) {
		if (content.value().getFieldsCount() != dest.value().getFieldsCount()) {
			throw new IncompatibleFieldsCount(dest.value(), content.value());
		}
		var newRecord = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newRecord
			.getField(i).orElseThrow(() -> new NoSuchField(i))
			.setValueWithRedirection(f.value()));
		return new GioviRecord(newRecord);
	}

	public static GioviRecord redirection(GioviRecord dest, GioviRecord content) {
		var newRecord = dest.value().copy();
		content.value().forEach((s, f) -> newRecord
			.getField(s).orElseThrow(() -> new NoSuchField(s))
			.setValueWithRedirection(f.value()));
		return new GioviRecord(newRecord);
	}

}
