package it.simonvic.giovi.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import it.simonvic.giovi.lang.values.GioviValue;
import it.simonvic.giovi.lang.values.natives.Tuple;
import it.simonvic.giovi.lang.values.natives.TupleNamed;

public class Tuples {

	public static <T> Tuple of(List<T> list) {
		var l = new ArrayList<GioviValue<?>>();
		for (T v : list) {
			l.add(GioviValue.of(v));
		}
		return new Tuple(l);
	}

	public static TupleNamed of() {
		return new TupleNamed();
	}

	public static <T> TupleNamed of(
		String f1, T v1) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, GioviValue.of(v1));
		return new TupleNamed(s);
	}

	public static <T> TupleNamed of(
		String f1, T v1,
		String f2, T v2) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		return new TupleNamed(s);
	}

	public static <T> TupleNamed of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		s.put(f3, GioviValue.of(v3));
		return new TupleNamed(s);
	}

	public static <T> TupleNamed of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		s.put(f3, GioviValue.of(v3));
		s.put(f4, GioviValue.of(v4));
		return new TupleNamed(s);
	}

	public static <T> TupleNamed of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4,
		String f5, T v5) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		s.put(f3, GioviValue.of(v3));
		s.put(f4, GioviValue.of(v4));
		s.put(f5, GioviValue.of(v5));
		return new TupleNamed(s);
	}

}
