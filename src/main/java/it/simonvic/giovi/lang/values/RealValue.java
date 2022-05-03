package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public non-sealed class RealValue extends Value<Double> {
	public RealValue(Double value) {
		super(value);
	}

	public RealValue() {
		super(0d);
	}

	@Override
	public void set(Value<?> value) {
		switch(value.get()) {
			case Double v -> set(v);
			case Number v -> set(v.doubleValue());
			default -> super.set(value);
		}
	}

	@Override
	public GioviObject positive() {
		return new RealValue(value);
	}

	@Override
	public GioviObject negative() {
		return new RealValue(-value);
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new RealValue(value + v.get().doubleValue());
			case IntValue v -> new RealValue(value + v.get());
			case RealValue v -> new RealValue(value + v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new RealValue(value - v.get().intValue());
			case IntValue v -> new RealValue(value - v.get());
			case RealValue v -> new RealValue(value - v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject multiply(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new RealValue(value * v.get().doubleValue());
			case IntValue v -> new RealValue(value * v.get());
			case RealValue v -> new RealValue(value * v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject divide(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new RealValue(value / v.get().doubleValue());
			case IntValue v -> new RealValue(value / v.get());
			case RealValue v -> new RealValue(value / v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject modulo(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new RealValue(value % v.get().doubleValue());
			case IntValue v -> new RealValue(value % v.get());
			case RealValue v -> new RealValue(value % v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject power(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new RealValue(Math.pow(value, v.get().doubleValue()));
			case IntValue v -> new RealValue(Math.pow(value, v.get()));
			case RealValue v -> new RealValue(Math.pow(value, v.get()));
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value <= v.get().doubleValue());
			case IntValue v -> new BoolValue(value <= v.get());
			case RealValue v -> new BoolValue(value <= v.get());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value >= v.get().doubleValue());
			case IntValue v -> new BoolValue(value >= v.get());
			case RealValue v -> new BoolValue(value >= v.get());
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value > v.get().doubleValue());
			case IntValue v -> new BoolValue(value > v.get());
			case RealValue v -> new BoolValue(value > v.get());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value < v.get().doubleValue());
			case IntValue v -> new BoolValue(value < v.get());
			case RealValue v -> new BoolValue(value < v.get());
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value == v.get().doubleValue());
			case IntValue v -> new BoolValue(value == v.get().doubleValue());
			case RealValue v -> new BoolValue(value.equals(v.get()));
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}

}
