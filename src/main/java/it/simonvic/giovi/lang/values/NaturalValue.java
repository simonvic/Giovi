package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.type.Natural;

public non-sealed class NaturalValue extends Value<Natural> {

	public NaturalValue(Natural value) {
		super(value);
	}

	public NaturalValue() {
		super(new Natural());
	}

	@Override
	public void set(Value<?> value) {
		switch(value.get()) {
			case Natural v -> set(v);
			case Number v -> set(new Natural(v.intValue()));
			default -> super.set(value);
		}
	}

	@Override
	public GioviObject positive() {
		return new NaturalValue(value);
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new NaturalValue(new Natural(value.intValue() + v.get().intValue()));
			case IntValue v -> new IntValue(value.intValue() + v.get());
			case RealValue v -> new RealValue(value.doubleValue() + v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new NaturalValue(new Natural(value.intValue() - v.get().intValue()));
			case IntValue v -> new IntValue(value.intValue() - v.get());
			case RealValue v -> new RealValue(value.doubleValue() - v.get());
			default -> super.minus(right);
		};
	}

	@Override
	public GioviObject multiply(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new NaturalValue(new Natural(value.intValue() * v.get().intValue()));
			case IntValue v -> new IntValue(value.intValue() * v.get());
			case RealValue v -> new RealValue(value.doubleValue() * v.get());
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviObject divide(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new RealValue(value.doubleValue() / v.get().doubleValue());
			case IntValue v -> new RealValue(value.doubleValue() / v.get());
			case RealValue v -> new RealValue(value.doubleValue() / v.get());
			default -> super.divide(right);
		};
	}

	@Override
	public GioviObject modulo(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new NaturalValue(new Natural(value.intValue() % v.get().intValue()));
			case IntValue v -> new IntValue(value.intValue() % v.get());
			case RealValue v -> new RealValue(value.doubleValue() % v.get());
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviObject power(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new NaturalValue(new Natural((int) Math.pow(value.intValue(), v.get().intValue())));
			case IntValue v -> new IntValue((int) Math.pow(value.intValue(), v.get()));
			case RealValue v -> new RealValue(Math.pow(value.doubleValue(), v.get()));
			default -> super.power(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value.intValue() <= v.get().intValue());
			case IntValue v -> new BoolValue(value.intValue() <= v.get());
			case RealValue v -> new BoolValue(value.doubleValue() <= v.get());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value.intValue() >= v.get().intValue());
			case IntValue v -> new BoolValue(value.intValue() >= v.get());
			case RealValue v -> new BoolValue(value.doubleValue() >= v.get());
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value.intValue() > v.get().intValue());
			case IntValue v -> new BoolValue(value.intValue() > v.get());
			case RealValue v -> new BoolValue(value.doubleValue() > v.get());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value.intValue() < v.get().intValue());
			case IntValue v -> new BoolValue(value.intValue() < v.get());
			case RealValue v -> new BoolValue(value.doubleValue() < v.get());
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value.intValue() == v.get().intValue());
			case IntValue v -> new BoolValue(value.intValue() == v.get());
			case RealValue v -> new BoolValue(value.doubleValue() == v.get());
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}

}
