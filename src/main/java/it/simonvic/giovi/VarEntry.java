package it.simonvic.giovi;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;

public final class VarEntry<T extends GioviValue<?>> extends Var<T> {

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

	// TODO: setting the value in a redirection is different than the GioviValue
	// implementation and might lead to a double setting of the value

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (T) this.value.redirection(content);
		return this.value;
	}


}

