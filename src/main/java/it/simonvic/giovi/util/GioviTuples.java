package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.TupleNamed;
import giovi.lang.GioviStruct;
import giovi.lang.GioviTuple;
import giovi.lang.GioviTupleNamed;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NoSuchField;

public class GioviTuples {

	public static GioviTuple of(Object... fields) {
		return new GioviTuple(Tuples.ofNative(fields));
	}

	public static <T> GioviTuple ofNamed(
		String i1, T v1) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		return new GioviTuple(new TupleNamed(s));
	}

	public static <T> GioviTupleNamed ofNamed(
		String i1, T v1,
		String i2, T v2) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		return new GioviTupleNamed(new TupleNamed(s));
	}

	public static <T> GioviTupleNamed ofNamed(
		String i1, T v1,
		String i2, T v2,
		String i3, T v3) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		s.put(i3, VarD.of(v3));
		return new GioviTupleNamed(new TupleNamed(s));
	}

	public static <T> GioviTupleNamed ofNamed(
		String i1, T v1,
		String i2, T v2,
		String i3, T v3,
		String i4, T v4) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		s.put(i3, VarD.of(v3));
		s.put(i4, VarD.of(v4));
		return new GioviTupleNamed(new TupleNamed(s));
	}

	public static GioviTuple redirection(GioviTuple dest, GioviTuple content) {
		var newTuple = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newTuple
			.getField(i).orElseThrow(() -> new NoSuchField(i))
			.setValue(f.value()));
		return new GioviTuple(newTuple);
	}

	public static GioviTuple redirection(GioviTuple dest, GioviTupleNamed content) {
		var newTuple = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newTuple
			.getField(i).orElseThrow(() -> new NoSuchField(i))
			.setValue(f.value()));
		return new GioviTuple(newTuple);
	}

	public static GioviTuple redirection(GioviTuple dest, GioviStruct content) {
		var newTuple = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newTuple
			.getField(i).orElseThrow(() -> new NoSuchField(i))
			.setValue(f.value()));
		return new GioviTuple(newTuple);
	}

	public static GioviTupleNamed redirection(GioviTupleNamed dest, GioviTuple content) {
		var newTupleNamed = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newTupleNamed
			.getField(i).orElseThrow(() -> new NoSuchField(i))
			.setValue(f.value()));
		return new GioviTupleNamed(newTupleNamed);
	}

	public static GioviTupleNamed redirection(GioviTupleNamed dest, GioviTupleNamed content) {
		var newTupleNamed = dest.value().copy();
		content.value().forEach((s, f) -> newTupleNamed
			.getField(s).orElseThrow(() -> new NoSuchField(s))
			.setValue(f.value()));
		return new GioviTupleNamed(newTupleNamed);
	}

	public static GioviTupleNamed redirection(GioviTupleNamed dest, GioviStruct content) {
		var newTupleNamed = dest.value().copy();
		content.value().forEach((s, f) -> newTupleNamed
			.getField(s).orElseThrow(() -> new NoSuchField(s))
			.setValue(f.value()));
		return new GioviTupleNamed(newTupleNamed);
	}

	public static GioviStruct redirection(GioviStruct dest, GioviTuple content) {
		var newStruct = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newStruct
			.getField(i).orElseThrow(() -> new NoSuchField(i))
			.setValue(f.value()));
		return new GioviStruct(newStruct);
	}

	public static GioviStruct redirection(GioviStruct dest, GioviTupleNamed content) {
		var newStruct = dest.value().copy();
		content.value().forEach((s, f) -> newStruct
			.getField(s).orElseThrow(() -> new NoSuchField(s))
			.setValue(f.value()));
		return new GioviStruct(newStruct);
	}

	public static GioviStruct redirection(GioviStruct dest, GioviStruct content) {
		var newStruct = dest.value().copy();
		content.value().forEach((s, f) -> newStruct
			.getField(s).orElseThrow(() -> new NoSuchField(s))
			.setValue(f.value()));
		return new GioviStruct(newStruct);
	}


}
