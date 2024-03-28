package giovi.lang;

import giovi.Function;

public final class GioviTypeFunction extends GioviType<GioviFunction> {

	public GioviTypeFunction() {
		super(GioviFunction.class);
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return this.equals(to);
	}

	@Override
	public GioviValue<?> spawn() {
		return new GioviFunction(Function.EMPTY);
	}

	@Override
	public GioviType<GioviFunction> copy() {
		return new GioviTypeFunction();
	}

}
