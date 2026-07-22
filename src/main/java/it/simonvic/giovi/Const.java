package it.simonvic.giovi;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;

public record Const<T extends GioviValue<?>>(T value) implements ValueWrapper<T> {

	@Override
	public T unwrap() {
		return value;
	}

	@Override
	public void setValue(T value) {
		throw new GioviException("Can't mutate constant");
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

