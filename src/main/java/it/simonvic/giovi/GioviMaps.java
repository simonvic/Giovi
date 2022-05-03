package it.simonvic.giovi;

import it.simonvic.giovi.lang.values.MapValue;
import it.simonvic.giovi.lang.values.Value;

import java.util.Map;

public class GioviMaps {

	public static <K, V> MapValue<Value<?>, Value<?>> of(K k1, V v1, K k2, V v2, K k3, V v3) {
		MapValue<Value<?>, Value<?>> map = new MapValue<>();
		map.put(Value.of(k1), Value.of(v1));
		map.put(Value.of(k2), Value.of(v2));
		map.put(Value.of(k3), Value.of(v3));
		return map;
	}

	public static Value<?> of(Map<?, ?> map) {
		MapValue<Value<?>, Value<?>> mapValue = new MapValue<>();
		map.forEach((k, v) -> {
			mapValue.put(Value.of(k), Value.of(v));
		});
		return mapValue;
	}
}
