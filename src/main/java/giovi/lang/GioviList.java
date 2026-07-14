package giovi.lang;

import static it.simonvic.giovi.util.GioviLists.copyOf;
import static it.simonvic.giovi.util.GioviUtils.nativeIndexOf;

import java.util.ArrayList;
import java.util.List;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.Range;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeList.class)
public record GioviList(GioviTypeList type, List<Var<?>> value)
	implements GioviComposite<List<Var<?>>> {

	public GioviList(GioviTypeList type, List<Var<?>> value) {
		this.type = type;
		this.value = new ArrayList<>();
		try {
			for (var entry : value) {
				this.value.add(entry.widenTo(this.type.entryType()));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public GioviList(GioviTypeList type) {
		this(type, new ArrayList<>());
	}

	@Override
	public String toString() {
		var i = value.iterator();
		if (!i.hasNext())
			return "[{" + type.entryType() + "}]";
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
			value.add(entry.widenTo(type.entryType()));
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
				value.remove(entry.widenTo(type.entryType()));
			}
		} catch (NonWideneable ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	@Override
	public GioviTypeList getGioviType() {
		return type;
	}

	@Override
	public GioviList copy() {
		return new GioviList(this.type, copyOf(this.value));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (right instanceof GioviTypeList t) {
			var newGioviList = t.spawn();
			for (var entry : this.value) {
				newGioviList.value.add(new Var<>(entry.as(t.entryType()).asGioviValue()));
			}
			return newGioviList;
		}
		return GioviComposite.super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviList v when v.type.entryType().equals(this.type.entryType())
				-> new GioviList(this.type, copyOf(v.value()));
			case GioviList v when v.type.entryType().isWidenableTo(this.type.entryType())
				-> new GioviList(this.type, copyOf(v.value()));
			default -> GioviComposite.super.redirection(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviList v when v.type.entryType().equals(this.type.entryType())
				-> addAll(copyOf(v.value));
			case GioviList v when v.type.entryType().isWidenableTo(this.type.entryType())
				-> addAll(copyOf(v.value));
			case GioviValue<?> v -> {
				try {
					add(new Var<>(v.copy()));
				} catch (IllegalArgumentException ex) {
					GioviComposite.super.redirectionPlus(right);
				}
			}
			default -> GioviComposite.super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		switch (right) {
			case GioviList v when v.type.entryType().equals(type.entryType())
				-> removeAll(v.value);
			case GioviList v when v.type.entryType().isWidenableTo(type.entryType())
				-> removeAllWidened(v.value);
			case GioviValue<?> v -> {
				try {
					remove(new Var<>(this.type.entryType().widen(v)));
				} catch (IllegalArgumentException | NonWideneable e) {
					return GioviComposite.super.redirectionMinus(right);
				}
			}
			default -> GioviComposite.super.redirectionMinus(right);
		}
		return this;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviList v -> {
				// NOTE: we are checking if entries are **equal** instead of **equals**
				if (this.value.size() != v.value.size()) {
					yield GioviBool.FALSE;
				}
				if (!this.type.entryType().equals(v.type.entryType())
					&& !this.type.entryType().isWidenableTo(v.type.entryType())) {
					yield GioviBool.FALSE;
				}
				for (int i = 0; i < this.value.size(); i++) {
					if (!this.value.get(i).equal(v.value.get(i)).asGioviBool().value()) {
						yield GioviBool.FALSE;
					}
				}
				yield GioviBool.TRUE;
			}
			default -> GioviComposite.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviList v when v.type.entryType().equals(this.type.entryType())
				-> GioviBool.of(this.value.containsAll(v.value));
			case GioviValue<?> v -> {
				for (var entry : this.value) {
					if (entry.unwrap().equal(v).asNativeBool()) {
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
		if (right instanceof Var<?> v) {
			right = v.unwrap();
		}
		return switch (right) {
			case GioviValue<?> gioviValue -> switch (gioviValue) {
				case GioviInt(var index)
					-> value.get(nativeIndexOf(index, value.size()));
				default -> GioviComposite.super.access(right);
			};
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value().size());
				case KEYS -> Range.of(0, value().size() - 1, 1);
				case VALUES -> GioviComposite.super.access(right);
			};
			case Range<?> range when range instanceof Range(Integer _, _, _, _) -> {
				var gioviList = new GioviList(this.type);
				range.iterate(index -> gioviList.value.add(this.value.get((int) index).copy()));
				yield gioviList;
			}
			default -> GioviComposite.super.access(right);
		};
	}

}
