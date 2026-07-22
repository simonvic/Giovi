package it.simonvic.giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;

public final class Const<T extends GioviValue<?>> extends Var<T> {

	public Const(T value) {
		super(value);
	}

	@Override
	public Const<T> copy() {
		return this;
	}

	@Override
	public Const<?> widenTo(GioviType<?> gioviType) throws NonWideneable {
		return new Const<>(gioviType.widen(this.unwrap()));
	}

}

