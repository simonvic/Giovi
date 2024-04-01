package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.Natural;
import it.simonvic.giovi.exceptions.NonWideneable;

public final class GioviReal extends GioviNumber<Double> {

	public static GioviReal valueOf(String s) throws NumberFormatException {
		s = s.replace("r", "") // r suffix
			.replace("_", ""); // separators
		if (s.startsWith("0b")) {
			return new GioviReal(Double.parseDouble(s.replace("0b", "")));
		}
		if (s.startsWith("0o")) {
			return new GioviReal(Double.parseDouble(s.replace("0o", "")));
		}
		return new GioviReal(Double.valueOf(s));
	}

	public GioviReal(Double value, Double defaultValue) {
		super(value, defaultValue);
	}

	public GioviReal(Double value) {
		super(value, 0d);
	}

	public GioviReal() {
		super(0d, 0d);
	}

	@Override
	public GioviTypeReal getGioviType() {
		return new GioviTypeReal();
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		if (type instanceof GioviTypeReal) {
			return this;
		}
		throw new NonWideneable(this, type);
	}

	@Override
	public GioviReal copy() {
		return new GioviReal(this.value, this.defaultValue);
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

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> value = defaultValue;
			case GioviNatural v -> value = v.value.doubleValue();
			case GioviInt v -> value = v.value.doubleValue();
			case GioviReal v -> value = v.value.doubleValue();
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> value += defaultValue;
			case GioviNatural v -> value += v.value.doubleValue();
			case GioviInt v -> value += v.value.doubleValue();
			case GioviReal v -> value += v.value.doubleValue();
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> value -= defaultValue;
			case GioviNatural v -> value -= v.value.doubleValue();
			case GioviInt v -> value -= v.value.doubleValue();
			case GioviReal v -> value -= v.value.doubleValue();
			default -> super.redirectionMinus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> value *= defaultValue;
			case GioviNatural v -> value *= v.value.doubleValue();
			case GioviInt v -> value *= v.value.doubleValue();
			case GioviReal v -> value *= v.value.doubleValue();
			default -> super.redirectionMultiply(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> value /= defaultValue;
			case GioviNatural v -> value /= v.value.doubleValue();
			case GioviInt v -> value /= v.value.doubleValue();
			case GioviReal v -> value /= v.value.doubleValue();
			default -> super.redirectionDivide(right);
		}
		return this;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult positive() {
		return new GioviReal(value);
	}

	@Override
	public GioviParserVisitResult negative() {
		return new GioviReal(-value);
	}

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviReal(value + defaultValue);
			case GioviNatural v -> new GioviReal(value + v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value + v.unbox());
			case GioviReal v -> new GioviReal(value + v.unbox());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviReal(value - defaultValue);
			case GioviNatural v -> new GioviReal(value - v.unbox().intValue());
			case GioviInt v -> new GioviReal(value - v.unbox());
			case GioviReal v -> new GioviReal(value - v.unbox());
			default -> super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviReal(value * defaultValue);
			case GioviNatural v -> new GioviReal(value * v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value * v.unbox());
			case GioviReal v -> new GioviReal(value * v.unbox());
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviReal(value / defaultValue);
			case GioviNatural v -> new GioviReal(value / v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value / v.unbox());
			case GioviReal v -> new GioviReal(value / v.unbox());
			default -> super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviReal(value % defaultValue);
			case GioviNatural v -> new GioviReal(value % v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value % v.unbox());
			case GioviReal v -> new GioviReal(value % v.unbox());
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviReal(Math.pow(value, defaultValue));
			case GioviNatural v -> new GioviReal(Math.pow(value, v.unbox().doubleValue()));
			case GioviInt v -> new GioviReal(Math.pow(value, v.unbox()));
			case GioviReal v -> new GioviReal(Math.pow(value, v.unbox()));
			default -> super.power(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviNatural v -> new GioviBool(value == v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value == v.unbox().doubleValue());
			case GioviReal v -> new GioviBool(value.equals(v.unbox()));
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
			case GioviDefault v -> new GioviBool(value < defaultValue);
			case GioviNatural v -> new GioviBool(value < v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value < v.unbox());
			case GioviReal v -> new GioviBool(value < v.unbox());
			default -> super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value <= defaultValue);
			case GioviNatural v -> new GioviBool(value <= v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value <= v.unbox());
			case GioviReal v -> new GioviBool(value <= v.unbox());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value > defaultValue);
			case GioviNatural v -> new GioviBool(value > v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value > v.unbox());
			case GioviReal v -> new GioviBool(value > v.unbox());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value >= defaultValue);
			case GioviNatural v -> new GioviBool(value >= v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value >= v.unbox());
			case GioviReal v -> new GioviBool(value >= v.unbox());
			default -> super.greaterEqual(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviReal(defaultValue);
		}
		return super.dot(right);
	}

}
