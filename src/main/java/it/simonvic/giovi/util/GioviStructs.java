package it.simonvic.giovi.util;

import it.simonvic.giovi.lang.values.GioviStruct;
import it.simonvic.giovi.lang.values.natives.Struct;

public class GioviStructs {

	public static GioviStruct of(String typename) {
		return new GioviStruct(new Struct(typename));
	}

	public static <T> GioviStruct of(
		String typename,
		String i1, T v1
	) {
		return new GioviStruct(new Struct(typename, Tuples.of(
			i1, v1
		)));
	}

	public static <T> GioviStruct of(
		String typename,
		String i1, T v1,
		String i2, T v2
	) {
		return new GioviStruct(new Struct(typename, Tuples.of(
			i1, v1,
			i2, v2
		)));
	}

	public static <T> GioviStruct of(
		String typename,
		String i1, T v1,
		String i2, T v2,
		String i3, T v3
	) {
		return new GioviStruct(new Struct(typename, Tuples.of(
			i1, v1,
			i2, v2,
			i3, v3
		)));
	}

	public static <T> GioviStruct of(
		String typename,
		String i1, T v1,
		String i2, T v2,
		String i3, T v3,
		String i4, T v4
	) {
		return new GioviStruct(new Struct(typename, Tuples.of(
			i1, v1,
			i2, v2,
			i3, v3,
			i4, v4
		)));
	}

}
