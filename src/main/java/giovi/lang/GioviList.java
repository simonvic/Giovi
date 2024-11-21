package giovi.lang;

import static it.simonvic.giovi.util.GioviLists.copyOf;

import java.util.ArrayList;
import java.util.List;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.Range;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Func.GenericParam;
import it.simonvic.giovi.lang.annotation.Native.Func.Param;
import it.simonvic.giovi.lang.annotation.Native.Type;

public record GioviList(GioviType<?> entryType, List<Var<?>> value)
	implements GioviValue<List<Var<?>>> {

	public GioviList(GioviType<?> entryType, List<Var<?>> value) {
		this.entryType = entryType;
		this.value = new ArrayList<>();
		try {
			for (var entry : value) {
				this.value.add(entry.widenTo(this.entryType));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public GioviList(GioviType<?> entryType) {
		this(entryType, new ArrayList<>());
	}

	@Override
	public List<Var<?>> defaultValue() {
		return new ArrayList<>();
	}

	@Override
	public String toString() {
		var i = value.iterator();
		if (!i.hasNext())
			return "[{" + entryType + "}]";
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		// sb.append(entryType);
		// sb.append(": ");
		for (;;) {
			sb.append(i.next());
			if (!i.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}

	// TODO: check if Vars or GioviValues should be exposed

	/**
	 * Add entry to list. The entry will be widened if necessary
	 * @param entry
	 */
	private void add(Var<?> entry) {
		try {
			value.add(entry.widenTo(entryType));
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	private void addAll(List<? extends Var<?>> entries) {
		entries.forEach(this::add);
	}

	private void remove(Var<?> entry) {
		value.remove(entry);
	}

	private void removeAll(List<? extends Var<?>> entries) {
		value.removeAll(entries);
	}

	private void removeAllWidened(List<? extends Var<?>> entries) {
		try {
			for (var entry : entries) {
				value.remove(entry.widenTo(entryType));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
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
		var v = new ArrayList<Var<?>>();
		this.value.forEach(value -> v.add(value.copy()));
		return new GioviList(this.entryType, v);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (right instanceof GioviTypeList t) {
			var newGioviList = t.spawn();
			this.value.forEach(v -> newGioviList.add(v.as(t.getEntryType()).asVar()));
			return newGioviList;
		}
		return GioviValue.super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviList(entryType, copyOf(defaultValue()));
			case GioviList v when v.entryType.equals(this.entryType) -> new GioviList(
				entryType, copyOf(v.value()));
			case GioviList v when v.entryType.canWidenTo(this.entryType) -> {
				var newGioviValue = new GioviList(entryType);
				v.value().forEach(entry -> newGioviValue.add(entry.copy()));
				yield newGioviValue;
			}
			default -> GioviValue.super.redirection(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault _ -> addAll(copyOf(defaultValue()));
			case GioviList v when v.entryType.canWidenTo(this.entryType) -> addAll(copyOf(v.value));
			case GioviValue<?> v -> {
				try {
					add(new Var<>(v.copy()));
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
			case GioviDefault _ -> removeAll(defaultValue());
			case GioviList v when v.entryType.equals(entryType) -> removeAll(v.value);
			case GioviList v when v.entryType.canWidenTo(entryType) -> removeAllWidened(v.value);
			case GioviValue<?> v -> {
				try {
					remove(new Var<>(v.widenTo(this.entryType)));
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
					if (!contains(entry.value()).asNativeBool()) {
						yield new GioviBool(false);
					}
				}
				yield new GioviBool(true);
			}
			case GioviValue<?> v -> {
				for (var entry : this.value) {
					if (entry.value().equal(right).asNativeBool()) {
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
		if (right instanceof Var<?> v) {
			right = v.value();
		}
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
			case Range<?> range when range instanceof Range(Integer start, Integer end, _, _) -> {
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
			return new GioviList(entryType);
		}
		return GioviValue.super.dot(right);
	}

	@Func
	public void clear() {
		value.clear();
	}

	@Func
	public @Type(GioviTypeInt.class) GioviInt size() {
		return new GioviInt(value.size());
	}

	@Func
	public @Type(GioviTypeList.class) GioviList reversed() {
		return new GioviList(entryType, value.reversed());
	}

	@Func
	public void insert(@GenericParam(name = "value") GioviValue<?> value)
		throws NonWideneable {
		this.value.add(0, new Var<>(value.copy().widenTo(entryType)));
	}

	@Func
	public void print(@GenericParam(name = "number") GioviParserVisitResult number) {
		System.out.println(number);
	}

	@Func
	public void insertAt(
		@Param(name = "index", type = @Type(GioviTypeInt.class)) GioviInt index,
		@GenericParam(name = "value") GioviValue<?> value)
		throws NonWideneable {
		this.value.add(index.value(), new Var<>(value.copy().widenTo(entryType)));
	}

	@Func
	public @Type(GioviTypeList.class) GioviList cut(
		@Param(name = "start", type = @Type(GioviTypeInt.class)) GioviInt start,
		@Param(name = "end", type = @Type(GioviTypeInt.class)) GioviInt end) {
		return new GioviList(entryType, value.subList(start.value(), end.value()));
	}

	//@Func
	//public @Type(GioviTypeList.class) GioviList cut(
	//	@Param(name = "end", type = @Type(GioviTypeInt.class)) GioviInt end) {
	//	return new GioviList(entryType, value.subList(0, end.value()));
	//}

	@Func
	public @Type(GioviTypeList.class) GioviList merge(
		@Param(name = "list", type = @Type(GioviTypeList.class)) GioviList list) {
		var merged = this.copy();
		for (var entry : list.value) {
			if (!merged.value.contains(entry)) {
				merged.value.add(entry);
			}
		}
		return merged;
	}

}
