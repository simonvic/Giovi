package it.simonvic.giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;

public class VarEntry<T extends GioviValue<?>> extends Var<T> {

	public static <T> VarEntry<? extends GioviValue<?>> of(T value) {
		return new VarEntry<>(GioviValue.of(value));
	}

	public VarEntry(T value) {
		super(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public VarEntry<T> copy() {
		return new VarEntry<T>((T) unwrap().copy());
	}

	@Override
	public VarEntry<?> widenTo(GioviType<?> gioviType) throws NonWideneable {
		return new VarEntry<>(gioviType.widen(this.unwrap()));
	}

}

