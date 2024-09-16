package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.Natural;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviReal(Double value, Double defaultValue) implements GioviNumber<Double> {

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

	public GioviReal(Double value) {
		this(value, 0d);
	}

	public GioviReal() {
		this(0d, 0d);
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
		return switch (content) {
			case GioviDefault _ -> new GioviReal(defaultValue, defaultValue);
			case GioviNatural v -> new GioviReal(v.value().doubleValue(), defaultValue);
			case GioviInt v -> new GioviReal(v.value().doubleValue(), defaultValue);
			case GioviReal v -> new GioviReal(v.value().doubleValue(), defaultValue);
			default -> GioviNumber.super.redirection(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value + defaultValue, defaultValue);
			case GioviNatural v -> new GioviReal(value + v.value().doubleValue(), defaultValue);
			case GioviInt v -> new GioviReal(value + v.value().doubleValue(), defaultValue);
			case GioviReal v -> new GioviReal(value + v.value().doubleValue(), defaultValue);
			default -> GioviNumber.super.redirectionPlus(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value - defaultValue, defaultValue);
			case GioviNatural v -> new GioviReal(value - v.value().doubleValue(), defaultValue);
			case GioviInt v -> new GioviReal(value - v.value().doubleValue(), defaultValue);
			case GioviReal v -> new GioviReal(value - v.value().doubleValue(), defaultValue);
			default -> GioviNumber.super.redirectionMinus(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value * defaultValue, defaultValue);
			case GioviNatural v -> new GioviReal(value * v.value().doubleValue(), defaultValue);
			case GioviInt v -> new GioviReal(value * v.value().doubleValue(), defaultValue);
			case GioviReal v -> new GioviReal(value * v.value().doubleValue(), defaultValue);
			default -> GioviNumber.super.redirectionMultiply(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value / defaultValue, defaultValue);
			case GioviNatural v -> new GioviReal(value / v.value().doubleValue(), defaultValue);
			case GioviInt v -> new GioviReal(value / v.value().doubleValue(), defaultValue);
			case GioviReal v -> new GioviReal(value / v.value().doubleValue(), defaultValue);
			default -> GioviNumber.super.redirectionDivide(right);
		};
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
			case GioviDefault _ -> new GioviReal(value + defaultValue);
			case GioviNatural v -> new GioviReal(value + v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value + v.unbox());
			case GioviReal v -> new GioviReal(value + v.unbox());
			default -> GioviNumber.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value - defaultValue);
			case GioviNatural v -> new GioviReal(value - v.unbox().intValue());
			case GioviInt v -> new GioviReal(value - v.unbox());
			case GioviReal v -> new GioviReal(value - v.unbox());
			default -> GioviNumber.super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value * defaultValue);
			case GioviNatural v -> new GioviReal(value * v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value * v.unbox());
			case GioviReal v -> new GioviReal(value * v.unbox());
			default -> GioviNumber.super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value / defaultValue);
			case GioviNatural v -> new GioviReal(value / v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value / v.unbox());
			case GioviReal v -> new GioviReal(value / v.unbox());
			default -> GioviNumber.super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value % defaultValue);
			case GioviNatural v -> new GioviReal(value % v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value % v.unbox());
			case GioviReal v -> new GioviReal(value % v.unbox());
			default -> GioviNumber.super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(Math.pow(value, defaultValue));
			case GioviNatural v -> new GioviReal(Math.pow(value, v.unbox().doubleValue()));
			case GioviInt v -> new GioviReal(Math.pow(value, v.unbox()));
			case GioviReal v -> new GioviReal(Math.pow(value, v.unbox()));
			default -> GioviNumber.super.power(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.equals(defaultValue));
			case GioviNatural v -> new GioviBool(value == v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value == v.unbox().doubleValue());
			case GioviReal v -> new GioviBool(value.equals(v.unbox()));
			default -> GioviNumber.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	@Override
	public GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value < defaultValue);
			case GioviNatural v -> new GioviBool(value < v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value < v.unbox());
			case GioviReal v -> new GioviBool(value < v.unbox());
			default -> GioviNumber.super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value <= defaultValue);
			case GioviNatural v -> new GioviBool(value <= v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value <= v.unbox());
			case GioviReal v -> new GioviBool(value <= v.unbox());
			default -> GioviNumber.super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value > defaultValue);
			case GioviNatural v -> new GioviBool(value > v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value > v.unbox());
			case GioviReal v -> new GioviBool(value > v.unbox());
			default -> GioviNumber.super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value >= defaultValue);
			case GioviNatural v -> new GioviBool(value >= v.unbox().doubleValue());
			case GioviInt v -> new GioviBool(value >= v.unbox());
			case GioviReal v -> new GioviBool(value >= v.unbox());
			default -> GioviNumber.super.greaterEqual(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviReal(defaultValue);
		}
		return GioviNumber.super.dot(right);
	}

}
