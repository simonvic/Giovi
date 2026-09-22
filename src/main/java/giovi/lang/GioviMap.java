// TODO: https://www.youtube.com/shorts/3GLBpNVtDtQ
package giovi.lang;

import static it.simonvic.giovi.util.GioviMaps.copyOf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import giovi.GioviParserVisitResult;
import giovi.MemberKey;
import it.simonvic.giovi.Entry;
import it.simonvic.giovi.exceptions.KeyNotPresent;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeMap.class)
public record GioviMap(
	GioviTypeMap type,
	LinkedHashMap<Entry<?>, Entry<?>> value
) implements GioviComposite<LinkedHashMap<Entry<?>, Entry<?>>> {

	public GioviMap(GioviTypeMap type) {
		this(type, new LinkedHashMap<>());
	}

	public GioviMap(
		GioviTypeMap type,
		LinkedHashMap<Entry<?>, Entry<?>> value
	) {
		this.type = type;
		this.value = new LinkedHashMap<>();
		try {
			for (var entry : value.entrySet()) {
				this.value.put(
					new Entry<>(entry.getKey().unwrap().widenTo(this.type.keyType())),
					new Entry<>(entry.getValue().unwrap().widenTo(this.type.elementType()))
				);
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
	private void put(Entry<?> key, Entry<?> element) throws IllegalArgumentException {
		// TODO: remove widening here and handle manually
		try {
			value.put(
				new Entry<>(key.unwrap().widenTo(type.keyType())),
				new Entry<>(element.unwrap().widenTo(type.elementType()))
			);
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	private void putAll(Map<Entry<?>, Entry<?>> map) throws IllegalArgumentException {
		map.forEach(this::put);
	}

	// TODO: return optional or default value (@candidate) (or maybe Void for better performance)
	public Entry<?> get(Entry<?> key) {
		if (!value.containsKey(key)) {
			// return new VarEntry<>(new GioviVoid());
			// return new VarEntry<>(elementType.spawn());
			throw new KeyNotPresent(this, key.unwrap());
		}
		return value.get(key);
	}

	private void removeAll(Map<Entry<?>, Entry<?>> entries) {
		entries.forEach(value::remove);
	}

	private void removeAllWidened(Map<Entry<?>, Entry<?>> entries) {
		try {
			for (var entry : entries.entrySet()) {
				value.remove(
					new Entry<>(entry.getKey().unwrap().widenTo(type.keyType())),
					new Entry<>(entry.getValue().unwrap().widenTo(type.elementType()))
				);
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
			this.value.forEach(
				(k, e) -> map.value.put(
					new Entry<>(k.as(t.getKeyType()).asGioviValue()),
					new Entry<>(e.as(t.getElementType()).asGioviValue())
				)
			);
			return map;
		}
		return GioviComposite.super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviMap(var type, var value) when type.equals(this.type)
				-> new GioviMap(this.type, copyOf(value));

			case GioviMap(var type, var value) when type.isWidenableTo(this.type)
				-> new GioviMap(this.type, copyOf(value));

			default -> GioviComposite.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviMap v when v.type.isWidenableTo(this.type) -> {
				var merged = this.copy();
				merged.putAll(v.value);
				yield merged;
			}
			// case GioviMap v when widest(this.type, v.type) instanceof GioviTypeMap widestType -> {
			// 	var merged = new GioviMap(widestType, copyOf(this.value));
			// 	merged.putAll(v.value);
			// 	yield merged;
			// }
			default -> GioviComposite.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviMap v when v.type.isWidenableTo(this.type) -> {
				var intersection = this.copy();
				intersection.removeAllWidened(v.value);
				yield intersection;
			}
			// case GioviMap v when widest(this.type, v.type) instanceof GioviTypeMap widestType -> {
			// 	var intersection = new GioviMap(widestType, copyOf(this.value));
			// 	if (widestType == this.type) {
			// 		intersection.removeAllWidened(v.value);
			// 	} else {
			// 		intersection.removeAll(v.value);
			// 	}
			// 	yield intersection;
			// }
			case GioviValue<?> v -> {
				var intersection = this.copy();
				try {
					intersection.value.remove(new Entry<>(this.type.keyType().widen(v)));
				} catch (NonWideneable e) {
					yield GioviComposite.super.minus(right);
				}
				yield intersection;
			}
			default -> GioviComposite.super.minus(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviMap v -> {
				// NOTE: we need to check if the key and element is **equal** instead of **equals**
				var thisEntrySet = this.value.entrySet();
				var otherEntrySet = v.value.entrySet();
				if (thisEntrySet.size() != otherEntrySet.size()) {
					yield GioviBool.FALSE;
				}
				if (!this.type.equals(v.type) && !this.type.isWidenableTo(v.type)) {
					yield GioviBool.FALSE;
				}
				var thisIterator = thisEntrySet.iterator();
				var otherIterator = otherEntrySet.iterator();
				while (thisIterator.hasNext() && otherIterator.hasNext()) {
					var thisEntry = thisIterator.next();
					var otherEntry = otherIterator.next();
					var thisKey = thisEntry.getKey();
					var otherKey = otherEntry.getKey();
					if (!thisKey.equal(otherKey).asNativeBool()) {
						yield GioviBool.FALSE;
					}
					var thisValue = thisEntry.getValue();
					var otherValue = otherEntry.getValue();
					if (!thisValue.equal(otherValue).asNativeBool()) {
						yield GioviBool.FALSE;
					}
				}
				yield GioviBool.TRUE;
			}
			default -> GioviComposite.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviMap(var type, var value) when type.equals(this.type) -> {
				for (var entry : value.entrySet()) {
					var key = entry.getKey();
					if (
						!this.value.containsKey(key)
							|| !this.value.get(key).equal(entry.getValue()).asNativeBool()
					) {
						yield GioviBool.FALSE;
					}
				}
				yield GioviBool.TRUE;
			}
			case GioviValue<?> v -> {
				for (var key : value.keySet()) {
					if (key.equal(v).asNativeBool()) {
						yield GioviBool.TRUE;
					}
				}
				yield GioviBool.FALSE;
			}
			default -> GioviComposite.super.contains(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> gioviValue -> get(new Entry<>(gioviValue));
			case MemberKey(var identifier) when identifier.startsWith("_") -> switch (identifier) {
				case "_count" -> new GioviInt(value().size());
				case "_keys" -> new GioviList(
					new GioviTypeList(type.keyType()),
					new ArrayList<>(value().keySet())
				);
				case "_values" -> new GioviList(
					new GioviTypeList(type.elementType()),
					new ArrayList<>(value().values())
				);
				default -> GioviComposite.super.access(right);
			};
			default -> GioviComposite.super.access(right);
		};
	}

}
