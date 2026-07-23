package it.simonvic.giovi;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.GioviException;

public record Const<G extends GioviValue<?>>(G value) implements ValueWrapper<G> {

	@Override
	public G unwrap() {
		return value;
	}

	@Override
	public void setValue(G value) {
		throw new GioviException("Can't mutate constant");
	}

	@Override
	public Const<G> copy() {
		return this;
	}

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		throw new GioviException("Can't redirect to constants");
	}

}

