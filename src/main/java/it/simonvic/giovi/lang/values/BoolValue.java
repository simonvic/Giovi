package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public class BoolValue extends Value<Boolean> {
	public BoolValue(Boolean value) {
		super(value);
	}

	public BoolValue() {
		super(false);
	}

	@Override
	public void set(Value<?> value) {
		switch(value.get()) {
			case Boolean v -> set(v);
//			case Integer v -> set(v > 0);
			default -> super.set(value);
		}
	}

	@Override
	public GioviObject not() {
		return new BoolValue(!value);
	}

	@Override
	public GioviObject and(GioviObject right) {
		return switch(right) {
			case BoolValue v -> new BoolValue(value && v.get());
//			case IntValue v -> new BoolValue(value && v.asBool());
			default -> super.and(right);
		};
	}

	@Override
	public GioviObject or(GioviObject right) {
		return switch(right) {
			case BoolValue v -> new BoolValue(value || v.get());
//			case IntValue v -> new BoolValue(value || v.asBool());
			default -> super.and(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch(right) {
			case BoolValue v -> new BoolValue(value.equals(v.get()));
//			case IntValue v -> new BoolValue(value == v.asBool());
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BoolValue other = (BoolValue) obj;
		return this.value.equals(other.value);
	}
}
