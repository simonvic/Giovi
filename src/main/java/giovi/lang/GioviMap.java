package giovi.lang;

import java.util.LinkedHashMap;
import java.util.Map;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.KeyNotPresent;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.util.GioviMaps;

public final class GioviMap extends GioviValue<LinkedHashMap<GioviValue<?>, GioviValue<?>>> {

	private final GioviType<?> keyType;
	private final GioviType<?> elementType;

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
		super(new LinkedHashMap<>(), new LinkedHashMap<>());
		this.keyType = keyType;
		this.elementType = elementType;
		value.forEach((k, e) -> this.value.put(
			k.as(keyType).asGioviValue(),
			e.as(elementType).asGioviValue()));
		defaultValue.forEach((k, e) -> this.defaultValue.put(
			k.as(keyType).asGioviValue(),
			e.as(elementType).asGioviValue()));
	}

	/**
	 * Put key-value pair. They will be widened if necessary.
	 * @param key
	 * @param element
	 */
	public void put(GioviValue<?> key, GioviValue<?> element) {
		try {
			value.put(
				key.widenTo(keyType),
				element.widenTo(elementType));
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public void putAll(Map<GioviValue<?>, GioviValue<?>> map) {
		map.forEach(this::put);
	}

	public GioviValue<?> get(GioviValue<?> key) {
		if (!value.containsKey(key)) {
			throw new KeyNotPresent(this, key);
		}
		return value.get(key);
	}


	public void remove(GioviValue<?> key, GioviValue<?> element) {
		key = key.as(keyType).asGioviValue();
		if (value.containsKey(key)) {
			element = element.as(elementType).asGioviValue();
			if (value.get(key).equals(element)) {
				value.remove(key);
			}
		}
	}

	public void remove(GioviValue<?> key) {
		key = key.as(keyType).asGioviValue();
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
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		if (type instanceof GioviTypeMap t) {
			var map = new GioviMap(t.getKeyType(), t.getElementType());
			this.value.forEach(map::put);
			return map;
		}
		throw new NonWideneable(this, type);
	}

	@Override
	public GioviMap copy() {
		var v = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		this.value.forEach((key, element) -> v.put(key.copy(), element.copy()));

		var d = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		this.defaultValue.forEach((key, element) -> d.put(key.copy(), element.copy()));
		return new GioviMap(this.keyType, this.elementType, v, d);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (right instanceof GioviTypeMap t) {
			var map = new GioviMap(t.getKeyType(), t.getElementType());
			this.value.forEach((k, e) -> map.put(
				k.as(t.getKeyType()).asGioviValue(),
				e.as(t.getElementType()).asGioviValue()));
			return map;
		}
		return super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> value = GioviMaps.copyOf(defaultValue);
			case GioviMap v when v.keyType.canWidenTo(this.keyType)
				&& v.elementType.canWidenTo(this.elementType) -> init(GioviMaps.copyOf(v.value));
			default -> super.redirection(content);
		}
		return this;
	}

	// TODO: remove this crap and use union/intersaction operator

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> putAll(defaultValue);
			case GioviMap v when v.keyType.canWidenTo(this.keyType)
				&& v.elementType.canWidenTo(this.elementType) -> putAll(v.value);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> removeAll(defaultValue);
			case GioviMap v when v.keyType.canWidenTo(this.keyType)
				&& v.elementType.canWidenTo(this.elementType) -> removeAll(v.value);
			case GioviValue<?> v -> {
				try {
					remove(v.widenTo(this.keyType));
				} catch (IllegalArgumentException | NonWideneable e) {
					return super.redirectionMinus(right);
				}
			}
			default -> super.redirectionMinus(right);
		}
		return this;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviMap v -> {
				// NOTE: we need to check if the key and element is **equal** instead of **equals**
				var thisEntrySet = this.value.entrySet();
				var otherEntrySet = v.value.entrySet();
				if (thisEntrySet.size() != otherEntrySet.size()) {
					yield new GioviBool(false);
				}
				if (!this.keyType.equals(v.keyType)
					&& !this.keyType.canWidenTo(v.keyType)) {
					yield new GioviBool(false);
				}
				if (!this.elementType.equals(v.elementType)
					&& !this.elementType.canWidenTo(v.elementType)) {
					yield new GioviBool(false);
				}
				var thisIterator = thisEntrySet.iterator();
				var otherIterator = otherEntrySet.iterator();
				while (thisIterator.hasNext() && otherIterator.hasNext()) {
					var thisEntry = thisIterator.next();
					var otherEntry = otherIterator.next();
					var thisKey = thisEntry.getKey();
					var otherKey = otherEntry.getKey();
					if (!thisKey.equal(otherKey).asNativeBool()) {
						yield new GioviBool(false);
					}
					var thisValue = thisEntry.getValue();
					var otherValue = otherEntry.getValue();
					if (!thisValue.equal(otherValue).asNativeBool()) {
						yield new GioviBool(false);
					}
				}
				yield new GioviBool(true);
			}
			default -> super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		for (var key : this.value.keySet()) {
			if (key.equal(right).asNativeBool()) {
				return new GioviBool(true);
			}
		}
		return new GioviBool(false);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> gioviValue -> get(gioviValue);
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(unbox().size());
				case KEYS -> new GioviList(keyType, unbox().keySet().stream().toList());
				case VALUES -> new GioviList(elementType, unbox().values().stream().toList());
			};
			default -> throw new GioviException("Invalid access key: " + right);
		};
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			var d = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
			this.defaultValue.forEach((key, element) -> d.put(key.copy(), element.copy()));
			return new GioviMap(keyType, elementType, d);
		}
		return super.dot(right);
	}

}
