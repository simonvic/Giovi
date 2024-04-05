package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.Struct;
import giovi.lang.GioviValue;

public class Structs {

	public static Struct of(String typename) {
		return new Struct(typename);
	}

	public static Struct of(
		String typename,
		String f1, GioviValue<?> v1) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, v1);
		return new Struct(typename, s);
	}

	public static Struct of(
		String typename,
		String f1, GioviValue<?> v1,
		String f2, GioviValue<?> v2) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		return new Struct(typename, s);
	}

	public static Struct of(
		String typename,
		String f1, GioviValue<?> v1,
		String f2, GioviValue<?> v2,
		String f3, GioviValue<?> v3) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		return new Struct(typename, s);
	}

	public static Struct of(
		String typename,
		String f1, GioviValue<?> v1,
		String f2, GioviValue<?> v2,
		String f3, GioviValue<?> v3,
		String f4, GioviValue<?> v4) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		s.put(f4, v4);
		return new Struct(typename, s);
	}

	public static Struct of(
		String typename,
		String f1, GioviValue<?> v1,
		String f2, GioviValue<?> v2,
		String f3, GioviValue<?> v3,
		String f4, GioviValue<?> v4,
		String f5, GioviValue<?> v5) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		s.put(f4, v4);
		s.put(f5, v5);
		return new Struct(typename, s);
	}

	public static <T> Struct ofNative(
		String typename,
		String f1, T v1) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, GioviValue.of(v1));
		return new Struct(typename, s);
	}

	public static <T> Struct ofNative(
		String typename,
		String f1, T v1,
		String f2, T v2) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		return new Struct(typename, s);
	}

	public static <T> Struct ofNative(
		String typename,
		String f1, T v1,
		String f2, T v2,
		String f3, T v3) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		s.put(f3, GioviValue.of(v3));
		return new Struct(typename, s);
	}

	public static <T> Struct ofNative(
		String typename,
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4) {
		var s = new LinkedHashMap<String, GioviValue<?>>();
		s.put(f1, GioviValue.of(v1));
		s.put(f2, GioviValue.of(v2));
		s.put(f3, GioviValue.of(v3));
		s.put(f4, GioviValue.of(v4));
		return new Struct(typename, s);
	}

	public static <T> Struct ofNative(
		String typename,
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
		return new Struct(typename, s);
	}

}
