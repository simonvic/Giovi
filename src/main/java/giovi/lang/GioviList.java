package giovi.lang;

import java.util.ArrayList;
import java.util.List;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import it.simonvic.giovi.lang.Range;

public final class GioviList extends GioviValue<List<GioviValue<?>>> {

	private final GioviType<?> entryType;

	public GioviList(
		GioviType<?> entryType,
		List<GioviValue<?>> value,
		List<GioviValue<?>> defaultValue) {
		super(new ArrayList<>(), new ArrayList<>());
		this.entryType = entryType;
		value.stream()
			.map(entry -> entry.as(this.entryType).asGioviValue())
			.forEach(this.value::add);
		defaultValue.stream()
			.map(entry -> entry.as(this.entryType).asGioviValue())
			.forEach(this.defaultValue::add);
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

	public void add(GioviValue<?> entry) {
		value.add(entry.as(entryType).asGioviValue());
	}

	public void addAll(List<? extends GioviValue<?>> entries) {
		entries.forEach(this::add);
	}

	public void remove(GioviValue<?> entry) {
		value.remove(entry);
	}

	public void removeAll(List<? extends GioviValue<?>> seq) {
		value.removeAll(seq);
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
	public GioviList copy() {
		var v = new ArrayList<GioviValue<?>>();
		this.value.forEach(value -> v.add(value.copy()));

		var d = new ArrayList<GioviValue<?>>();
		this.defaultValue.forEach(value -> v.add(value.copy()));

		return new GioviList(this.entryType, v, d);
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		switch (gioviValue) {
			case GioviList v -> value = v.unbox();
			default -> super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue) {
			case GioviList v -> setDefaultValue(v.unbox());
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public void boxDefault() {
		this.init(defaultValue);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (right instanceof GioviTypeList t) {
			var newGioviList = t.spawn();
			this.value.forEach(newGioviList::add);
			return newGioviList;
		}
		return super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			case GioviList v -> this.init(v.unbox());
			default -> super.redirection(content);
		}
		return this;
	}

	// TODO: remove this crap and use concat/intersaction operator

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> addAll(defaultValue);
			case GioviList v -> addAll(v.unbox());
			case GioviValue<?> v -> add(v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> removeAll(defaultValue);
			case GioviList v -> removeAll(v.unbox());
			case GioviValue<?> v -> remove(v);
			default -> super.redirectionPlus(right);
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
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		for (var entry : this.value) {
			if (entry.equal(right).asNativeBool()) {
				return new GioviBool(true);
			}
		}
		return new GioviBool(false);
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
			case Range<?> range -> {
				if (range.getStart() instanceof Integer start
					&& range.getEnd() instanceof Integer end) {
					var gioviList = new GioviList(this.entryType.copy());
					var list = gioviList.value;
					range.iterate(index -> list.add(this.value.get((int) index).copy()));
					yield gioviList;
				}
				yield super.access(right);
			}
			default -> super.access(right);
		};
	}

}
