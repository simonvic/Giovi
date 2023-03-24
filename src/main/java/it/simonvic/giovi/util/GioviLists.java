package it.simonvic.giovi.util;

import it.simonvic.giovi.lang.values.GioviList;
import it.simonvic.giovi.lang.values.GioviValue;

import java.util.ArrayList;
import java.util.List;

public class GioviLists {

	public static <T> GioviList of(List<T> values) {
//		if (values.isEmpty()) throw new GioviException("Impossible to infer list type");
		List<GioviValue<?>> entries = new ArrayList<>();
		for (T value : values) {
			entries.add(GioviValue.of(value));
		}
		return new GioviList(entries);
	}

	public static <T> GioviList of(T... values) {
//		if (values.length == 0) throw new GioviException("Impossible to infer list type");
		List<GioviValue<?>> entries = new ArrayList<>();
		for (T value : values) {
			entries.add(GioviValue.of(value));
		}
		return new GioviList(entries);
	}
}
