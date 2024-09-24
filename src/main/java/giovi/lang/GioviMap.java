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
		try {
			for (var entry : value.entrySet()) {
				this.value.put(
					entry.getKey().widenTo(this.keyType),
					entry.getValue().widenTo(this.elementType));
			}
			for (var entry : defaultValue.entrySet()) {
				this.defaultValue.put(
					entry.getKey().widenTo(this.keyType),
					entry.getValue().widenTo(this.elementType));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * Put key-value pair. They will be widened if necessary.
	 * @param key
	 * @param element
	 * @throws IllegalArgumentException
	 */
	private void put(GioviValue<?> key, GioviValue<?> element) throws IllegalArgumentException {
		try {
			value.put(
				key.widenTo(keyType),
				element.widenTo(elementType));
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	private void putAll(Map<GioviValue<?>, GioviValue<?>> map) throws IllegalArgumentException {
		map.forEach(this::put);
	}

	private GioviValue<?> get(GioviValue<?> key) {
		if (!value.containsKey(key)) {
			throw new KeyNotPresent(this, key);
		}
		return value.get(key);
	}


	private void remove(GioviValue<?> key, GioviValue<?> element) {
		if (value.containsKey(key)) {
			if (value.get(key).equals(element)) {
				value.remove(key);
			}
		}
	}

	private void remove(GioviValue<?> key) {
		for (GioviValue<?> k : value.keySet()) {
			if (k.equals(key)) {
				value.remove(k);
				return;
			}
		}
	}

	private void removeAll(Map<GioviValue<?>, GioviValue<?>> entries) {
		entries.forEach(this::remove);
	}

	public void removeAllWidened(Map<GioviValue<?>, GioviValue<?>> entries) {
		try {
			for (var entry : entries.entrySet()) {
				value.remove(
					entry.getKey().widenTo(keyType),
					entry.getValue().widenTo(elementType));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	private void clear() {
		value.clear();
	}

	private void init(LinkedHashMap<GioviValue<?>, GioviValue<?>> entries) {
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
	public GioviTypeMap getGioviType() {
		return new GioviTypeMap(keyType, elementType);
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		if (type instanceof GioviTypeMap t) {
			var widerKeyType = t.getKeyType();
			var widerElementType = t.getElementType();
			var widened = new GioviMap(widerKeyType, widerElementType);
			for (var entry : this.value.entrySet()) {
				widened.value.put(
					entry.getKey().widenTo(widerKeyType),
					entry.getValue().widenTo(widerElementType));
			}
			return widened;
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
			case GioviDefault _ -> value = GioviMaps.copyOf(defaultValue);
			case GioviMap v when v.keyType.canWidenTo(this.keyType)
				&& v.elementType.canWidenTo(this.elementType) -> init(GioviMaps.copyOf(v.value));
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> putAll(defaultValue);
			case GioviMap v when v.keyType.canWidenTo(this.keyType)
				&& v.elementType.canWidenTo(this.elementType) -> putAll(v.value);
			default -> super.redirectionPlus(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> removeAll(defaultValue);
			case GioviMap v when v.keyType.equals(this.keyType)
				&& v.elementType.equals(this.elementType) -> removeAll(v.value);
			case GioviMap v when v.keyType.canWidenTo(this.keyType)
				&& v.elementType.canWidenTo(this.elementType) -> removeAllWidened(v.value);
			case GioviValue<?> v -> {
				try {
					remove(v.widenTo(this.keyType));
				} catch (IllegalArgumentException | NonWideneable e) {
					return super.redirectionMinus(content);
				}
			}
			default -> super.redirectionMinus(content);
		}
		return this;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.equals(defaultValue));
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
		return switch (right) {
			case GioviMap m when m.keyType.equals(keyType) && m.elementType.equals(elementType) -> {
				for (var entry : m.value.entrySet()) {
					var key = entry.getKey();
					if (!value.containsKey(key)
						|| !value.get(key).equal(entry.getValue()).asNativeBool()) {
						yield new GioviBool(false);
					}
				}
				yield new GioviBool(true);
			}
			case GioviValue<?> v -> {
				for (var key : value.keySet()) {
					if (key.equal(v).asNativeBool()) {
						yield new GioviBool(true);
					}
				}
				yield new GioviBool(false);
			}
			default -> super.contains(right);
		};
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
