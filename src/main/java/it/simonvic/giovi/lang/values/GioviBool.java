package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public non-sealed class GioviBool extends GioviValue<Boolean> {
	public GioviBool(Boolean value) {
		super(value);
	}

	public GioviBool() {
		super(false);
	}

	@Override
	public void set(GioviValue<?> value) {
		switch(value.get()) {
			case Boolean v -> set(v);
//			case Integer v -> set(v > 0);
			default -> super.set(value);
		}
	}

	@Override
	public GioviObject not() {
		return new GioviBool(!value);
	}

	@Override
	public GioviObject and(GioviObject right) {
		return switch(right) {
			case GioviBool v -> new GioviBool(value && v.get());
//			case IntValue v -> new BoolValue(value && v.asBool());
			default -> super.and(right);
		};
	}

	@Override
	public GioviObject or(GioviObject right) {
		return switch(right) {
			case GioviBool v -> new GioviBool(value || v.get());
//			case IntValue v -> new BoolValue(value || v.asBool());
			default -> super.and(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch(right) {
			case GioviBool v -> new GioviBool(value.equals(v.get()));
//			case IntValue v -> new BoolValue(value == v.asBool());
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}

}
