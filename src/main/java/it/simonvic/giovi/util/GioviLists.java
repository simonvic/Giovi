package it.simonvic.giovi.util;

import java.util.ArrayList;
import java.util.List;

import giovi.lang.GioviList;
import giovi.lang.GioviTypeList;
import it.simonvic.giovi.Entry;

public class GioviLists {

	public static <J> GioviList of(List<J> values) {
		var entries = new ArrayList<Entry<?>>();
		for (J value : values) {
			entries.add(Entry.of(value));
		}
		return new GioviList(new GioviTypeList(GioviTypes.inferGioviType(entries)), entries);
	}

	public static <J> GioviList of(J v) {
		return GioviLists.of(List.of(v));
	}

	public static <J> GioviList of(J v, J v2) {
		return GioviLists.of(List.of(v, v2));
	}

	public static <J> GioviList of(J v, J v2, J v3) {
		return GioviLists.of(List.of(v, v2, v3));
	}

	public static <J> GioviList of(J v, J v2, J v3, J v4) {
		return GioviLists.of(List.of(v, v2, v3, v4));
	}

	public static <J> GioviList of(J v, J v2, J v3, J v4, J v5) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5));
	}

	public static <J> GioviList of(J v, J v2, J v3, J v4, J v5, J v6) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6));
	}

	public static <J> GioviList of(J v, J v2, J v3, J v4, J v5, J v6, J v7) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6, v7));
	}

	public static <J> GioviList of(J v, J v2, J v3, J v4, J v5, J v6, J v7, J v8) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6, v7, v8));
	}

	public static <J> GioviList of(J v, J v2, J v3, J v4, J v5, J v6, J v7, J v8, J v9) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6, v7, v8, v8));
	}

	public static <J> GioviList of(J v, J v2, J v3, J v4, J v5, J v6, J v7, J v8, J v9, J v10) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6, v7, v8, v9, v10));
	}

	public static <J> GioviList of(
		J v, J v2, J v3, J v4, J v5, J v6, J v7, J v8, J v9, J v10, J v11
	) {
		return GioviLists.of(List.of(v, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11));
	}

	public static List<Entry<?>> copyOf(List<Entry<?>> list) {
		var copied = new ArrayList<Entry<?>>();
		for (var value : list) {
			copied.add(value.copy());
		}
		return copied;
	}

}
