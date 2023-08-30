package it.simonvic.giovi.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.simonvic.giovi.lang.primitives.Struct;
import it.simonvic.giovi.lang.values.GioviStruct;
import it.simonvic.giovi.lang.values.GioviValue;

public class GioviStructs {

	public static GioviStruct of(Map<String, GioviValue<?>> map) {
		return new GioviStruct(new Struct.Named(new LinkedHashMap<>(map)));
	}

	public static <T> GioviStruct of(List<T> list) {
		return new GioviStruct(Structs.of(list));
	}

	public static <T> GioviStruct of() {
		return new GioviStruct(Structs.of());
	}

	public static <T> GioviStruct of(
		String f1, T v1) {
		return new GioviStruct(Structs.of(
			f1, v1));
	}

	public static <T> GioviStruct of(
		String f1, T v1,
		String f2, T v2) {
		return new GioviStruct(Structs.of(
			f1, v1,
			f2, v2));
	}

	public static <T> GioviStruct of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3) {
		return new GioviStruct(Structs.of(
			f1, v1,
			f2, v2,
			f3, v3));
	}

	public static <T> GioviStruct of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4) {
		return new GioviStruct(Structs.of(
			f1, v1,
			f2, v2,
			f3, v3,
			f4, v4));
	}

	public static <T> GioviStruct of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4,
		String f5, T v5) {
		return new GioviStruct(Structs.of(
			f1, v1,
			f2, v2,
			f3, v3,
			f4, v4,
			f5, v5));
	}
}
