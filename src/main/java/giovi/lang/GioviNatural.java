package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.Natural;
import it.simonvic.giovi.exceptions.NonWideneable;

public final class GioviNatural extends GioviNumber<Natural> {

	public static GioviNatural valueOf(String s) throws NumberFormatException {
		return new GioviNatural(Natural.valueOf(s));
	}

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
	public GioviTypeNatural getGioviType() {
		return new GioviTypeNatural();
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		return switch (type) {
			case GioviTypeNatural t -> this;
			case GioviTypeInt t -> this.intValue();
			case GioviTypeReal t -> this.realValue();
			default -> throw new NonWideneable(this, type);
		};
	}

	@Override
	public GioviNatural copy() {
		return new GioviNatural(
			new Natural(this.value.intValue()),
			new Natural(this.defaultValue.intValue()));
	}

	@Override
	public GioviNatural naturalValue() {
		return this;
	}

	@Override
	public GioviInt intValue() {
		return new GioviInt(value.intValue());
	}

	@Override
	public GioviReal realValue() {
		return new GioviReal(value.doubleValue());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> value = new Natural(defaultValue.intValue());
			case GioviNatural v -> value = v.value;
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> value = value.plus(defaultValue);
			case GioviNatural v -> value = value.plus(v.value);
			default -> super.redirectionPlus(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> value = value.minus(defaultValue);
			case GioviNatural v -> value = value.minus(v.value);
			default -> super.redirectionMinus(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> value = value.multiply(defaultValue);
			case GioviNatural v -> value = value.multiply(v.value);
			default -> super.redirectionMultiply(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> value = value.divide(defaultValue);
			case GioviNatural v -> value = value.divide(v.value);
			default -> super.redirectionDivide(content);
		}
		return this;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult positive() {
		return new GioviNatural(value);
	}

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviNatural(
				new Natural(value.intValue() + defaultValue.intValue()));
			case GioviNatural v -> new GioviNatural(
				new Natural(value.intValue() + v.unbox().intValue()));
			case GioviInt v -> new GioviInt(value.intValue() + v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() + v.unbox());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviNatural(
				new Natural(value.intValue() - defaultValue.intValue()));
			case GioviNatural v -> new GioviNatural(
				new Natural(value.intValue() - v.unbox().intValue()));
			case GioviInt v -> new GioviInt(value.intValue() - v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() - v.unbox());
			default -> super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviNatural(
				new Natural(value.intValue() * defaultValue.intValue()));
			case GioviNatural v -> new GioviNatural(
				new Natural(value.intValue() * v.unbox().intValue()));
			case GioviInt v -> new GioviInt(value.intValue() * v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() * v.unbox());
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviNatural(
				new Natural(value.intValue() / defaultValue.intValue()));
			case GioviNatural v -> new GioviReal(value.doubleValue() / v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value.doubleValue() / v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() / v.unbox());
			default -> super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviNatural(
				new Natural(value.intValue() % defaultValue.intValue()));
			case GioviNatural v -> new GioviNatural(
				new Natural(value.intValue() % v.unbox().intValue()));
			case GioviInt v -> new GioviInt(value.intValue() % v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() % v.unbox());
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviNatural(
				new Natural((int) Math.pow(value.intValue(), defaultValue.intValue())));
			case GioviNatural v -> new GioviNatural(
				new Natural((int) Math.pow(value.intValue(), v.unbox().intValue())));
			case GioviInt v -> new GioviInt((int) Math.pow(value.intValue(), v.unbox()));
			case GioviReal v -> new GioviReal(Math.pow(value.doubleValue(), v.unbox()));
			default -> super.power(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.intValue() == defaultValue.intValue());
			case GioviNatural v -> new GioviBool(value.intValue() == v.unbox().intValue());
			case GioviInt v -> new GioviBool(value.intValue() == v.unbox());
			case GioviReal v -> new GioviBool(value.doubleValue() == v.unbox());
			default -> super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}


	@Override
	public GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.intValue() < defaultValue.intValue());
			case GioviNatural v -> new GioviBool(value.intValue() < v.unbox().intValue());
			case GioviInt v -> new GioviBool(value.intValue() < v.unbox());
			case GioviReal v -> new GioviBool(value.doubleValue() < v.unbox());
			default -> super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.intValue() <= defaultValue.intValue());
			case GioviNatural v -> new GioviBool(value.intValue() <= v.unbox().intValue());
			case GioviInt v -> new GioviBool(value.intValue() <= v.unbox());
			case GioviReal v -> new GioviBool(value.doubleValue() <= v.unbox());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.intValue() > defaultValue.intValue());
			case GioviNatural v -> new GioviBool(value.intValue() > v.unbox().intValue());
			case GioviInt v -> new GioviBool(value.intValue() > v.unbox());
			case GioviReal v -> new GioviBool(value.doubleValue() > v.unbox());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.intValue() >= defaultValue.intValue());
			case GioviNatural v -> new GioviBool(value.intValue() >= v.unbox().intValue());
			case GioviInt v -> new GioviBool(value.intValue() >= v.unbox());
			case GioviReal v -> new GioviBool(value.doubleValue() >= v.unbox());
			default -> super.greaterEqual(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviNatural(new Natural(defaultValue.intValue()));
		}
		return super.dot(right);
	}

}
