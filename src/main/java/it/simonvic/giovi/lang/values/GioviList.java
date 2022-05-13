package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.Sequence;

import java.util.ArrayList;
import java.util.List;

public non-sealed class GioviList<T extends GioviValue<?>>
	extends GioviValue<List<T>>
	implements GioviCollection {

	public GioviList(List<T> value, List<T> defaultValue) {
		super(value, defaultValue);
	}

	public GioviList() {
		super(new ArrayList<>(), new ArrayList<>());
	}

	@Override
	public void box(GioviValue<?> value) {
		switch (value) {
			case GioviList<?> v -> this.box((List<T>) v.unbox());
			default -> super.box(value);
		}
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return new GioviBool(value.contains(right.asGioviValue()));
	}

	@Override
	public GioviObject redirection(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value = new ArrayList<>(defaultValue);
			case GioviList<?> v -> value = (List<T>) v.unbox();
			default -> super.redirection(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionPlus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value.addAll(defaultValue);
			case GioviList<?> v -> v.unbox().forEach(gioviValue -> value.add((T) gioviValue));
			case GioviValue<?> v -> value.add((T) v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMinus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value.removeAll(defaultValue);
			case GioviList<?> v -> v.unbox().forEach(gioviValue -> value.remove((T) gioviValue));
			case GioviValue<?> v -> value.remove((T) v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviList<?> v -> new GioviBool(value.equals(v.value));
			default -> super.equal(right);
		};
	}

	public void add(T entry) {
		value.add(entry);
	}

	public void addAll(List<? extends T> seq) {
		value.addAll(seq);
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
		final GioviList<GioviValue<?>> other = (GioviList<GioviValue<?>>) obj;
		return this.value.equals(other.value);
	}

	@Override
	public GioviValue<?> access(GioviObject accessor) {
		return switch (accessor) {
			case GioviInt v -> unbox().get(v.unbox());
			case Accessor v -> switch (v.get()) {
				case "%" -> new GioviInt(unbox().size());
				case "@" -> {
					GioviList<GioviInt> indeces = new GioviList<>();
					indeces.addAll(Sequence.of(0, unbox().size() - 1, 1).getValues());
					yield indeces;
				}
				default -> throw new GioviException("Invalid accessor: " + accessor);
			};
			default -> throw new GioviException("Invalid accessor:" + accessor);
		};
	}
}
