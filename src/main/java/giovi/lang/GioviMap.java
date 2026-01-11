// TODO: https://www.youtube.com/shorts/3GLBpNVtDtQ
package giovi.lang;

import static it.simonvic.giovi.util.GioviMaps.copyOf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.KeyNotPresent;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeMap.class)
public record GioviMap(
	GioviTypeMap type,
	LinkedHashMap<Var<?>, Var<?>> value
) implements GioviValue<LinkedHashMap<Var<?>, Var<?>>> {

	public GioviMap(GioviTypeMap type) {
		this(type, new LinkedHashMap<>());
	}

	public GioviMap(
		GioviTypeMap type,
		LinkedHashMap<Var<?>, Var<?>> value
	) {
		this.type = type;
		this.value = new LinkedHashMap<>();
		try {
			for (var entry : value.entrySet()) {
				this.value.put(
					entry.getKey().widenTo(this.type.keyType()),
					entry.getValue().widenTo(this.type.elementType()));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	@Override
	public LinkedHashMap<Var<?>, Var<?>> zero() {
		return new LinkedHashMap<>();
	}

	/**
	 * Put key-value pair. They will be widened if necessary.
	 * @param key
	 * @param element
	 * @throws IllegalArgumentException
	 */
	private void put(Var<?> key, Var<?> element) throws IllegalArgumentException {
		// TODO: remove widening here and handle manually
		try {
			value.put(
				key.widenTo(type.keyType()),
				element.widenTo(type.elementType()));
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	private void putAll(Map<Var<?>, Var<?>> map) throws IllegalArgumentException {
		map.forEach(this::put);
	}

	// TODO: return optional or default value (@candidate) (or maybe Void for better performance)
	public Var<?> get(Var<?> key) {
		if (!value.containsKey(key)) {
			// return new Var<>(new GioviVoid());
			// return new Var<>(elementType.spawn());
			throw new KeyNotPresent(this, key.value());
		}
		return value.get(key);
	}

	private void removeAll(Map<Var<?>, Var<?>> entries) {
		entries.forEach(value::remove);
	}

	private void removeAllWidened(Map<Var<?>, Var<?>> entries) {
		try {
			for (var entry : entries.entrySet()) {
				value.remove(
					entry.getKey().widenTo(type.keyType()),
					entry.getValue().widenTo(type.elementType()));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public GioviType<?> getKeyType() {
		return type.keyType();
	}

	public GioviType<?> getElementType() {
		return type.elementType();
	}

	@Override
	public String toString() {
		var i = value.entrySet().iterator();
		if (!i.hasNext())
			return "[{" + type.keyType() + "}:{" + type.elementType() + "}]";

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
		return type;
	}

	@Override
	public GioviMap copy() {
		return new GioviMap(type, copyOf(this.value));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (right instanceof GioviTypeMap t) {
			var map = new GioviMap(type);
			this.value.forEach((k, e) -> map.value.put(
				new Var<>(k.as(t.getKeyType()).asGioviValue()),
				new Var<>(e.as(t.getElementType()).asGioviValue())));
			return map;
		}
		return GioviValue.super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviMap(type, copyOf(zero()));

			case GioviMap(var type, var value) when type.equals(this.type)
				-> new GioviMap(this.type, copyOf(value));

			case GioviMap(var type, var value) when type.isWidenableTo(this.type)
				-> new GioviMap(this.type, copyOf(value));

			default -> GioviValue.super.redirection(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault _ -> putAll(zero());
			case GioviMap(var type, var value) when type.isWidenableTo(this.type) -> putAll(value);
			default -> GioviValue.super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault _ -> removeAll(zero());
			case GioviMap(var type, var value) when type.equals(this.type) -> removeAll(value);
			case GioviMap(var type, var value) when type.isWidenableTo(this.type)
				-> removeAllWidened(value);
			case GioviValue<?> v -> {
				try {
					value.remove(new Var<>(this.type.keyType().widen(v)));
				} catch (NonWideneable e) {
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
			case GioviDefault _ -> new GioviBool(value.equals(zero()));
			case GioviMap v -> {
				// NOTE: we need to check if the key and element is **equal** instead of **equals**
				var thisEntrySet = this.value.entrySet();
				var otherEntrySet = v.value.entrySet();
				if (thisEntrySet.size() != otherEntrySet.size()) {
					yield new GioviBool(false);
				}
				if (!this.type.equals(v.type) && !this.type.isWidenableTo(v.type)) {
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
			case GioviMap(var type, var value) when type.equals(this.type) -> {
				for (var entry : value.entrySet()) {
					var key = entry.getKey();
					if (!this.value.containsKey(key)
						|| !this.value.get(key).equal(entry.getValue()).asNativeBool()) {
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
				case KEYS -> new GioviList(type.keyType(), new ArrayList<>(value().keySet()));
				case VALUES -> new GioviList(type.elementType(), new ArrayList<>(value().values()));
			};
			default -> GioviValue.super.access(right);
		};
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviMap(type);
		}
		return GioviValue.super.dot(right);
	}

}
