package it.simonvic.giovi.lang.values;

public final class GioviVoid extends GioviPrimitive<Void> {

	public GioviVoid() {
		super(null, null);
	}

	@Override
	public GioviTypeVoid getGioviType() {
		return new GioviTypeVoid();
	}

	@Override
	public Class<?> getValueType() {
		return Void.class;
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
