package it.simonvic.giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;

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

	@Override
	public Var<?> widenTo(GioviType<?> gioviType) throws NonWideneable {
		return new Var<>(
			gioviType.widen(this.unwrap()),
			gioviType.widen(this.unwrapDefault())
		);
	}

}

