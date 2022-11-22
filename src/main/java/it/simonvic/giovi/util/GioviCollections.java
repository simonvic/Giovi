package it.simonvic.giovi.util;

import it.simonvic.giovi.lang.type.GioviType;
import it.simonvic.giovi.lang.values.GioviList;
import it.simonvic.giovi.lang.values.GioviMap;
import it.simonvic.giovi.lang.values.GioviValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GioviCollections {

	public static <T> GioviValue<?> of(List<T> values) {
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

	public static <K,V> GioviMap of(Map<K, V> map) {
		LinkedHashMap<GioviValue<?>, GioviValue<?>> entries = new LinkedHashMap<>();
		map.forEach((k, v) -> {
			entries.put(GioviValue.of(k), GioviValue.of(v));
		});
		return new GioviMap(entries);
	}
}
