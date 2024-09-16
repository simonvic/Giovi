package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.Natural;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviInt(Integer value, Integer defaultValue) implements GioviNumber<Integer> {

	public static GioviInt valueOf(String s) throws NumberFormatException {
		s = s.replace("i", "") // i suffix
			.replace("_", ""); // separators
		if (s.startsWith("0b")) {
			return new GioviInt(Integer.parseInt(s.replace("0b", ""), 2));
		}
		if (s.startsWith("0o")) {
			return new GioviInt(Integer.parseInt(s.replace("0o", ""), 8));
		}
		return new GioviInt(Integer.decode(s));
	}

	public GioviInt(Integer value) {
		this(value, 0);
	}

	public GioviInt() {
		this(0, 0);
	}

	@Override
	public GioviTypeInt getGioviType() {
		return new GioviTypeInt();
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		return switch (type) {
			case GioviTypeInt t -> this;
			case GioviTypeReal t -> this.realValue();
			default -> throw new NonWideneable(this, type);
		};
	}

	@Override
	public GioviInt copy() {
		return new GioviInt(this.value, this.defaultValue);
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

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviInt(defaultValue, defaultValue);
			case GioviNatural v -> new GioviInt(v.value().intValue(), defaultValue);
			case GioviInt v -> new GioviInt(v.value(), defaultValue);
			default -> GioviNumber.super.redirection(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviInt(value + defaultValue, defaultValue);
			case GioviNatural v -> new GioviInt(value + v.value().intValue(), defaultValue);
			case GioviInt v -> new GioviInt(value + v.value(), defaultValue);
			default -> GioviNumber.super.redirectionPlus(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviInt(value - defaultValue, defaultValue);
			case GioviNatural v -> new GioviInt(value - v.value().intValue(), defaultValue);
			case GioviInt v -> new GioviInt(value - v.value(), defaultValue);
			default -> GioviNumber.super.redirectionMinus(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviInt(value * defaultValue, defaultValue);
			case GioviNatural v -> new GioviInt(value * v.value().intValue(), defaultValue);
			case GioviInt v -> new GioviInt(value * v.value(), defaultValue);
			default -> GioviNumber.super.redirectionMultiply(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviInt(value / defaultValue, defaultValue);
			case GioviNatural v -> new GioviInt(value / v.value().intValue(), defaultValue);
			case GioviInt v -> new GioviInt(value / v.value(), defaultValue);
			default -> GioviNumber.super.redirectionDivide(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult positive() {
		return new GioviInt(value);
	}

	@Override
	public GioviParserVisitResult negative() {
		return new GioviInt(-value);
	}

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviInt(value + defaultValue);
			case GioviNatural v -> new GioviInt(value + v.value().intValue());
			case GioviInt v -> new GioviInt(value + v.value());
			case GioviReal v -> new GioviReal(value + v.value());
			default -> GioviNumber.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviInt(value - defaultValue);
			case GioviNatural v -> new GioviInt(value - v.value().intValue());
			case GioviInt v -> new GioviInt(value - v.value());
			case GioviReal v -> new GioviReal(value - v.value());
			default -> GioviNumber.super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviInt(value * defaultValue);
			case GioviNatural v -> new GioviInt(value * v.value().intValue());
			case GioviInt v -> new GioviInt(value * v.value());
			case GioviReal v -> new GioviReal(value * v.value());
			default -> GioviNumber.super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviInt(value / defaultValue);
			case GioviNatural v -> new GioviReal(value.doubleValue() / v.value().doubleValue());
			case GioviInt v -> new GioviReal(value.doubleValue() / v.value());
			case GioviReal v -> new GioviReal(value.doubleValue() / v.value());
			default -> GioviNumber.super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviInt(value % defaultValue);
			case GioviNatural v -> new GioviInt(value % v.value().intValue());
			case GioviInt v -> new GioviInt(value % v.value());
			case GioviReal v -> new GioviReal(value.doubleValue() % v.value());
			default -> GioviNumber.super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviInt((int) Math.pow(value, defaultValue));
			case GioviNatural v -> new GioviInt((int) Math.pow(value, v.value().intValue()));
			case GioviInt v -> new GioviInt((int) Math.pow(value, v.value()));
			case GioviReal v -> new GioviReal(Math.pow(value.doubleValue(), v.value()));
			default -> GioviNumber.super.power(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.equals(defaultValue));
			case GioviNatural v -> new GioviBool(value.equals(v.value().intValue()));
			case GioviInt v -> new GioviBool(value.equals(v.value()));
			case GioviReal v -> new GioviBool(value.doubleValue() == v.value());
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
			case GioviNatural v -> new GioviBool(value < v.value().intValue());
			case GioviInt v -> new GioviBool(value < v.value());
			case GioviReal v -> new GioviBool(value < v.value());
			default -> GioviNumber.super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value <= defaultValue);
			case GioviNatural v -> new GioviBool(value <= v.value().intValue());
			case GioviInt v -> new GioviBool(value <= v.value());
			case GioviReal v -> new GioviBool(value <= v.value());
			default -> GioviNumber.super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value > defaultValue);
			case GioviNatural v -> new GioviBool(value > v.value().intValue());
			case GioviInt v -> new GioviBool(value > v.value());
			case GioviReal v -> new GioviBool(value > v.value());
			default -> GioviNumber.super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value >= defaultValue);
			case GioviNatural v -> new GioviBool(value >= v.value().intValue());
			case GioviInt v -> new GioviBool(value >= v.value());
			case GioviReal v -> new GioviBool(value >= v.value());
			default -> GioviNumber.super.greaterEqual(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviInt(defaultValue);
		}
		return GioviNumber.super.dot(right);
	}

}
