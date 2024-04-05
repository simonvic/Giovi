package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.TupleNamed;
import giovi.lang.GioviTuple;
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

	public static <T> GioviTuple ofNamed(
		String i1, T v1,
		String i2, T v2) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		s.put(i2, GioviValue.of(v2));
		return new GioviTuple(new TupleNamed(s));
	}

	public static <T> GioviTuple ofNamed(
		String i1, T v1,
		String i2, T v2,
		String i3, T v3) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		s.put(i2, GioviValue.of(v2));
		s.put(i3, GioviValue.of(v3));
		return new GioviTuple(new TupleNamed(s));
	}

	public static <T> GioviTuple ofNamed(
		String i1, T v1,
		String i2, T v2,
		String i3, T v3,
		String i4, T v4) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		s.put(i2, GioviValue.of(v2));
		s.put(i3, GioviValue.of(v3));
		s.put(i4, GioviValue.of(v4));
		return new GioviTuple(new TupleNamed(s));
	}


}
