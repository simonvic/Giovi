package it.simonvic.giovi.util;

import it.simonvic.giovi.lang.values.ListValue;
import it.simonvic.giovi.lang.values.MapValue;
import it.simonvic.giovi.lang.values.Value;

import java.util.List;
import java.util.Map;

public class GioviCollections {

	public static Value<?> of(List<?> values) {
		ListValue<Value<?>> list = new ListValue<>();
		values.forEach(value -> list.add(Value.of(value)));
		return list;
	}

	public static <T> ListValue<Value<?>> of(T... values) {
		ListValue<Value<?>> list = new ListValue<>();
		for (T value : values) {
			list.add(Value.of(value));
		}
		return list;
	}

	public static Value<?> of(Map<?, ?> map) {
		MapValue<Value<?>, Value<?>> mapValue = new MapValue<>();
		map.forEach((k, v) -> {
			mapValue.put(Value.of(k), Value.of(v));
		});
		return mapValue;
	}
}
