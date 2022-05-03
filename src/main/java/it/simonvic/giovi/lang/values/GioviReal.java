package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public non-sealed class GioviReal extends GioviNumber<Double> {
	public GioviReal(Double value) {
		super(value);
	}

	public GioviReal() {
		super(0d);
	}

	@Override
	public void set(GioviValue<?> value) {
		switch(value.get()) {
			case Double v -> set(v);
			case Number v -> set(v.doubleValue());
			default -> super.set(value);
		}
	}

	@Override
	public GioviObject positive() {
		return new GioviReal(value);
	}

	@Override
	public GioviObject negative() {
		return new GioviReal(-value);
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(value + v.get().doubleValue());
			case GioviInt v -> new GioviReal(value + v.get());
			case GioviReal v -> new GioviReal(value + v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(value - v.get().intValue());
			case GioviInt v -> new GioviReal(value - v.get());
			case GioviReal v -> new GioviReal(value - v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject multiply(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(value * v.get().doubleValue());
			case GioviInt v -> new GioviReal(value * v.get());
			case GioviReal v -> new GioviReal(value * v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject divide(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(value / v.get().doubleValue());
			case GioviInt v -> new GioviReal(value / v.get());
			case GioviReal v -> new GioviReal(value / v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject modulo(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(value % v.get().doubleValue());
			case GioviInt v -> new GioviReal(value % v.get());
			case GioviReal v -> new GioviReal(value % v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject power(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(Math.pow(value, v.get().doubleValue()));
			case GioviInt v -> new GioviReal(Math.pow(value, v.get()));
			case GioviReal v -> new GioviReal(Math.pow(value, v.get()));
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value <= v.get().doubleValue());
			case GioviInt v -> new GioviBool(value <= v.get());
			case GioviReal v -> new GioviBool(value <= v.get());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value >= v.get().doubleValue());
			case GioviInt v -> new GioviBool(value >= v.get());
			case GioviReal v -> new GioviBool(value >= v.get());
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value > v.get().doubleValue());
			case GioviInt v -> new GioviBool(value > v.get());
			case GioviReal v -> new GioviBool(value > v.get());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value < v.get().doubleValue());
			case GioviInt v -> new GioviBool(value < v.get());
			case GioviReal v -> new GioviBool(value < v.get());
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value == v.get().doubleValue());
			case GioviInt v -> new GioviBool(value == v.get().doubleValue());
			case GioviReal v -> new GioviBool(value.equals(v.get()));
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}

}
