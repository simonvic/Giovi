package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.TupleNamed;
import giovi.lang.GioviStruct;
import giovi.lang.GioviTuple;
import giovi.lang.GioviTupleNamed;
import giovi.lang.GioviValue;

public class GioviTuples {

	public static GioviTuple of(Object... fields) {
		return new GioviTuple(Tuples.of(fields));
	}

	public static <T> GioviTuple ofNamed(
		String i1, T v1) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		return new GioviTuple(new TupleNamed(s));
	}

	public static <T> GioviTupleNamed ofNamed(
		String i1, T v1,
		String i2, T v2) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		s.put(i2, GioviValue.of(v2));
		return new GioviTupleNamed(new TupleNamed(s));
	}

	public static <T> GioviTupleNamed ofNamed(
		String i1, T v1,
		String i2, T v2,
		String i3, T v3) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		s.put(i2, GioviValue.of(v2));
		s.put(i3, GioviValue.of(v3));
		return new GioviTupleNamed(new TupleNamed(s));
	}

	public static <T> GioviTupleNamed ofNamed(
		String i1, T v1,
		String i2, T v2,
		String i3, T v3,
		String i4, T v4) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		s.put(i2, GioviValue.of(v2));
		s.put(i3, GioviValue.of(v3));
		s.put(i4, GioviValue.of(v4));
		return new GioviTupleNamed(new TupleNamed(s));
	}

	public static GioviTuple redirection(GioviTuple dest, GioviTuple content) {
		var newTuple = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newTuple.boxField(i, f.copy()));
		return new GioviTuple(newTuple, dest.defaultValue());
	}

	public static GioviTuple redirection(GioviTuple dest, GioviTupleNamed content) {
		var newTuple = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newTuple.boxField(i, f.copy()));
		return new GioviTuple(newTuple, dest.defaultValue());
	}

	public static GioviTuple redirection(GioviTuple dest, GioviStruct content) {
		var newTuple = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newTuple.boxField(i, f.copy()));
		return new GioviTuple(newTuple, dest.defaultValue());
	}

	public static GioviTupleNamed redirection(GioviTupleNamed dest, GioviTuple content) {
		var newTupleNamed = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newTupleNamed.boxField(i, f));
		return new GioviTupleNamed(newTupleNamed, dest.defaultValue());
	}

	public static GioviTupleNamed redirection(GioviTupleNamed dest, GioviTupleNamed content) {
		var newTupleNamed = dest.value().copy();
		content.value().forEach((i, f) -> newTupleNamed.boxField(i, f));
		return new GioviTupleNamed(newTupleNamed, dest.defaultValue());
	}

	public static GioviTupleNamed redirection(GioviTupleNamed dest, GioviStruct content) {
		var newTupleNamed = dest.value().copy();
		content.value().forEach((i, f) -> newTupleNamed.boxField(i, f));
		return new GioviTupleNamed(newTupleNamed, dest.defaultValue());
	}

	public static GioviStruct redirection(GioviStruct dest, GioviTuple content) {
		var newStruct = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newStruct.boxField(i, f));
		return new GioviStruct(newStruct, dest.defaultValue());
	}

	public static GioviStruct redirection(GioviStruct dest, GioviTupleNamed content) {
		var newStruct = dest.value().copy();
		content.value().forEach((i, f) -> newStruct.boxField(i, f));
		return new GioviStruct(newStruct, dest.defaultValue());
	}

	public static GioviStruct redirection(GioviStruct dest, GioviStruct content) {
		var newStruct = dest.value().copy();
		content.value().forEach((i, f) -> newStruct.boxField(i, f));
		return new GioviStruct(newStruct, dest.defaultValue());
	}


}
