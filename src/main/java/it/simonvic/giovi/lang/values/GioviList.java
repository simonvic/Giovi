package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

import java.util.ArrayList;
import java.util.List;

public non-sealed class GioviList<T extends GioviValue<?>> extends GioviValue<List<T>> {

	public GioviList() {
		super(new ArrayList<>());
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return new GioviBool(value.contains(right.asGioviValue()));
	}


	@Override
	public GioviObject redirection(GioviObject right) {
		switch (right) {
			case GioviList<?> v -> value = (List<T>) v.get();
			default -> super.redirection(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionPlus(GioviObject right) {
		switch (right) {
			case GioviList<?> v -> v.get().forEach(gioviValue -> value.add((T) gioviValue));
			case GioviValue<?> v -> value.add((T) v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMinus(GioviObject right) {
		switch (right) {
			case GioviList<?> v -> v.get().forEach(gioviValue -> value.remove((T) gioviValue));
			case GioviValue<?> v -> value.remove((T) v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
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
}
