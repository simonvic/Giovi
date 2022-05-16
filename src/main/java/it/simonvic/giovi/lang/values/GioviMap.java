package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.GioviObject;

import java.util.*;

public non-sealed class GioviMap<K extends GioviValue<?>, V extends GioviValue<?>>
	extends GioviValue<LinkedHashMap<K, V>>
	implements GioviCollection {

	public GioviMap(LinkedHashMap<K, V> value, LinkedHashMap<K, V> defaultValue) {
		super(value, defaultValue);
	}

	public GioviMap() {
		super(new LinkedHashMap<>(), new LinkedHashMap<>());
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		switch (gioviValue) {
			case GioviMap<?, ?> v -> this.box((LinkedHashMap<K, V>) v.unbox());
			default -> super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue.unbox()) {
			case GioviMap<?, ?> v -> setDefaultValue((LinkedHashMap<K, V>) new LinkedHashMap<>(v.unbox()));
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return new GioviBool(value.containsKey(right));
	}

	@Override
	public GioviObject redirection(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value = new LinkedHashMap<>(defaultValue);
			case GioviMap<?, ?> v -> value = (LinkedHashMap<K, V>) v.value;
			default -> super.redirection(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionPlus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> defaultValue.forEach(this::put);
			case GioviMap<?, ?> v -> v.unbox().forEach((key, value) -> this.put((K) key, (V) value));
//			case GioviValue<?> v -> this.put((K) v, defaultValue);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMinus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> defaultValue.forEach(this::remove);
			case GioviMap<?, ?> v -> v.unbox().forEach((key, element) -> this.remove((K) key, (V) element));
			case GioviList<?> v -> v.unbox().forEach(keyToRemove -> this.remove((K) keyToRemove)); //@todo keep this???
			case GioviValue<?> v -> remove((K) v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviMap<?, ?> v -> new GioviBool(this.equals(v));
			default -> super.equal(right);
		};
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		final GioviMap<K, V> other = (GioviMap<K, V>) obj;

		if (value.size() != other.value.size()) return false;

		for (Map.Entry<K, V> entry : value.entrySet()) {
			if (!entry.getValue().equals(other.get(entry.getKey()))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		Iterator<Map.Entry<K, V>> i = value.entrySet().iterator();
		if (!i.hasNext())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (; ; ) {
			Map.Entry<K, V> e = i.next();
			K key = e.getKey();
			V value = e.getValue();
			sb.append(key == this ? "(this Map)" : key);
			sb.append(':');
			sb.append(value == this ? "(this Map)" : value);
			if (!i.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}


	public void put(K key, V element) {
		for (K k : value.keySet()) {
			if (k.equals(key)) {
				value.get(k).box(element);
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

	private void remove(K key, V element) {
		for (K k : value.keySet()) {
			if (k.equals(key) && element.equals(value.get(k))) {
				value.remove(k);
				return;
			}
		}
	}

	private void remove(K key) {
		for (K k : value.keySet()) {
			if (k.equals(key)) {
				value.remove(k);
				return;
			}
		}
	}

	@Override
	public GioviValue<?> access(GioviObject accessor) {
		return switch (accessor) {
			case GioviValue<?> v -> get((K) v);
			case Accessor v -> switch (v.get()) {
				case "%" -> new GioviInt(unbox().size());
				case "@" -> {
					GioviList<K> indeces = new GioviList<>();
					indeces.addAll(unbox().keySet().stream().toList());
					yield indeces;
				}
				case "*" -> {
					GioviList<V> values = new GioviList<>();
					values.addAll(unbox().values().stream().toList());
					yield values;
				}
				default -> throw new GioviException("Invalid accessor: " + accessor);
			};
			default -> throw new GioviException("Invalid accessor:" + accessor);
		};
	}
}
