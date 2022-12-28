package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.type.GioviType;

import java.util.LinkedHashMap;
import java.util.Map;

public non-sealed class GioviMap extends GioviCollection<LinkedHashMap<GioviValue<?>, GioviValue<?>>> {

	private final GioviType keyType;
	private final GioviType elementType;

	public GioviMap(LinkedHashMap<GioviValue<?>, GioviValue<?>> value) {
		this(null, null, value);
	}

	public GioviMap(GioviType keyType, GioviType elementType, LinkedHashMap<GioviValue<?>, GioviValue<?>> value, LinkedHashMap<GioviValue<?>, GioviValue<?>> defaultValue) {
		super(value, defaultValue);
		this.keyType = keyType != null ? keyType : GioviType.inferGioviType(value.keySet());
		this.elementType = elementType != null ? elementType : GioviType.inferGioviType(value.values());
		checkCasts(this.value, this.keyType, this.elementType);
		checkCasts(this.defaultValue, this.keyType, this.elementType);
	}

	public GioviMap(GioviType keyType, GioviType elementType, LinkedHashMap<GioviValue<?>, GioviValue<?>> value) {
		this(keyType, elementType, value, new LinkedHashMap<>());
	}

	public GioviMap(GioviType keyType, GioviType elementType) {
		this(keyType, elementType, new LinkedHashMap<>(), new LinkedHashMap<>());
	}

	public GioviMap(Class<? extends GioviValue<?>> keyType, Class<? extends GioviValue<?>> elementType) {
		this(new GioviType(keyType), new GioviType(elementType));
	}

	private static void checkCasts(LinkedHashMap<GioviValue<?>, GioviValue<?>> entries, GioviType keyType, GioviType elementType) {
		entries.forEach((key, element) -> {
			key.castTo(keyType);
			element.castTo(elementType);
		});
	}

	public GioviType getKeyType() {
		return keyType;
	}

	public GioviType getElementType() {
		return elementType;
	}

	public void put(GioviValue<?> key, GioviValue<?> element) {
		key = key.castTo(keyType);
		element = element.castTo(elementType);
		for (GioviValue<?> k : value.keySet()) {
			if (k.equals(key)) {
				value.get(k).box(element);
				return;
			}
		}
		value.put(key, element);
	}

	public void putAll(Map<GioviValue<?>, GioviValue<?>> defaultValue) {
		defaultValue.forEach(this::put);
	}

	public GioviValue<?> get(GioviValue<?> key) {
		for (GioviValue<?> k : value.keySet()) {
			if (k.equals(key)) {
				return value.get(k);
			}
		}
		return null;
	}


	public void remove(GioviValue<?> key, GioviValue<?> element) {
		key = key.castTo(keyType);
		element = element.castTo(elementType);
		for (GioviValue<?> k : value.keySet()) {
			if (k.equals(key) && element.equals(value.get(k))) {
				value.remove(k);
				return;
			}
		}
	}

	public void remove(GioviValue<?> key) {
		key = key.castTo(keyType);
		for (GioviValue<?> k : value.keySet()) {
			if (k.equals(key)) {
				value.remove(k);
				return;
			}
		}
	}

	public void removeAll(Map<GioviValue<?>, GioviValue<?>> defaultValue) {
		defaultValue.forEach(this::remove);
	}

	public void clear() {
		value.clear();
	}

	public void init(LinkedHashMap<GioviValue<?>, GioviValue<?>> entries) {
		clear();
		putAll(entries);
	}

	@Override
	public GioviValue<?> castTo(GioviType type) {
		Class<? extends GioviValue<?>> typeClass = type.unbox();
		if (typeClass == GioviMap.class) return new GioviMap(value);
		return super.castTo(type);
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		switch (gioviValue) {
			case GioviMap v -> value = v.unbox();
			default -> super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue) {
			case GioviMap v -> setDefaultValue(v.unbox());
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public void boxDefault() {
		this.init(defaultValue);
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return new GioviBool(value.containsKey(right.asGioviValue()));
	}

	@Override
	public GioviObject redirection(GioviObject right) {
		switch (right) {
			case GioviDefault v -> boxDefault();
			case GioviMap v -> init(v.unbox());
			default -> super.redirection(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionPlus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> putAll(defaultValue);
			case GioviMap v -> putAll(v.unbox());
//			case GioviValue<?> v -> this.put((K) v, defaultValue);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMinus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> removeAll(defaultValue);
			case GioviMap v -> removeAll(v.unbox());
			case GioviValue<?> v -> remove(v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviMap v -> new GioviBool(this.equals(v));
			default -> super.equal(right);
		};
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		final GioviMap other = (GioviMap) obj;

		if (value.size() != other.value.size()) return false;

		for (Map.Entry<GioviValue<?>, GioviValue<?>> entry : value.entrySet()) {
			if (!entry.getValue().equals(other.get(entry.getKey()))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		var i = value.entrySet().iterator();
		if (!i.hasNext())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (; ; ) {
			var e = i.next();
			var key = e.getKey();
			var value = e.getValue();
			sb.append(key);
			sb.append(':');
			sb.append(value);
			if (!i.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}


	@Override
	public GioviValue<?> access(GioviValue<?> accessor) {
		return get(accessor);
	}

	@Override
	public GioviValue<?> access(Accessor accessor) {
		return switch (accessor.get()) {
			case "%" -> new GioviInt(unbox().size());
			case "@" -> new GioviList(unbox().keySet().stream().toList());
			case "*" -> new GioviList(unbox().values().stream().toList());
			default -> throw new GioviException("Invalid accessor: " + accessor);
		};
	}
}
