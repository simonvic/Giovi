package it.simonvic.giovi;

import giovi.lang.GioviValue;

public final class Member<G extends GioviValue<?>> extends DefaultableValueWrapper<G> {

	public Member(G value, G defaultValue) {
		super(value, defaultValue);
	}

	public Member(G value) {
		super(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Member<G> copy() {
		return new Member<>(
			(G) unwrap().copy(),
			(G) unwrapDefault().copy()
		);
	}

}

