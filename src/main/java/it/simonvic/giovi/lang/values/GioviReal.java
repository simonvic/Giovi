package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.type.Natural;

public non-sealed class GioviReal extends GioviNumber<Double> {

	public GioviReal(Double value) {
		super(value);
	}

	public GioviReal() {
		super(0d);
	}

	@Override
	public GioviNatural naturalValue() {
		return new GioviNatural(new Natural(value.intValue()));
	}

	@Override
	public GioviInt intValue() {
		return new GioviInt(value.intValue());
	}

	@Override
	public GioviReal realValue() {
		return new GioviReal(value);
	}

	@Override
	public void box(GioviValue<?> value) {
		switch (value.unbox()) {
			case Double v -> box(v);
			case Number v -> box(v.doubleValue());
			default -> super.box(value);
		}
	}

	@Override
	public GioviObject redirection(GioviObject content) {
		if (content instanceof GioviNumber number) {
			value = number.realValue().unbox();
			return this;
		}
		return super.redirection(content);
	}

	@Override
	public GioviObject redirectionPlus(GioviObject right) {
		if (right instanceof GioviNumber number) {
			value += number.realValue().unbox();
			return this;
		}
		return super.redirectionPlus(right);
	}

	@Override
	public GioviObject redirectionMinus(GioviObject right) {
		if (right instanceof GioviNumber number) {
			value -= number.realValue().unbox();
			return this;
		}
		return super.redirectionMinus(right);
	}

	@Override
	public GioviObject redirectionMultiply(GioviObject right) {
		if (right instanceof GioviNumber number) {
			value *= number.realValue().unbox();
			return this;
		}
		return super.redirectionMultiply(right);
	}

	@Override
	public GioviObject redirectionDivide(GioviObject right) {
		if (right instanceof GioviNumber number) {
			value /= number.intValue().unbox();
			return this;
		}
		return super.redirectionDivide(right);
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
			case GioviNatural v -> new GioviReal(value + v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value + v.unbox());
			case GioviReal v -> new GioviReal(value + v.unbox());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(value - v.unbox().intValue());
			case GioviInt v -> new GioviReal(value - v.unbox());
			case GioviReal v -> new GioviReal(value - v.unbox());
			default -> super.minus(right);
		};
	}

	@Override
	public GioviObject multiply(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(value * v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value * v.unbox());
			case GioviReal v -> new GioviReal(value * v.unbox());
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviObject divide(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(value / v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value / v.unbox());
			case GioviReal v -> new GioviReal(value / v.unbox());
			default -> super.divide(right);
		};
	}

	@Override
	public GioviObject modulo(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(value % v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value % v.unbox());
			case GioviReal v -> new GioviReal(value % v.unbox());
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviObject power(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviReal(Math.pow(value, v.unbox().doubleValue()));
			case GioviInt v -> new GioviReal(Math.pow(value, v.unbox()));
			case GioviReal v -> new GioviReal(Math.pow(value, v.unbox()));
			default -> super.power(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value <= v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value <= v.unbox());
			case GioviReal v -> new GioviBool(value <= v.unbox());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value >= v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value >= v.unbox());
			case GioviReal v -> new GioviBool(value >= v.unbox());
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value > v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value > v.unbox());
			case GioviReal v -> new GioviBool(value > v.unbox());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value < v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value < v.unbox());
			case GioviReal v -> new GioviBool(value < v.unbox());
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviNatural v -> new GioviBool(value == v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value == v.unbox().doubleValue());
			case GioviReal v -> new GioviBool(value.equals(v.unbox()));
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}

}
