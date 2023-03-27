package it.simonvic.giovi.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.simonvic.giovi.lang.primitives.Struct;
import it.simonvic.giovi.lang.values.GioviStructNamed;
import it.simonvic.giovi.lang.values.GioviStruct;
import it.simonvic.giovi.lang.values.GioviStructUnnamed;
import it.simonvic.giovi.lang.values.GioviValue;

public class GioviStructs {

	public static GioviStruct of(Struct v) {
		return switch(v) {
			case Struct.Named s -> new GioviStructNamed(s);
			case Struct.Unnamed s -> new GioviStructUnnamed(s);
		};
	}

	public static GioviStructNamed of(Map<String, GioviValue<?>> map) {
		return new GioviStructNamed(new Struct.Named(new LinkedHashMap<>(map)));
	}

	public static <T> GioviStructUnnamed of(List<T> list) {
		return new GioviStructUnnamed(Structs.of(list));
	}

	public static <T> GioviStructNamed of() {
		return new GioviStructNamed(Structs.of());
	}

	public static <T> GioviStructNamed of(String f1, T v1) {
		return new GioviStructNamed(Structs.of(f1, v1));
	}

	public static <T> GioviStructNamed of(String f1, T v1, String f2, T v2) {
		return new GioviStructNamed(Structs.of(f1, v1, f2, v2));
	}

	public static <T> GioviStructNamed of(String f1, T v1, String f2, T v2, String f3, T v3) {
		return new GioviStructNamed(Structs.of(f1, v1, f2, v2, f3, v3));
	}

	public static <T> GioviStructNamed of(String f1, T v1, String f2, T v2, String f3, T v3, String f4, T v4) {
		return new GioviStructNamed(Structs.of(f1, v1, f2, v2, f3, v3, f4, v4));
	}

	public static <T> GioviStructNamed of(String f1, T v1, String f2, T v2, String f3, T v3, String f4, T v4, String f5, T v5) {
		return new GioviStructNamed(Structs.of(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5));
	}
}
