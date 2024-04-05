package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.Struct;
import giovi.lang.GioviStruct;
import giovi.lang.GioviValue;

public class GioviStructs {

	public static GioviStruct of(String typename) {
		return new GioviStruct(new Struct(typename));
	}

	public static GioviStruct of(
		String typename,
		String i1, GioviValue<?> v1) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, v1);
		return new GioviStruct(new Struct(typename, s));
	}

	public static GioviStruct of(
		String typename,
		String i1, GioviValue<?> v1,
		String i2, GioviValue<?> v2) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		return new GioviStruct(new Struct(typename, s));
	}

	public static GioviStruct of(
		String typename,
		String i1, GioviValue<?> v1,
		String i2, GioviValue<?> v2,
		String i3, GioviValue<?> v3) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		s.put(i3, v3);
		return new GioviStruct(new Struct(typename, s));
	}

	public static GioviStruct of(
		String typename,
		String i1, GioviValue<?> v1,
		String i2, GioviValue<?> v2,
		String i3, GioviValue<?> v3,
		String i4, GioviValue<?> v4) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		s.put(i3, v3);
		s.put(i4, v4);
		return new GioviStruct(new Struct(typename, s));
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		return new GioviStruct(new Struct(typename, s));
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1,
		String i2, T v2) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		s.put(i2, GioviValue.of(v2));
		return new GioviStruct(new Struct(typename, s));
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1,
		String i2, T v2,
		String i3, T v3) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		s.put(i2, GioviValue.of(v2));
		s.put(i3, GioviValue.of(v3));
		return new GioviStruct(new Struct(typename, s));
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1,
		String i2, T v2,
		String i3, T v3,
		String i4, T v4) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(i1, GioviValue.of(v1));
		s.put(i2, GioviValue.of(v2));
		s.put(i3, GioviValue.of(v3));
		s.put(i4, GioviValue.of(v4));
		return new GioviStruct(new Struct(typename, s));
	}

}
