package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

import java.util.ArrayList;
import java.util.List;

public non-sealed class ListValue<T extends Value<?>> extends Value<List<T>> {

	public ListValue() {
		super(new ArrayList<>());
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return new BoolValue(value.contains(right.asValue()));
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case Value<?> v -> {
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
			case ListValue<?> v -> new BoolValue(value.equals(v.value));
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
		final ListValue<Value<?>> other = (ListValue<Value<?>>) obj;
		return this.value.equals(other.value);
	}
}
