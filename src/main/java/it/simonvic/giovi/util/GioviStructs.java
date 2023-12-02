package it.simonvic.giovi.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.simonvic.giovi.lang.GioviStructDefinition;
import it.simonvic.giovi.lang.primitives.Struct;
import it.simonvic.giovi.lang.values.GioviStructTuple;
import it.simonvic.giovi.lang.values.GioviStructTyped;
import it.simonvic.giovi.lang.values.GioviValue;

public class GioviStructs {

	public static GioviStructTuple of(Map<String, GioviValue<?>> map) {
		return new GioviStructTuple(new Struct.Named(new LinkedHashMap<>(map)));
	}

	public static <T> GioviStructTuple of(List<T> list) {
		return new GioviStructTuple(Structs.of(list));
	}

	public static <T> GioviStructTuple of() {
		return new GioviStructTuple(Structs.of());
	}

	public static <T> GioviStructTuple of(
		String f1, T v1) {
		return new GioviStructTuple(Structs.of(
			f1, v1));
	}

	public static <T> GioviStructTuple of(
		String f1, T v1,
		String f2, T v2) {
		return new GioviStructTuple(Structs.of(
			f1, v1,
			f2, v2));
	}

	public static <T> GioviStructTuple of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3) {
		return new GioviStructTuple(Structs.of(
			f1, v1,
			f2, v2,
			f3, v3));
	}

	public static <T> GioviStructTuple of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4) {
		return new GioviStructTuple(Structs.of(
			f1, v1,
			f2, v2,
			f3, v3,
			f4, v4));
	}

	public static <T> GioviStructTuple of(
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4,
		String f5, T v5) {
		return new GioviStructTuple(Structs.of(
			f1, v1,
			f2, v2,
			f3, v3,
			f4, v4,
			f5, v5));
	}

	public static <T> GioviStructTyped of(
		String typename) {
		return new GioviStructTyped(new GioviStructDefinition(
			typename, Structs.of()));
	}

	public static <T> GioviStructTyped of(
		String typename,
		String f1, T v1) {
		return new GioviStructTyped(new GioviStructDefinition(
			typename, Structs.of(
				f1, v1)));
	}

	public static <T> GioviStructTyped of(
		String typename,
		String f1, T v1,
		String f2, T v2) {
		return new GioviStructTyped(new GioviStructDefinition(
			typename, Structs.of(
				f1, v1,
				f2, v2)));
	}

	public static <T> GioviStructTyped of(
		String typename,
		String f1, T v1,
		String f2, T v2,
		String f3, T v3) {
		return new GioviStructTyped(new GioviStructDefinition(
			typename, Structs.of(
				f1, v1,
				f2, v2,
				f3, v3)));
	}

	public static <T> GioviStructTyped of(
		String typename,
		String f1, T v1,
		String f2, T v2,
		String f3, T v3,
		String f4, T v4) {
		return new GioviStructTyped(new GioviStructDefinition(
			typename, Structs.of(
				f1, v1,
				f2, v2,
				f3, v3,
				f4, v4)));
	}

}
