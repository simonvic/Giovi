package giovi.lang;

import static it.simonvic.giovi.util.GioviLists.copyOf;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.Range;

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
		if (entry.getGioviType().canWidenTo(entryType)) {
			value.add(entry.as(entryType).asGioviValue());
		} else {
			throw new IllegalArgumentException();
		}
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

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	// FIXME: we should not automatically cast when redirecting (unless we can widen)

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
			case GioviDefault v -> value = copyOf(defaultValue);
			case GioviList v when v.entryType.equals(this.entryType) -> value = copyOf(v.value);
			case GioviList v when v.entryType.canWidenTo(this.entryType) -> init(copyOf(v.value));
			default -> super.redirection(content);
		}
		return this;
	}

	// TODO: remove this crap and use concat/intersaction operator

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> addAll(copyOf(defaultValue));
			case GioviList v when v.entryType.canWidenTo(this.entryType) -> addAll(copyOf(v.value));
			case GioviValue<?> v when v.getGioviType().canWidenTo(this.entryType) -> add(v.copy());
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> removeAll(defaultValue);
			case GioviList v -> removeAll(v.value);
			case GioviValue<?> v -> remove(v);
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
