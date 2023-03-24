package it.simonvic.giovi.util;

import it.simonvic.giovi.lang.values.GioviMap;
import it.simonvic.giovi.lang.values.GioviValue;

import java.util.LinkedHashMap;
import java.util.Map;

public class GioviMaps {

	public static <K, V> GioviMap of(Map<K, V> map) {
		LinkedHashMap<GioviValue<?>, GioviValue<?>> entries = new LinkedHashMap<>();
		map.forEach((k, v) -> {
			entries.put(GioviValue.of(k), GioviValue.of(v));
		});
		return new GioviMap(entries);
	}

	public static <K, V> GioviMap of(K k1, V v1) {
		var m = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		m.put(GioviValue.of(k1), GioviValue.of(v1));
		return new GioviMap(m);
	}

	public static <K, V> GioviMap of(K k1, V v1, K k2, V v2) {
		var m = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		m.put(GioviValue.of(k1), GioviValue.of(v1));
		m.put(GioviValue.of(k2), GioviValue.of(v2));
		return new GioviMap(m);
	}

	public static <K, V> GioviMap of(K k1, V v1, K k2, V v2, K k3, V v3) {
		var m = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		m.put(GioviValue.of(k1), GioviValue.of(v1));
		m.put(GioviValue.of(k2), GioviValue.of(v2));
		m.put(GioviValue.of(k3), GioviValue.of(v3));
		return new GioviMap(m);
	}

	public static <K, V> GioviMap of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
		var m = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		m.put(GioviValue.of(k1), GioviValue.of(v1));
		m.put(GioviValue.of(k2), GioviValue.of(v2));
		m.put(GioviValue.of(k3), GioviValue.of(v3));
		m.put(GioviValue.of(k4), GioviValue.of(v4));
		return new GioviMap(m);
	}

	public static <K, V> GioviMap of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
		var m = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		m.put(GioviValue.of(k1), GioviValue.of(v1));
		m.put(GioviValue.of(k2), GioviValue.of(v2));
		m.put(GioviValue.of(k3), GioviValue.of(v3));
		m.put(GioviValue.of(k4), GioviValue.of(v4));
		m.put(GioviValue.of(k5), GioviValue.of(v5));
		return new GioviMap(m);
	}

	public static <K, V> GioviMap of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
		var m = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		m.put(GioviValue.of(k1), GioviValue.of(v1));
		m.put(GioviValue.of(k2), GioviValue.of(v2));
		m.put(GioviValue.of(k3), GioviValue.of(v3));
		m.put(GioviValue.of(k4), GioviValue.of(v4));
		m.put(GioviValue.of(k5), GioviValue.of(v5));
		m.put(GioviValue.of(k6), GioviValue.of(v6));
		return new GioviMap(m);
	}
}
