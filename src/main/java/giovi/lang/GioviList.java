package giovi.lang;

import static it.simonvic.giovi.util.GioviLists.copyOf;

import java.util.ArrayList;
import java.util.List;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.Range;
import it.simonvic.giovi.exceptions.NonWideneable;

public final class GioviList extends GioviValue<List<GioviValue<?>>> {

	private final GioviType<?> entryType;

	public GioviList(
		GioviType<?> entryType,
		List<GioviValue<?>> value,
		List<GioviValue<?>> defaultValue) {
		super(new ArrayList<>(), new ArrayList<>());
		this.entryType = entryType;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entryType == null) ? 0 : entryType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		GioviList other = (GioviList) obj;
		if (entryType == null) {
			if (other.entryType != null) return false;
		} else if (!entryType.equals(other.entryType)) return false;
		return true;
	}

	/**
	 * Add entry to list. The entry will be widened if necessary
	 * @param entry
	 */
	private void add(GioviValue<?> entry) {
		try {
			value.add(entry.widenTo(entryType));
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	private void addAll(List<? extends GioviValue<?>> entries) {
		entries.forEach(this::add);
	}

	private void remove(GioviValue<?> entry) {
		value.remove(entry);
	}

	private void removeAll(List<? extends GioviValue<?>> entries) {
		value.removeAll(entries);
	}

	private void removeAllWidened(List<? extends GioviValue<?>> entries) {
		try {
			for (var entry : entries) {
				value.remove(entry.widenTo(entryType));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	private void clear() {
		value.clear();
	}

	private void init(List<GioviValue<?>> entries) {
		clear();
		addAll(entries);
	}

	public GioviType<?> getEntryType() {
		return entryType;
	}

	@Override
	public GioviTypeList getGioviType() {
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
		return super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> value = copyOf(defaultValue);
			case GioviList v when v.entryType.equals(this.entryType) -> value = copyOf(v.value);
			case GioviList v when v.entryType.canWidenTo(this.entryType) -> init(copyOf(v.value));
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> addAll(copyOf(defaultValue));
			case GioviList v when v.entryType.canWidenTo(this.entryType) -> addAll(copyOf(v.value));
			case GioviValue<?> v -> {
				try {
					add(v.copy());
				} catch (IllegalArgumentException ex) {
					super.redirectionPlus(content);
				}
			}
			default -> super.redirectionPlus(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> removeAll(defaultValue);
			case GioviList v when v.entryType.equals(this.entryType) -> removeAll(v.value);
			case GioviList v when v.entryType.canWidenTo(this.entryType) -> removeAllWidened(
				v.value);
			case GioviValue<?> v -> {
				try {
					remove(v.widenTo(this.entryType));
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
					if (!this.value.get(i).equal(v.value.get(i)).asGioviBool().unbox()) {
						yield new GioviBool(false);
					}
				}
				yield new GioviBool(true);
			}
			default -> super.equal(right);
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
			default -> super.contains(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> gioviValue -> switch (gioviValue) {
				case GioviInt i -> {
					int index = i.unbox();
					index = index >= 0 ? index : value.size() + index;
					yield value.get(index);
				}
				default -> super.access(right);
			};
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(unbox().size());
				case KEYS -> Range.of(0, unbox().size() - 1, 1);
				case VALUES -> super.access(right);
			};
			case Range<?> range when range instanceof Range(Integer start, Integer end, var step, var strategy) -> {
				var gioviList = new GioviList(this.entryType.copy());
				range.iterate(index -> gioviList.value.add(this.value.get((int) index).copy()));
				yield gioviList;
			}
			default -> super.access(right);
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
		return super.dot(right);
	}

}
