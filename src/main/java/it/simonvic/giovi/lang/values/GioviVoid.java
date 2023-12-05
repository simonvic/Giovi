package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.GioviException;

public final class GioviVoid extends GioviPrimitive<Void> {

	public GioviVoid() {
		super(null, null);
	}

	@Override
	public Class<?> getValueType() {
		return Void.class;
	}

	@Override
	public GioviValue<?> castTo(GioviType<?> type) {
		throw new GioviException("Cast not implemented: " + this.getGioviType() + " to " + type);
	}

	@Override
	public String toString() {
		return "void";
	}

	@Override
	public GioviValue<Void> copy() {
		return new GioviVoid();
	}

}
