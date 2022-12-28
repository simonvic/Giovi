package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.type.Natural;

public non-sealed class GioviInt extends GioviNumber<Integer> {

	public GioviInt(Integer value, Integer defaultValue) {
		super(value, defaultValue);
	}

	public GioviInt(Integer value) {
		super(value, 0);
	}

	public GioviInt() {
		super(0, 0);
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


//	@Override
//	public GioviValue<?> castTo(GioviType type) {
//		Class<? extends GioviValue<?>> typeClass = type.unbox();
//		if (typeClass == GioviBool.class) return new GioviBool(value > 0);
//		return super.castTo(type);
//	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		switch (gioviValue.unbox()) {
			case Integer v -> box(v);
			case Number v -> box(v.intValue());
			default -> super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue.unbox()) {
			case Integer v -> setDefaultValue(v);
			case Number v -> setDefaultValue(v.intValue());
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public GioviObject redirection(GioviObject content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			case GioviNumber v -> value = v.intValue().unbox();
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviObject redirectionPlus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value += defaultValue;
			case GioviNumber v -> value += v.intValue().unbox();
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMinus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value -= defaultValue;
			case GioviNumber v -> value -= v.intValue().unbox();
			default -> super.redirectionMinus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMultiply(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value *= defaultValue;
			case GioviNumber v -> value *= v.intValue().unbox();
			default -> super.redirectionMultiply(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionDivide(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value /= defaultValue;
			case GioviNumber v -> value /= v.intValue().unbox();
			default -> super.redirectionDivide(right);
		}
		return this;
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
			case GioviDefault v -> new GioviInt(value + defaultValue);
			case GioviNatural v -> new GioviInt(value + v.unbox().intValue());
			case GioviInt v -> new GioviInt(value + v.unbox());
			case GioviReal v -> new GioviReal(value + v.unbox());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviInt(value - defaultValue);
			case GioviNatural v -> new GioviInt(value - v.unbox().intValue());
			case GioviInt v -> new GioviInt(value - v.unbox());
			case GioviReal v -> new GioviReal(value - v.unbox());
			default -> super.minus(right);
		};
	}

	@Override
	public GioviObject multiply(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviInt(value * defaultValue);
			case GioviNatural v -> new GioviInt(value * v.unbox().intValue());
			case GioviInt v -> new GioviInt(value * v.unbox());
			case GioviReal v -> new GioviReal(value * v.unbox());
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviObject divide(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviInt(value / defaultValue);
			case GioviNatural v -> new GioviReal(value.doubleValue() / v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value.doubleValue() / v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() / v.unbox());
			default -> super.divide(right);
		};
	}

	@Override
	public GioviObject modulo(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviInt(value % defaultValue);
			case GioviNatural v -> new GioviInt(value % v.unbox().intValue());
			case GioviInt v -> new GioviInt(value % v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() % v.unbox());
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviObject power(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviInt((int) Math.pow(value, defaultValue));
			case GioviNatural v -> new GioviInt((int) Math.pow(value, v.unbox().intValue()));
			case GioviInt v -> new GioviInt((int) Math.pow(value, v.unbox()));
			case GioviReal v -> new GioviReal(Math.pow(value.doubleValue(), v.unbox()));
			default -> super.power(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value <= defaultValue);
			case GioviNatural v -> new GioviBool(value <= v.unbox().intValue());
			case GioviInt v -> new GioviBool(value <= v.unbox());
			case GioviReal v -> new GioviBool(value <= v.unbox());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value >= defaultValue);
			case GioviNatural v -> new GioviBool(value >= v.unbox().intValue());
			case GioviInt v -> new GioviBool(value >= v.unbox());
			case GioviReal v -> new GioviBool(value >= v.unbox());
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value > defaultValue);
			case GioviNatural v -> new GioviBool(value > v.unbox().intValue());
			case GioviInt v -> new GioviBool(value > v.unbox());
			case GioviReal v -> new GioviBool(value > v.unbox());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value < defaultValue);
			case GioviNatural v -> new GioviBool(value < v.unbox().intValue());
			case GioviInt v -> new GioviBool(value < v.unbox());
			case GioviReal v -> new GioviBool(value < v.unbox());
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviNatural v -> new GioviBool(value.equals(v.unbox().intValue()));
			case GioviInt v -> new GioviBool(value.equals(v.unbox()));
			case GioviReal v -> new GioviBool(value.doubleValue() == v.unbox());
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}
}
