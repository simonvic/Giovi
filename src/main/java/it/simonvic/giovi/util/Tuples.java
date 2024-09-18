package it.simonvic.giovi.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import giovi.Tuple;
import giovi.TupleNamed;
import giovi.VarD;

public class Tuples {

	public static Tuple of(Object... fields) {
		var l = new ArrayList<VarD<?>>();
		for (int i = 0; i < fields.length; i++) {
			l.add(VarD.of(fields[i]));
		}
		return new Tuple(l);
	}

	public static <T> TupleNamed ofNamed(
		String f1, T v1) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, VarD.of(v1));
		return new TupleNamed(s);
	}

	public static <T> TupleNamed ofNamed(
		String f1, T v1,
		String f2, T v2) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, VarD.of(v1));
		s.put(f2, VarD.of(v2));
		return new TupleNamed(s);
	}

	public static <T> TupleNamed ofNamed(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, VarD.of(v1));
		s.put(f2, VarD.of(v2));
		s.put(f3, VarD.of(v3));
		return new TupleNamed(s);
	}

	public static <T> TupleNamed ofNamed(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, VarD.of(v1));
		s.put(f2, VarD.of(v2));
		s.put(f3, VarD.of(v3));
		s.put(f4, VarD.of(v4));
		return new TupleNamed(s);
	}

	public static <T> TupleNamed ofNamed(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4,
		String f5, T v5) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, VarD.of(v1));
		s.put(f2, VarD.of(v2));
		s.put(f3, VarD.of(v3));
		s.put(f4, VarD.of(v4));
		s.put(f5, VarD.of(v5));
		return new TupleNamed(s);
	}

}
