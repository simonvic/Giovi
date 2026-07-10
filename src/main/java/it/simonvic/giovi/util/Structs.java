package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.GStruct;
import it.simonvic.giovi.VarD;

public class Structs {

	public static GStruct of(String typename) {
		return new GStruct(typename);
	}

	public static GStruct of(
		String typename,
		String f1, VarD<?> v1
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, v1);
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
		String f1, VarD<?> v1,
		String f2, VarD<?> v2
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
		String f1, VarD<?> v1,
		String f2, VarD<?> v2,
		String f3, VarD<?> v3
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
		String f1, VarD<?> v1,
		String f2, VarD<?> v2,
		String f3, VarD<?> v3,
		String f4, VarD<?> v4
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		s.put(f4, v4);
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
		String f1, VarD<?> v1,
		String f2, VarD<?> v2,
		String f3, VarD<?> v3,
		String f4, VarD<?> v4,
		String f5, VarD<?> v5
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		s.put(f4, v4);
		s.put(f5, v5);
		return new GStruct(typename, s);
	}

	public static <T> GStruct ofNative(
		String typename,
		String f1, T v1
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, VarD.of(v1));
		return new GStruct(typename, s);
	}

	public static <T> GStruct ofNative(
		String typename,
		String f1, T v1,
		String f2, T v2
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, VarD.of(v1));
		s.put(f2, VarD.of(v2));
		return new GStruct(typename, s);
	}

	public static <T> GStruct ofNative(
		String typename,
		String f1, T v1,
		String f2, T v2,
		String f3, T v3
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, VarD.of(v1));
		s.put(f2, VarD.of(v2));
		s.put(f3, VarD.of(v3));
		return new GStruct(typename, s);
	}

	public static <T> GStruct ofNative(
		String typename,
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, VarD.of(v1));
		s.put(f2, VarD.of(v2));
		s.put(f3, VarD.of(v3));
		s.put(f4, VarD.of(v4));
		return new GStruct(typename, s);
	}

	public static <T> GStruct ofNative(
		String typename,
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4,
		String f5, T v5
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(f1, VarD.of(v1));
		s.put(f2, VarD.of(v2));
		s.put(f3, VarD.of(v3));
		s.put(f4, VarD.of(v4));
		s.put(f5, VarD.of(v5));
		return new GStruct(typename, s);
	}

}
