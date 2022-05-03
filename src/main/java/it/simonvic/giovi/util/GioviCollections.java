package it.simonvic.giovi.util;

import it.simonvic.giovi.lang.values.GioviList;
import it.simonvic.giovi.lang.values.GioviMap;
import it.simonvic.giovi.lang.values.GioviValue;

import java.util.List;
import java.util.Map;

public class GioviCollections {

	public static GioviValue<?> of(List<?> values) {
		GioviList<GioviValue<?>> list = new GioviList<>();
		values.forEach(value -> list.add(GioviValue.of(value)));
		return list;
	}

	public static <T> GioviList<GioviValue<?>> of(T... values) {
		GioviList<GioviValue<?>> list = new GioviList<>();
		for (T value : values) {
			list.add(GioviValue.of(value));
		}
		return list;
	}

	public static GioviValue<?> of(Map<?, ?> map) {
		GioviMap<GioviValue<?>, GioviValue<?>> mapValue = new GioviMap<>();
		map.forEach((k, v) -> {
			mapValue.put(GioviValue.of(k), GioviValue.of(v));
		});
		return mapValue;
	}
}
