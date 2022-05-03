package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public non-sealed class MapValue<K extends Value<?>, V extends Value<?>> extends Value<LinkedHashMap<K, V>> {

	public MapValue() {
		super(new LinkedHashMap<>());
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return new BoolValue(value.containsKey(right));
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case MapValue<?,?> v -> new BoolValue(this.equals(v));
			default -> super.equal(right);
		};
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		final MapValue<K,V> other = (MapValue<K,V>) obj;

		if (value.size() != other.value.size()) return false;

		for(Map.Entry<K,V> entry : value.entrySet()) {
			if (!entry.getValue().equals(other.get(entry.getKey()))) {
				return false;
			}
		}
		return true;
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
			sb.append(':');
			sb.append(value == this ? "(this Map)" : value);
			if (! i.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}


	public void put(K key, V element) {
		for (K k : value.keySet()) {
			if (k.equals(key)) {
				value.put(k, element);
				return;
			}
		}
		value.put(key, element);
	}

	public V get(K key) {
		for (K k : value.keySet()) {
			if (k.equals(key)) {
				return value.get(k);
			}
		}
		return null;
	}
}
