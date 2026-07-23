package it.simonvic.giovi;

import giovi.lang.GioviValue;

public final class Entry<T extends GioviValue<?>> extends MutableValueWrapper<T> {

	public static <T> Entry<? extends GioviValue<?>> of(T value) {
		return new Entry<>(GioviValue.of(value));
	}

	public Entry(T value) {
		super(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Entry<T> copy() {
		return new Entry<T>((T) unwrap().copy());
	}

}

