package giovi.lang;

import static it.simonvic.giovi.util.GioviLists.copyOf;

import java.util.ArrayList;
import java.util.List;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.Range;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviList(GioviType<?> entryType, List<GioviValue<?>> value,
	List<GioviValue<?>> defaultValue) implements GioviValue<List<GioviValue<?>>> {

	public GioviList(GioviType<?> entryType, List<GioviValue<?>> value,
		List<GioviValue<?>> defaultValue) {
		this.entryType = entryType;
		this.value = new ArrayList<>();
		this.defaultValue = new ArrayList<>();
		try {
			for (var entry : value) {
				this.value.add(entry.widenTo(this.entryType));
			}
			for (var entry : defaultValue) {
				this.defaultValue.add(entry.widenTo(this.entryType));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public GioviList(
		GioviType<?> entryType,
		List<GioviValue<?>> value) {
		this(entryType, value, new ArrayList<>());
	}

	public GioviList(GioviType<?> entryType) {
		this(entryType, new ArrayList<>(), new ArrayList<>());
	}

	/**
	 * Add entry to list. The entry will be widened if necessary
	 * @param entry
	 */
	public void add(GioviValue<?> entry) {
		try {
			value.add(entry.widenTo(entryType));
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public void addAll(List<? extends GioviValue<?>> entries) {
		entries.forEach(this::add);
	}

	public void remove(GioviValue<?> entry) {
		value.remove(entry);
	}

	public void removeAll(List<? extends GioviValue<?>> entries) {
		value.removeAll(entries);
	}

	public void clear() {
		value.clear();
	}

	public void init(List<GioviValue<?>> entries) {
		clear();
		addAll(entries);
	}

	public GioviType<?> getEntryType() {
		return entryType;
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypeList(entryType);
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		if (type instanceof GioviTypeList t) {
			var widerEntryType = t.getEntryType();
			var widened = new GioviList(widerEntryType);
			for (var entry : this.value) {
				widened.value.add(entry.widenTo(widerEntryType));
			}
			return widened;
		}
		throw new NonWideneable(this, type);
	}

	@Override
	public GioviList copy() {
		var v = new ArrayList<GioviValue<?>>();
		this.value.forEach(value -> v.add(value.copy()));

		var d = new ArrayList<GioviValue<?>>();
		this.defaultValue.forEach(value -> d.add(value.copy()));

		return new GioviList(this.entryType, v, d);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (right instanceof GioviTypeList t) {
			var newGioviList = t.spawn();
			this.value.forEach(v -> newGioviList.add(v.as(t.getEntryType()).asGioviValue()));
			return newGioviList;
		}
		return GioviValue.super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviList(entryType, copyOf(defaultValue), defaultValue);
			case GioviList v when v.entryType.equals(this.entryType) -> new GioviList(
				entryType, copyOf(v.value()), defaultValue);
			case GioviList v when v.entryType.canWidenTo(this.entryType) -> {
				var newGioviValue = new GioviList(entryType, new ArrayList<>(), defaultValue);
				v.value().forEach(entry -> newGioviValue.add(entry.copy()));
				yield newGioviValue;
			}
			default -> GioviValue.super.redirection(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault _ -> addAll(copyOf(defaultValue));
			case GioviList v when v.entryType.canWidenTo(this.entryType) -> addAll(copyOf(v.value));
			case GioviValue<?> v -> {
				try {
					add(v.copy());
				} catch (IllegalArgumentException ex) {
					GioviValue.super.redirectionPlus(right);
				}
			}
			default -> GioviValue.super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault _ -> removeAll(defaultValue);
			case GioviList v when v.entryType.canWidenTo(this.entryType) -> removeAll(v.value);
			case GioviValue<?> v -> {
				try {
					remove(v.widenTo(this.entryType));
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
			case GioviDefault _ -> new GioviBool(value.equals(defaultValue));
			case GioviList v -> {
				// NOTE: we are checking if entries are **equal** instead of **equals**
				if (this.value.size() != v.value.size()) {
					yield new GioviBool(false);
				}
				if (!this.entryType.equals(v.entryType)
					&& !this.entryType.canWidenTo(v.entryType)) {
					yield new GioviBool(false);
				}
				for (int i = 0; i < this.value.size(); i++) {
					if (!this.value.get(i).equal(v.value.get(i)).asGioviBool().value()) {
						yield new GioviBool(false);
					}
				}
				yield new GioviBool(true);
			}
			default -> GioviValue.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviList v -> {
				for (var entry : v.value) {
					if (!contains(entry).asNativeBool()) {
						yield new GioviBool(false);
					}
				}
				yield new GioviBool(true);
			}
			case GioviValue<?> v -> {
				for (var entry : this.value) {
					if (entry.equal(right).asNativeBool()) {
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
			case GioviValue<?> gioviValue -> switch (gioviValue) {
				case GioviInt i -> {
					int index = i.value();
					index = index >= 0 ? index : value.size() + index;
					yield value.get(index);
				}
				default -> GioviValue.super.access(right);
			};
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value().size());
				case KEYS -> Range.of(0, value().size() - 1, 1);
				case VALUES -> GioviValue.super.access(right);
			};
			case Range<?> range when range instanceof Range(Integer start, Integer end, var step, var strategy) -> {
				var gioviList = new GioviList(this.entryType.copy());
				range.iterate(index -> gioviList.value.add(this.value.get((int) index).copy()));
				yield gioviList;
			}
			default -> GioviValue.super.access(right);
		};
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			List<GioviValue<?>> d = new ArrayList<>();
			for (var gioviValue : defaultValue) {
				d.add(gioviValue.copy());
			}
			return new GioviList(entryType, d);
		}
		return GioviValue.super.dot(right);
	}

}
