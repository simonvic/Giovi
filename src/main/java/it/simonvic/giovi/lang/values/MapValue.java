package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;
import org.antlr.v4.misc.OrderedHashMap;

import java.util.*;

public class MapValue<K extends GioviObject, V extends GioviObject> extends Value<OrderedHashMap<K, V>> {

	public MapValue(OrderedHashMap<K, V> value) {
		super(value);
	}

	public MapValue() {
		super(new OrderedHashMap<>());
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return new BoolValue(value.containsKey(right));
	}

	@Override
	public String toString() {
		Iterator<Map.Entry<K,V>> i = value.entrySet().iterator();
		if (! i.hasNext())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;) {
			Map.Entry<K,V> e = i.next();
			K key = e.getKey();
			V value = e.getValue();
			sb.append(key   == this ? "(this Map)" : key);
			sb.append('=');
			sb.append(value == this ? "(this Map)" : value);
			if (! i.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}
}
