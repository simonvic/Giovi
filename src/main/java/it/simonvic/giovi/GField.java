package it.simonvic.giovi;

import giovi.lang.GioviValue;

public final class GField<G extends GioviValue<?>> extends DefaultableValueWrapper<G> {

	public GField(G value, G defaultValue) {
		super(value, defaultValue);
	}

	public GField(G value) {
		super(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public GField<G> copy() {
		return new GField<>(
			(G) unwrap().copy(),
			(G) unwrapDefault().copy()
		);
	}

}

