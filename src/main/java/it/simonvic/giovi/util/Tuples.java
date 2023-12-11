package it.simonvic.giovi.util;

import java.util.List;

import it.simonvic.giovi.lang.values.GioviValue;
import it.simonvic.giovi.lang.values.natives.Tuple;

public class Tuples {

	public static <T> Tuple.Unnamed of(List<T> list) {
		var s = new Tuple.Unnamed();
		for (T v : list) {
			s.put(GioviValue.of(v));
		}
		return s;
	}

	public static Tuple.Named of() {
		return new Tuple.Named();
	}

	public static <T> Tuple.Named of(
		String f1, T v1) {
		var s = new Tuple.Named();
		s.put(f1, GioviValue.of(v1));
		return s;
	}

	public static <T> Tuple.Named of(
		String f1, T v1,
		String f2, T v2) {
		var s = new Tuple.Named();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		return s;
	}

	public static <T> Tuple.Named of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3) {
		var s = new Tuple.Named();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		s.put(f3, GioviValue.of(v3));
		return s;
	}

	public static <T> Tuple.Named of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4) {
		var s = new Tuple.Named();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		s.put(f3, GioviValue.of(v3));
		s.put(f4, GioviValue.of(v4));
		return s;
	}

	public static <T> Tuple.Named of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4,
		String f5, T v5) {
		var s = new Tuple.Named();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		s.put(f3, GioviValue.of(v3));
		s.put(f4, GioviValue.of(v4));
		s.put(f5, GioviValue.of(v5));
		return s;
	}
}
