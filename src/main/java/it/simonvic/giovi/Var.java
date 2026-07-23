package it.simonvic.giovi;

import giovi.lang.GioviValue;

public final class Var<T extends GioviValue<?>> extends DefaultableValueWrapper<T> {

	public Var(T value, T defaultValue) {
		super(value, defaultValue);
	}

	public Var(T value) {
		super(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Var<T> copy() {
		return new Var<>(
			(T) unwrap().copy(),
			(T) unwrapDefault().copy()
		);
	}

}

