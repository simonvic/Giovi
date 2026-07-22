package it.simonvic.giovi.util;

import java.util.LinkedHashMap;
import java.util.Map;

import giovi.lang.GioviMap;
import giovi.lang.GioviTypeMap;
import it.simonvic.giovi.VarEntry;

public class GioviMaps {

	public static <K, V> GioviMap of(Map<K, V> map) {
		var entries = new LinkedHashMap<VarEntry<?>, VarEntry<?>>();
		map.forEach((k, v) -> entries.put(VarEntry.of(k), VarEntry.of(v)));
		return new GioviMap(
			new GioviTypeMap(
				GioviTypes.inferGioviType(entries.keySet()),
				GioviTypes.inferGioviType(entries.values())
			),
			entries
		);
	}

	public static <K, V> GioviMap of(
		K k1, V v1
	) {
		return GioviMaps.of(
			Map.of(
				k1, v1
			)
		);
	}

	public static <K, V> GioviMap of(
		K k1, V v1,
		K k2, V v2
	) {
		return GioviMaps.of(
			Map.of(
				k1, v1,
				k2, v2
			)
		);
	}

	public static <K, V> GioviMap of(
		K k1, V v1,
		K k2, V v2,
		K k3, V v3
	) {
		return GioviMaps.of(
			Map.of(
				k1, v1,
				k2, v2,
				k3, v3
			)
		);
	}

	public static <K, V> GioviMap of(
		K k1, V v1,
		K k2, V v2,
		K k3, V v3,
		K k4, V v4
	) {
		return GioviMaps.of(
			Map.of(
				k1, v1,
				k2, v2,
				k3, v3,
				k4, v4
			)
		);
	}

	public static <K, V> GioviMap of(
		K k1, V v1,
		K k2, V v2,
		K k3, V v3,
		K k4, V v4,
		K k5, V v5
	) {
		return GioviMaps.of(
			Map.of(
				k1, v1,
				k2, v2,
				k3, v3,
				k4, v4,
				k5, v5
			)
		);
	}

	public static <K, V> GioviMap of(
		K k1, V v1,
		K k2, V v2,
		K k3, V v3,
		K k4, V v4,
		K k5, V v5,
		K k6, V v6
	) {
		return GioviMaps.of(
			Map.of(
				k1, v1,
				k2, v2,
				k3, v3,
				k4, v4,
				k5, v5,
				k6, v6
			)
		);
	}

	public static LinkedHashMap<VarEntry<?>, VarEntry<?>> copyOf(
		LinkedHashMap<VarEntry<?>, VarEntry<?>> map
	) {
		var copied = new LinkedHashMap<VarEntry<?>, VarEntry<?>>();
		map.forEach((k, v) -> copied.put(k.copy(), v.copy()));
		return copied;
	}

}
