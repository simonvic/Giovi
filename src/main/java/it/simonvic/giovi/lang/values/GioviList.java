package it.simonvic.giovi.lang.values;

import java.util.ArrayList;
import java.util.List;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.Accessor;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.Sequence;

public non-sealed class GioviList extends GioviCollection<List<GioviValue<?>>> {

	private final GioviType<?> entryType;

	public GioviList(GioviType<?> entryType) {
		this(entryType, new ArrayList<>(), new ArrayList<>());
	}

	public GioviList(List<GioviValue<?>> entries) {
		this(null, entries, new ArrayList<>());
	}

	public GioviList(
		GioviType<?> entryType,
		List<GioviValue<?>> value,
		List<GioviValue<?>> defaultValue) {

		super(value, defaultValue);
		this.entryType = entryType != null ? entryType : GioviType.inferGioviType(value);
		checkCasts(this.value, this.entryType);
		checkCasts(this.defaultValue, this.entryType);
	}


	public static void checkCasts(List<GioviValue<?>> entries, GioviType<?> type) {
		entries.forEach(entry -> entry.castTo(type));
	}

	public void add(GioviValue<?> entry) {
		value.add(entry.castTo(entryType));
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
	public GioviValue<?> castTo(GioviType<?> type) {
		if (type instanceof GioviTypeList t) {
			var list = new GioviList(t.getEntryType());
			this.value.forEach(list::add);
			return new GioviList(value);
		}
		return super.castTo(type);
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

	@Override
	public GioviObject contains(GioviObject right) {
		return new GioviBool(value.contains(right.asGioviValue()));
	}

	@Override
	public GioviObject redirection(GioviObject right) {
		switch (right) {
			case GioviDefault v -> boxDefault();
			case GioviList v -> this.init(v.unbox());
			default -> super.redirection(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionPlus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> addAll(defaultValue);
			case GioviList v -> addAll(v.unbox());
			case GioviValue<?> v -> add(v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMinus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> removeAll(defaultValue);
			case GioviList v -> removeAll(v.unbox());
			case GioviValue<?> v -> remove(v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviList v -> new GioviBool(value.equals(v.unbox()));
			default -> super.equal(right);
		};
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final GioviList other = (GioviList) obj;
		return this.value.equals(other.value);
	}

	@Override
	public GioviValue<?> access(GioviValue<?> accessor) {
		if (accessor instanceof GioviInt index) {
			return value.get(index.unbox());
		}
		throw new GioviException("Invalid accessor: " + accessor);
	}

	@Override
	public GioviValue<?> access(Accessor accessor) {
		return switch (accessor.get()) {
			case "%" -> new GioviInt(unbox().size());
			case "@" -> {
				GioviList indeces = new GioviList(entryType);
				indeces.addAll(Sequence.of(0, unbox().size() - 1, 1).getValues());
				yield indeces;
			}
			default -> throw new GioviException("Invalid accessor: " + accessor);
		};
	}
}
