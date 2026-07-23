package it.simonvic.giovi;

import giovi.lang.GioviValue;

public final class Member<T extends GioviValue<?>> extends DefaultableValueWrapper<T> {

	public Member(T value, T defaultValue) {
		super(value, defaultValue);
	}

	public Member(T value) {
		super(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Member<T> copy() {
		return new Member<>(
			(T) unwrap().copy(),
			(T) unwrapDefault().copy()
		);
	}

}

