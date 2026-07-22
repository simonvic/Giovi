package it.simonvic.giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;

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

	@Override
	public Member<?> widenTo(GioviType<?> gioviType) throws NonWideneable {
		return new Member<>(
			gioviType.widen(this.unwrap()),
			gioviType.widen(this.unwrapDefault())
		);
	}


}

