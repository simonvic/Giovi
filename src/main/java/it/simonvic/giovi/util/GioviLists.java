package it.simonvic.giovi.util;

import java.util.ArrayList;
import java.util.List;

import giovi.lang.GioviList;
import it.simonvic.giovi.Var;

public class GioviLists {

	public static <T> GioviList of(List<T> values) {
		var entries = new ArrayList<Var<?>>();
		for (T value : values) {
			entries.add(Var.of(value));
		}
		return new GioviList(GioviTypes.inferGioviType(entries), entries);
	}

	public static <T> GioviList of(T v) {
		return GioviLists.of(List.of(v));
	}

	public static <T> GioviList of(T v, T v2) {
		return GioviLists.of(List.of(v, v2));
	}

	public static <T> GioviList of(T v, T v2, T v3) {
		return GioviLists.of(List.of(v, v2, v3));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4) {
		return GioviLists.of(List.of(v, v2, v3, v4));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6, T v7) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6, v7));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6, T v7, T v8) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6, v7, v8));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6, T v7, T v8, T v9) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6, v7, v8, v8));
	}

	public static <T> GioviList of(T v, T v2, T v3, T v4, T v5, T v6, T v7, T v8, T v9, T v10) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6, v7, v8, v9, v10));
	}

	public static <T> GioviList of(
		T v, T v2, T v3, T v4, T v5, T v6, T v7, T v8, T v9, T v10, T v11
	) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11));
	}

	public static List<Var<?>> copyOf(List<Var<?>> list) {
		var copied = new ArrayList<Var<?>>();
		for (var value : list) {
			copied.add(value.copy());
		}
		return copied;
	}

}
