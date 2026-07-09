package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeAny() implements GioviType<GioviAny> {

	@Override
	public Class<? extends GioviAny> value() {
		return GioviAny.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return true;
	}

	@Override
	public GioviAny widen(GioviValue<?> value) throws NonWideneable {
		return new GioviAny(value);
	}

	@Override
	public GioviAny spawn() {
		return new GioviAny(new GioviVoid());
	}

	@Override
	public GioviType<GioviAny> copy() {
		return this;
	}

}

