package it.simonvic.giovi.util;

import it.simonvic.giovi.lang.values.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GioviStructs {

	public static GioviStruct of(Struct v) {
		return switch(v) {
			case Struct.Named s -> new GioviNamedStruct(s);
			case Struct.Unnamed s -> new GioviUnnamedStruct(s);
		};
	}

	public static GioviNamedStruct of(Map<String, GioviValue<?>> map) {
		return new GioviNamedStruct(new Struct.Named(new LinkedHashMap<>(map)));
	}

	public static <T> GioviUnnamedStruct of(List<T> list) {
		return new GioviUnnamedStruct(Structs.of(list));
	}

	public static <T> GioviNamedStruct of() {
		return new GioviNamedStruct(Structs.of());
	}

	public static <T> GioviNamedStruct of(String f1, T v1) {
		return new GioviNamedStruct(Structs.of(f1, v1));
	}

	public static <T> GioviNamedStruct of(String f1, T v1, String f2, T v2) {
		return new GioviNamedStruct(Structs.of(f1, v1, f2, v2));
	}

	public static <T> GioviNamedStruct of(String f1, T v1, String f2, T v2, String f3, T v3) {
		return new GioviNamedStruct(Structs.of(f1, v1, f2, v2, f3, v3));
	}

	public static <T> GioviNamedStruct of(String f1, T v1, String f2, T v2, String f3, T v3, String f4, T v4) {
		return new GioviNamedStruct(Structs.of(f1, v1, f2, v2, f3, v3, f4, v4));
	}

	public static <T> GioviNamedStruct of(String f1, T v1, String f2, T v2, String f3, T v3, String f4, T v4, String f5, T v5) {
		return new GioviNamedStruct(Structs.of(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5));
	}
}
