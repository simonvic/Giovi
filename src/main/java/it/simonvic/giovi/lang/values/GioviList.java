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
		return new GioviBool(value.contains(right.asValue()));
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case GioviValue<?> v -> {
				value.add((T) v);
				yield this;
			}
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return super.minus(right);
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
