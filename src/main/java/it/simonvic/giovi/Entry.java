package it.simonvic.giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;

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

	@Override
	public Entry<?> widenTo(GioviType<?> gioviType) throws NonWideneable {
		return new Entry<>(gioviType.widen(this.unwrap()));
	}

}

