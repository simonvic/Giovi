package it.simonvic.giovi;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;

public final class Const<T extends GioviValue<?>> extends UnaryValueWrapper<T> {

	public static <T> Const<? extends GioviValue<?>> of(T value) {
		return new Const<>(GioviValue.of(value));
	}

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

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		throw new GioviException("Can't redirect to constants");
	}

}

