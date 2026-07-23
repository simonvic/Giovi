package it.simonvic.giovi;

import giovi.lang.GioviValue;

public final class Var<G extends GioviValue<?>> extends DefaultableValueWrapper<G> {

	public Var(G value, G defaultValue) {
		super(value, defaultValue);
	}

	public Var(G value) {
		super(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Var<G> copy() {
		return new Var<>(
			(G) unwrap().copy(),
			(G) unwrapDefault().copy()
		);
	}

}

