package it.simonvic.giovi.util;

import java.util.List;

import giovi.lang.GioviTuple;

public class GioviTuples {

	public static <T> GioviTuple of(List<T> list) {
		return new GioviTuple(Tuples.of(list));
	}

	public static GioviTuple of(
		Object v1) {
		return new GioviTuple(Tuples.of(List.of(v1)));
	}

	public static GioviTuple of(
		Object v1,
		Object v2) {
		return new GioviTuple(Tuples.of(List.of(v1, v2)));
	}

	public static GioviTuple of(
		Object v1,
		Object v2,
		Object v3) {
		return new GioviTuple(Tuples.of(List.of(v1, v2, v3)));
	}

	public static GioviTuple of(
		Object v1,
		Object v2,
		Object v3,
		Object v4) {
		return new GioviTuple(Tuples.of(List.of(v1, v2, v3, v4)));
	}

	public static GioviTuple of(
		Object v1,
		Object v2,
		Object v3,
		Object v4,
		Object v5) {
		return new GioviTuple(Tuples.of(List.of(v1, v2, v3, v4, v5)));
	}

}
