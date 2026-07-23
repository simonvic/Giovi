package it.simonvic.giovi;

import giovi.lang.GioviValue;

public final class Entry<G extends GioviValue<?>> extends MutableValueWrapper<G> {

	public static <J> Entry<? extends GioviValue<?>> of(J value) {
		return new Entry<>(GioviValue.of(value));
	}

	public Entry(G value) {
		super(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Entry<G> copy() {
		return new Entry<G>((G) unwrap().copy());
	}

}

