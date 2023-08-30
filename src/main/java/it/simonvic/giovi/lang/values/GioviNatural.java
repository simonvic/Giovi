package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.primitives.Natural;

public final class GioviNatural extends GioviNumber<Natural> {

	public GioviNatural(Natural value, Natural defaultValue) {
		super(value, defaultValue);
	}

	public GioviNatural(Natural value) {
		super(value, new Natural());
	}

	public GioviNatural() {
		super(new Natural(), new Natural());
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypePrimitive(GioviNatural.class);
	}

	@Override
	public GioviNatural copy() {
		return new GioviNatural(
			new Natural(this.value.intValue()),
			new Natural(this.defaultValue.intValue()));
	}

	@Override
	public GioviNatural naturalValue() {
		return new GioviNatural(value);
	}

	@Override
	public GioviInt intValue() {
		return new GioviInt(value.intValue());
	}

	@Override
	public GioviReal realValue() {
		return new GioviReal(value.doubleValue());
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		switch (gioviValue.unbox()) {
			case Natural v -> box(v);
			case Number v -> box(new Natural(v.intValue()));
			default -> super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue.unbox()) {
			case Natural v -> setDefaultValue(new Natural(v.intValue()));
			case Number v -> setDefaultValue(new Natural(v.intValue()));
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public void boxDefault() {
		value = new Natural(defaultValue.intValue());
	}

	@Override
	public GioviObject redirection(GioviObject content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			case GioviNumber<?> v -> value = v.naturalValue().unbox();
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviObject redirectionPlus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value = value.plus(defaultValue);
			case GioviNumber<?> v -> value = value.plus(v.naturalValue().unbox());
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMinus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value = value.minus(defaultValue);
			case GioviNumber<?> v -> value = value.minus(v.naturalValue().unbox());
			default -> super.redirectionMinus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMultiply(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value = value.multiply(defaultValue);
			case GioviNumber<?> v -> value = value.multiply(v.naturalValue().unbox());
			default -> super.redirectionMultiply(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionDivide(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value = value.divide(defaultValue);
			case GioviNumber<?> v -> value = value.divide(v.naturalValue().unbox());
			default -> super.redirectionDivide(right);
		}
		return this;
	}


	@Override
	public GioviObject positive() {
		return new GioviNatural(value);
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviNatural(
				new Natural(value.intValue() + defaultValue.intValue()));
			case GioviNatural v -> new GioviNatural(
				new Natural(value.intValue() + v.unbox().intValue()));
			case GioviInt v -> new GioviInt(value.intValue() + v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() + v.unbox());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviNatural(
				new Natural(value.intValue() - defaultValue.intValue()));
			case GioviNatural v -> new GioviNatural(
				new Natural(value.intValue() - v.unbox().intValue()));
			case GioviInt v -> new GioviInt(value.intValue() - v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() - v.unbox());
			default -> super.minus(right);
		};
	}

	@Override
	public GioviObject multiply(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviNatural(
				new Natural(value.intValue() * defaultValue.intValue()));
			case GioviNatural v -> new GioviNatural(
				new Natural(value.intValue() * v.unbox().intValue()));
			case GioviInt v -> new GioviInt(value.intValue() * v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() * v.unbox());
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviObject divide(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviNatural(
				new Natural(value.intValue() / defaultValue.intValue()));
			case GioviNatural v -> new GioviReal(value.doubleValue() / v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value.doubleValue() / v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() / v.unbox());
			default -> super.divide(right);
		};
	}

	@Override
	public GioviObject modulo(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviNatural(
				new Natural(value.intValue() % defaultValue.intValue()));
			case GioviNatural v -> new GioviNatural(
				new Natural(value.intValue() % v.unbox().intValue()));
			case GioviInt v -> new GioviInt(value.intValue() % v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() % v.unbox());
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviObject power(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviNatural(
				new Natural((int) Math.pow(value.intValue(), defaultValue.intValue())));
			case GioviNatural v -> new GioviNatural(
				new Natural((int) Math.pow(value.intValue(), v.unbox().intValue())));
			case GioviInt v -> new GioviInt((int) Math.pow(value.intValue(), v.unbox()));
			case GioviReal v -> new GioviReal(Math.pow(value.doubleValue(), v.unbox()));
			default -> super.power(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.intValue() <= defaultValue.intValue());
			case GioviNatural v -> new GioviBool(value.intValue() <= v.unbox().intValue());
			case GioviInt v -> new GioviBool(value.intValue() <= v.unbox());
			case GioviReal v -> new GioviBool(value.doubleValue() <= v.unbox());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.intValue() >= defaultValue.intValue());
			case GioviNatural v -> new GioviBool(value.intValue() >= v.unbox().intValue());
			case GioviInt v -> new GioviBool(value.intValue() >= v.unbox());
			case GioviReal v -> new GioviBool(value.doubleValue() >= v.unbox());
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.intValue() > defaultValue.intValue());
			case GioviNatural v -> new GioviBool(value.intValue() > v.unbox().intValue());
			case GioviInt v -> new GioviBool(value.intValue() > v.unbox());
			case GioviReal v -> new GioviBool(value.doubleValue() > v.unbox());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.intValue() < defaultValue.intValue());
			case GioviNatural v -> new GioviBool(value.intValue() < v.unbox().intValue());
			case GioviInt v -> new GioviBool(value.intValue() < v.unbox());
			case GioviReal v -> new GioviBool(value.doubleValue() < v.unbox());
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.intValue() == defaultValue.intValue());
			case GioviNatural v -> new GioviBool(value.intValue() == v.unbox().intValue());
			case GioviInt v -> new GioviBool(value.intValue() == v.unbox());
			case GioviReal v -> new GioviBool(value.doubleValue() == v.unbox());
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}

	public static GioviNatural valueOf(String s) throws NumberFormatException {
		return new GioviNatural(Natural.valueOf(s));
	}

}
