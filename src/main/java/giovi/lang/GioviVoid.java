package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;

// TODO: what if we make Void the variadic netrual element for all operations?
public final record GioviVoid() implements GioviPrimitive<Void> {

	@Override
	public Void value() {
		return null;
	}

	@Override
	public Void defaultValue() {
		return null;
	}

	@Override
	public String toString() {
		return "void";
	}

	@Override
	public Class<?> getValueType() {
		return Void.class;
	}

	@Override
	public GioviTypeVoid getGioviType() {
		return new GioviTypeVoid();
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		// TODO: eventually just do type.spawn()
		throw new NonWideneable(this, type);
	}

	@Override
	public GioviValue<Void> copy() {
		return new GioviVoid();
	}

}
