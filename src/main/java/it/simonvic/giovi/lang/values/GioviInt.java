package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.type.Natural;

public non-sealed class GioviInt extends GioviNumber<Integer> {

	public GioviInt(Integer value) {
		super(value);
	}

	public GioviInt() {
		super(0);
	}

	@Override
	public GioviNatural naturalValue() {
		return new GioviNatural(new Natural(value));
	}

	@Override
	public GioviInt intValue() {
		return new GioviInt(value);
	}

	@Override
	public GioviReal realValue() {
		return new GioviReal(value.doubleValue());
	}

	@Override
	public void set(GioviValue<?> value) {
		switch (value.get()) {
			case Integer v -> set(v);
			case Number v -> set(v.intValue());
			default -> super.set(value);
		}
	}

	@Override
	public GioviObject positive() {
		return new GioviInt(value);
	}

	@Override
	public GioviObject negative() {
		return new GioviInt(-value);
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviInt(value + v.get().intValue());
			case GioviInt v -> new GioviInt(value + v.get());
			case GioviReal v -> new GioviReal(value + v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviInt(value - v.get().intValue());
			case GioviInt v -> new GioviInt(value - v.get());
			case GioviReal v -> new GioviReal(value - v.get());
			default -> super.minus(right);
		};
	}

	@Override
	public GioviObject multiply(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviInt(value * v.get().intValue());
			case GioviInt v -> new GioviInt(value * v.get());
			case GioviReal v -> new GioviReal(value * v.get());
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviObject divide(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(value.doubleValue() / v.get().doubleValue());
			case GioviInt v -> new GioviReal(value.doubleValue() / v.get());
			case GioviReal v -> new GioviReal(value.doubleValue() / v.get());
			default -> super.divide(right);
		};
	}

	@Override
	public GioviObject modulo(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviInt(value % v.get().intValue());
			case GioviInt v -> new GioviInt(value % v.get());
			case GioviReal v -> new GioviReal(value.doubleValue() % v.get());
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviObject power(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviInt((int) Math.pow(value, v.get().intValue()));
			case GioviInt v -> new GioviInt((int) Math.pow(value, v.get()));
			case GioviReal v -> new GioviReal(Math.pow(value.doubleValue(), v.get()));
			default -> super.power(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value <= v.get().intValue());
			case GioviInt v -> new GioviBool(value <= v.get());
			case GioviReal v -> new GioviBool(value <= v.get());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value >= v.get().intValue());
			case GioviInt v -> new GioviBool(value >= v.get());
			case GioviReal v -> new GioviBool(value >= v.get());
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value > v.get().intValue());
			case GioviInt v -> new GioviBool(value > v.get());
			case GioviReal v -> new GioviBool(value > v.get());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value < v.get().intValue());
			case GioviInt v -> new GioviBool(value < v.get());
			case GioviReal v -> new GioviBool(value < v.get());
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value.equals(v.get().intValue()));
			case GioviInt v -> new GioviBool(value.equals(v.get()));
			case GioviReal v -> new GioviBool(value.doubleValue() == v.get());
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}
}
