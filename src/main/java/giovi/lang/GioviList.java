package giovi.lang;

import static it.simonvic.giovi.util.GioviLists.copyOf;

import java.util.ArrayList;
import java.util.List;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.Range;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeList.class)
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
	public List<Var<?>> zero() {
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
	public GioviList copy() {
		return new GioviList(this.entryType, copyOf(this.value));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (right instanceof GioviTypeList t) {
			var newGioviList = t.spawn();
			for (var entry : this.value) {
				newGioviList.value.add(new Var<>(entry.as(t.getEntryType()).asGioviValue()));
			}
			return newGioviList;
		}
		return GioviValue.super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviList(entryType, copyOf(zero()));
			case GioviList v when v.entryType.equals(this.entryType)
				-> new GioviList(entryType, copyOf(v.value()));
			case GioviList v when v.entryType.isWidenableTo(this.entryType)
				-> new GioviList(entryType, copyOf(v.value()));
			default -> GioviValue.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			// case GioviDefault _ -> plus(new GioviList(this.entryType));
			case GioviList v when v.entryType.equals(this.entryType) -> {
				var concat = copyOf(this.value);
				concat.addAll(copyOf(v.value));
				yield new GioviList(entryType, concat);
			}
			case GioviList v when v.entryType.isWidenableTo(this.entryType) -> {
				var concat = copyOf(this.value);
				for (var entry : v.value) {
					try {
						concat.add(new Var<>(entry.value().copy().widenTo(entryType)));
					} catch (NonWideneable ex) {
						throw new IllegalArgumentException(ex);
					}
				}
				yield new GioviList(entryType, concat);
			}
			case GioviValue<?> v -> {
				var concat = copyOf(this.value);
				try {
					concat.add(new Var<>(this.entryType.widen(v)));
				} catch (NonWideneable e) {
					yield GioviValue.super.plus(right);
				}
				yield new GioviList(entryType, concat);
			}
			default -> GioviValue.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			// case GioviDefault _ -> minus(new GioviList(this.entryType));
			case GioviList v when v.entryType.equals(entryType) -> {
				var intersection = copyOf(this.value);
				intersection.removeAll(v.value);
				yield new GioviList(this.entryType, intersection);
			}
			case GioviList v when v.entryType.isWidenableTo(entryType) -> {
				var intersection = copyOf(this.value);
				for (var entry : v.value) {
					try {
						intersection.remove(new Var<>(this.entryType.widen(entry.value())));
					} catch (NonWideneable e) {
						throw new GioviException(e);
					}
				}
				yield new GioviList(this.entryType, intersection);
			}
			case GioviValue<?> v -> {
				var intersection = copyOf(this.value);
				try {
					intersection.remove(new Var<>(this.entryType.widen(v)));
				} catch (NonWideneable e) {
					yield GioviValue.super.minus(right);
				}
				yield new GioviList(this.entryType, intersection);
			}
			default -> GioviValue.super.minus(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.equals(zero()));
			case GioviList v -> {
				// NOTE: we are checking if entries are **equal** instead of **equals**
				if (this.value.size() != v.value.size()) {
					yield new GioviBool(false);
				}
				if (!this.entryType.equals(v.entryType)
					&& !this.entryType.isWidenableTo(v.entryType)) {
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
			case GioviList v when v.entryType.equals(this.entryType)
				-> new GioviBool(this.value.containsAll(v.value));
			case GioviValue<?> v -> {
				for (var entry : this.value) {
					if (entry.value().equal(v).asNativeBool()) {
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

}
