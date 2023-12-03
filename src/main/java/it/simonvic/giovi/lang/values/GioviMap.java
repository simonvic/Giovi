package it.simonvic.giovi.lang.values;

import java.util.LinkedHashMap;
import java.util.Map;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.KeyNotPresent;
import it.simonvic.giovi.lang.AccessKey;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.util.GioviTypes;

public final class GioviMap extends GioviValue<LinkedHashMap<GioviValue<?>, GioviValue<?>>> {

	private final GioviType<?> keyType;
	private final GioviType<?> elementType;

	public GioviMap(LinkedHashMap<GioviValue<?>, GioviValue<?>> value) {
		this(
			GioviTypes.inferGioviType(value.keySet()),
			GioviTypes.inferGioviType(value.values()),
			value);
	}

	public GioviMap(
		GioviType<?> keyType,
		GioviType<?> elementType,
		LinkedHashMap<GioviValue<?>, GioviValue<?>> value) {
		this(keyType, elementType, value, new LinkedHashMap<>());
	}

	public GioviMap(GioviType<?> keyType, GioviType<?> elementType) {
		this(keyType, elementType, new LinkedHashMap<>(), new LinkedHashMap<>());
	}

	public GioviMap(
		GioviType<?> keyType,
		GioviType<?> elementType,
		LinkedHashMap<GioviValue<?>, GioviValue<?>> value,
		LinkedHashMap<GioviValue<?>, GioviValue<?>> defaultValue) {

		super(value, defaultValue);
		this.keyType = keyType;
		this.elementType = elementType;
		this.value.forEach((k, e) -> {
			k.castTo(this.keyType);
			e.castTo(this.elementType);
		});
		this.defaultValue.forEach((k, e) -> {
			k.castTo(this.keyType);
			e.castTo(this.elementType);
		});
	}

	// TODO: move map logic to native implementation

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
		throw new KeyNotPresent(this, key);
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

	public GioviType<?> getKeyType() {
		return keyType;
	}

	public GioviType<?> getElementType() {
		return elementType;
	}

	@Override
	public String toString() {
		var i = value.entrySet().iterator();
		if (!i.hasNext())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;) {
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
		result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		GioviMap other = (GioviMap) obj;
		if (keyType == null) {
			if (other.keyType != null) return false;
		} else if (!keyType.equals(other.keyType)) return false;
		if (elementType == null) {
			if (other.elementType != null) return false;
		} else if (!elementType.equals(other.elementType)) return false;
		return true;
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypeMap(keyType, elementType);
	}

	@Override
	public GioviMap copy() {
		var v = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		this.value.forEach((key, element) -> v.put(key.copy(), element.copy()));

		var d = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		this.defaultValue.forEach((key, element) -> v.put(key.copy(), element.copy()));
		return new GioviMap(this.keyType, this.elementType, v, d);
	}

	@Override
	public GioviValue<?> castTo(GioviType<?> type) {
		if (type instanceof GioviTypeMap t) {
			var map = new GioviMap(t.getKeyType(), t.getElementType());
			this.value.forEach(map::put);
			return map;
		}
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

	//                                                                                OPERATORATION
	//                                                                                  redirection
	@Override
	public GioviObject redirection(GioviObject content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			case GioviMap v -> init(v.unbox());
			default -> super.redirection(content);
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

	//                                                                                   arithmetic
	//                                                                                      boolean

	//                                                                                   relational
	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviMap v -> new GioviBool(this.equals(v));
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return new GioviBool(value.containsKey(right.asGioviValue()));
	}

	//                                                                                       access

	@Override
	public GioviObject access(GioviObject right) {
		return switch (right) {
			case GioviValue<?> gioviValue -> get(gioviValue);
			case AccessKey accessKey -> switch (accessKey.get()) {
				case "%" -> new GioviInt(unbox().size());
				case "@" -> new GioviList(unbox().keySet().stream().toList());
				case "*" -> new GioviList(unbox().values().stream().toList());
				default -> throw new GioviException("Invalid access key: " + accessKey);
			};
			default -> throw new GioviException("Invalid access key: " + right);
		};
	}
}
