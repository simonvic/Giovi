package giovi.lang;

import java.util.LinkedHashMap;
import java.util.Map;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.KeyNotPresent;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.util.GioviMaps;

public record GioviMap(
	GioviType<?> keyType,
	GioviType<?> elementType,
	LinkedHashMap<Var<?>, Var<?>> value)
	implements GioviValue<LinkedHashMap<Var<?>, Var<?>>> {

	public GioviMap(GioviType<?> keyType, GioviType<?> elementType) {
		this(keyType, elementType, new LinkedHashMap<>());
	}

	public GioviMap(
		GioviType<?> keyType,
		GioviType<?> elementType,
		LinkedHashMap<Var<?>, Var<?>> value) {
		this.keyType = keyType;
		this.elementType = elementType;
		this.value = new LinkedHashMap<>();
		try {
			for (var entry : value.entrySet()) {
				this.value.put(
					entry.getKey().widenTo(this.keyType),
					entry.getValue().widenTo(this.elementType));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	@Override
	public LinkedHashMap<Var<?>, Var<?>> defaultValue() {
		return new LinkedHashMap<>();
	}

	/**
	 * Put key-value pair. They will be widened if necessary.
	 * @param key
	 * @param element
	 * @throws IllegalArgumentException
	 */
	public void put(Var<?> key, Var<?> element) throws IllegalArgumentException {
		try {
			value.put(
				key.widenTo(keyType),
				element.widenTo(elementType));
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public void putAll(Map<Var<?>, Var<?>> map) throws IllegalArgumentException {
		map.forEach(this::put);
	}

	// TODO: return optional
	public Var<?> get(Var<?> key) {
		if (!value.containsKey(key)) {
			throw new KeyNotPresent(this, key.value());
		}
		return value.get(key);
	}


	public void remove(Var<?> key, Var<?> element) {
		if (value.containsKey(key)) {
			if (value.get(key).equals(element)) {
				value.remove(key);
			}
		}
	}

	public void remove(Var<?> key) {
		for (Var<?> k : value.keySet()) {
			if (k.equals(key)) {
				value.remove(k);
				return;
			}
		}
	}

	public void removeAll(Map<Var<?>, Var<?>> entries) {
		entries.forEach(this::remove);
	}

	public void removeAllWidened(Map<Var<?>, Var<?>> entries) {
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

	public void clear() {
		value.clear();
	}

	public void init(LinkedHashMap<Var<?>, Var<?>> entries) {
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
			return "[:]";

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
		var v = new LinkedHashMap<Var<?>, Var<?>>();
		this.value.forEach((key, element) -> v.put(key.copy(), element.copy()));
		return new GioviMap(this.keyType, this.elementType, v);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (right instanceof GioviTypeMap t) {
			var map = new GioviMap(t.getKeyType(), t.getElementType());
			this.value.forEach((k, e) -> map.put(
				k.as(t.getKeyType()).asVar(),
				e.as(t.getElementType()).asVar()));
			return map;
		}
		return GioviValue.super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviMap(keyType, elementType,
				GioviMaps.copyOf(defaultValue()));
			case GioviMap v when v.keyType.canWidenTo(this.keyType)
				&& v.elementType.canWidenTo(this.elementType) -> {
				var newGioviMap = new GioviMap(keyType, elementType);
				newGioviMap.putAll(GioviMaps.copyOf(v.value));
				yield newGioviMap;
			}
			default -> GioviValue.super.redirection(content);
		};
	}

	// FIXME: redirection for maps and list

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault _ -> putAll(defaultValue());
			case GioviMap v when v.keyType.canWidenTo(this.keyType)
				&& v.elementType.canWidenTo(this.elementType) -> putAll(v.value);
			default -> GioviValue.super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault _ -> removeAll(defaultValue());
			case GioviMap v when v.keyType.equals(this.keyType)
				&& v.elementType.equals(this.elementType) -> removeAll(v.value);
			case GioviMap v when v.keyType.canWidenTo(this.keyType)
				&& v.elementType.canWidenTo(this.elementType) -> removeAllWidened(v.value);
			case GioviValue<?> v -> {
				try {
					remove(new Var<>(v.widenTo(this.keyType)));
				} catch (IllegalArgumentException | NonWideneable e) {
					return GioviValue.super.redirectionMinus(right);
				}
			}
			default -> GioviValue.super.redirectionMinus(right);
		}
		return this;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.equals(defaultValue()));
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
			default -> GioviValue.super.equal(right);
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
					// TODO: we can finally remove this crap!
					if (key.equal(v).asNativeBool()) {
						yield new GioviBool(true);
					}
				}
				yield new GioviBool(false);
			}
			default -> GioviValue.super.contains(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> gioviValue -> get(new Var<>(gioviValue));
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value().size());
				case KEYS -> new GioviList(keyType, value().keySet().stream().toList());
				case VALUES -> new GioviList(elementType, value().values().stream().toList());
			};
			default -> GioviValue.super.access(right);
		};
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviMap(keyType, elementType);
		}
		return GioviValue.super.dot(right);
	}

}
