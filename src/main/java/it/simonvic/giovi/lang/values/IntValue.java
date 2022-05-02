package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

import java.util.Objects;

public class IntValue extends Value<Integer> {
	public IntValue(Integer value) {
		super(value);
	}

	public IntValue() {
		super(0);
	}

	@Override
	public void set(Value<?> value) {
		switch(value.get()) {
			case Integer v -> set(v);
			case Number v -> set(v.intValue());
			default -> super.set(value);
		}
	}

	@Override
	public GioviObject positive() {
		return new IntValue(value);
	}

	@Override
	public GioviObject negative() {
		return new IntValue(-value);
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new IntValue(value + v.get().intValue());
			case IntValue v -> new IntValue(value + v.get());
			case RealValue v -> new RealValue(value + v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new IntValue(value - v.get().intValue());
			case IntValue v -> new IntValue(value - v.get());
			case RealValue v -> new RealValue(value - v.get());
			default -> super.minus(right);
		};
	}

	@Override
	public GioviObject multiply(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new IntValue(value * v.get().intValue());
			case IntValue v -> new IntValue(value * v.get());
			case RealValue v -> new RealValue(value * v.get());
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
			case NaturalValue v -> new IntValue(value % v.get().intValue());
			case IntValue v -> new IntValue(value % v.get());
			case RealValue v -> new RealValue(value.doubleValue() % v.get());
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviObject power(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new IntValue((int) Math.pow(value, v.get().intValue()));
			case IntValue v -> new IntValue((int) Math.pow(value, v.get()));
			case RealValue v -> new RealValue(Math.pow(value.doubleValue(), v.get()));
			default -> super.power(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value <= v.get().intValue());
			case IntValue v -> new BoolValue(value <= v.get());
			case RealValue v -> new BoolValue(value <= v.get());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value >= v.get().intValue());
			case IntValue v -> new BoolValue(value >= v.get());
			case RealValue v -> new BoolValue(value >= v.get());
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value > v.get().intValue());
			case IntValue v -> new BoolValue(value > v.get());
			case RealValue v -> new BoolValue(value > v.get());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value < v.get().intValue());
			case IntValue v -> new BoolValue(value < v.get());
			case RealValue v -> new BoolValue(value < v.get());
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case NaturalValue v -> new BoolValue(value.equals(v.get().intValue()));
			case IntValue v -> new BoolValue(value.equals(v.get()));
			case RealValue v -> new BoolValue(value.doubleValue() == v.get());
			default -> super.equal(right);
		};
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
		final IntValue other = (IntValue) obj;
		return this.value.equals(other.value);
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}
}
