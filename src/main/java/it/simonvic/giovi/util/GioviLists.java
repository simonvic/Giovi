package it.simonvic.giovi.util;

import it.simonvic.giovi.lang.values.GioviList;
import it.simonvic.giovi.lang.values.GioviValue;

import java.util.ArrayList;
import java.util.List;

public class GioviLists {

	public static <T> GioviList of(List<T> values) {
		// if (values.isEmpty()) throw new GioviException("Impossible to infer list type");
		List<GioviValue<?>> entries = new ArrayList<>();
		for (T value : values) {
			entries.add(GioviValue.of(value));
		}
		return new GioviList(entries);
	}

	public static <T> GioviList of(T v) {
		return new GioviList(List.of(GioviValue.of(v)));
	}

	public static <T> GioviList of(T v, T v2) {
		return new GioviList(List.of(
			GioviValue.of(v),
			GioviValue.of(v2)));
	}

	public static <T> GioviList of(T v, T v2, T v3) {
		return new GioviList(List.of(
			GioviValue.of(v),
			GioviValue.of(v2),
			GioviValue.of(v3)));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4) {
		return new GioviList(List.of(
			GioviValue.of(v),
			GioviValue.of(v2),
			GioviValue.of(v3),
			GioviValue.of(v4)));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5) {
		return new GioviList(List.of(
			GioviValue.of(v),
			GioviValue.of(v2),
			GioviValue.of(v3),
			GioviValue.of(v4),
			GioviValue.of(v5)));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6) {
		return new GioviList(List.of(
			GioviValue.of(v),
			GioviValue.of(v2),
			GioviValue.of(v3),
			GioviValue.of(v4),
			GioviValue.of(v5),
			GioviValue.of(v6)));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6, T v7) {
		return new GioviList(List.of(
			GioviValue.of(v),
			GioviValue.of(v2),
			GioviValue.of(v3),
			GioviValue.of(v4),
			GioviValue.of(v5),
			GioviValue.of(v6),
			GioviValue.of(v7)));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6, T v7, T v8) {
		return new GioviList(List.of(
			GioviValue.of(v),
			GioviValue.of(v2),
			GioviValue.of(v3),
			GioviValue.of(v4),
			GioviValue.of(v5),
			GioviValue.of(v6),
			GioviValue.of(v7),
			GioviValue.of(v8)));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6, T v7, T v8, T v9) {
		return new GioviList(List.of(
			GioviValue.of(v),
			GioviValue.of(v2),
			GioviValue.of(v3),
			GioviValue.of(v4),
			GioviValue.of(v5),
			GioviValue.of(v6),
			GioviValue.of(v7),
			GioviValue.of(v8),
			GioviValue.of(v8)));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6, T v7, T v8, T v9, T v10) {
		return new GioviList(List.of(
			GioviValue.of(v),
			GioviValue.of(v2),
			GioviValue.of(v3),
			GioviValue.of(v4),
			GioviValue.of(v5),
			GioviValue.of(v6),
			GioviValue.of(v7),
			GioviValue.of(v8),
			GioviValue.of(v9),
			GioviValue.of(v10)));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6, T v7, T v8, T v9, T v10,
		T v11) {
		return new GioviList(List.of(
			GioviValue.of(v),
			GioviValue.of(v2),
			GioviValue.of(v3),
			GioviValue.of(v4),
			GioviValue.of(v5),
			GioviValue.of(v6),
			GioviValue.of(v7),
			GioviValue.of(v8),
			GioviValue.of(v9),
			GioviValue.of(v10),
			GioviValue.of(v11)));
	}

}
