package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.Struct;
import giovi.lang.GioviStruct;
import it.simonvic.giovi.VarD;

public class GioviStructs {

	public static GioviStruct of(String typename) {
		return new GioviStruct(new Struct(typename));
	}

	public static GioviStruct of(
		String typename,
		String i1, VarD<?> v1
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, v1);
		return new GioviStruct(new Struct(typename, s));
	}

	public static GioviStruct of(
		String typename,
		String i1, VarD<?> v1,
		String i2, VarD<?> v2
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		return new GioviStruct(new Struct(typename, s));
	}

	public static GioviStruct of(
		String typename,
		String i1, VarD<?> v1,
		String i2, VarD<?> v2,
		String i3, VarD<?> v3
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		s.put(i3, v3);
		return new GioviStruct(new Struct(typename, s));
	}

	public static GioviStruct of(
		String typename,
		String i1, VarD<?> v1,
		String i2, VarD<?> v2,
		String i3, VarD<?> v3,
		String i4, VarD<?> v4
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		s.put(i3, v3);
		s.put(i4, v4);
		return new GioviStruct(new Struct(typename, s));
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		return new GioviStruct(new Struct(typename, s));
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1,
		String i2, T v2
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		return new GioviStruct(new Struct(typename, s));
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1,
		String i2, T v2,
		String i3, T v3
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		s.put(i3, VarD.of(v3));
		return new GioviStruct(new Struct(typename, s));
	}

	public static <T> GioviStruct ofNative(
		String typename,
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
		return new GioviStruct(new Struct(typename, s));
	}

}
