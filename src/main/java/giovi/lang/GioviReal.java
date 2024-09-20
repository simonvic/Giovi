package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.Natural;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviReal(Double value) implements GioviNumber<Double> {

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

	public GioviReal() {
		this(0d);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Double defaultValue() {
		return 0d;
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
		return new GioviReal(this.value);
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
			case GioviDefault _ -> new GioviReal(defaultValue());
			case GioviNatural(var v) -> new GioviReal(v.doubleValue());
			case GioviInt(var v) -> new GioviReal(v.doubleValue());
			case GioviReal(var v) -> new GioviReal(v.doubleValue());
			default -> GioviNumber.super.redirection(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value + defaultValue());
			case GioviNatural(var v) -> new GioviReal(value + v.doubleValue());
			case GioviInt(var v) -> new GioviReal(value + v.doubleValue());
			case GioviReal(var v) -> new GioviReal(value + v.doubleValue());
			default -> GioviNumber.super.redirectionPlus(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value - defaultValue());
			case GioviNatural(var v) -> new GioviReal(value - v.doubleValue());
			case GioviInt(var v) -> new GioviReal(value - v.doubleValue());
			case GioviReal(var v) -> new GioviReal(value - v.doubleValue());
			default -> GioviNumber.super.redirectionMinus(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value * defaultValue());
			case GioviNatural(var v) -> new GioviReal(value * v.doubleValue());
			case GioviInt(var v) -> new GioviReal(value * v.doubleValue());
			case GioviReal(var v) -> new GioviReal(value * v.doubleValue());
			default -> GioviNumber.super.redirectionMultiply(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value / defaultValue());
			case GioviNatural(var v) -> new GioviReal(value / v.doubleValue());
			case GioviInt(var v) -> new GioviReal(value / v.doubleValue());
			case GioviReal(var v) -> new GioviReal(value / v.doubleValue());
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
			case GioviDefault _ -> new GioviReal(value + defaultValue());
			case GioviNatural(var v) -> new GioviReal(value + v.doubleValue());
			case GioviInt(var v) -> new GioviReal(value + v);
			case GioviReal(var v) -> new GioviReal(value + v);
			default -> GioviNumber.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value - defaultValue());
			case GioviNatural(var v) -> new GioviReal(value - v.intValue());
			case GioviInt(var v) -> new GioviReal(value - v);
			case GioviReal(var v) -> new GioviReal(value - v);
			default -> GioviNumber.super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value * defaultValue());
			case GioviNatural(var v) -> new GioviReal(value * v.doubleValue());
			case GioviInt(var v) -> new GioviReal(value * v);
			case GioviReal(var v) -> new GioviReal(value * v);
			default -> GioviNumber.super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value / defaultValue());
			case GioviNatural(var v) -> new GioviReal(value / v.doubleValue());
			case GioviInt(var v) -> new GioviReal(value / v);
			case GioviReal(var v) -> new GioviReal(value / v);
			default -> GioviNumber.super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(value % defaultValue());
			case GioviNatural(var v) -> new GioviReal(value % v.doubleValue());
			case GioviInt(var v) -> new GioviReal(value % v);
			case GioviReal(var v) -> new GioviReal(value % v);
			default -> GioviNumber.super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviReal(Math.pow(value, defaultValue()));
			case GioviNatural(var v) -> new GioviReal(Math.pow(value, v.doubleValue()));
			case GioviInt(var v) -> new GioviReal(Math.pow(value, v));
			case GioviReal(var v) -> new GioviReal(Math.pow(value, v));
			default -> GioviNumber.super.power(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.equals(defaultValue()));
			case GioviNatural(var v) -> new GioviBool(value == v.doubleValue());
			case GioviInt(var v) -> new GioviBool(value == v.doubleValue());
			case GioviReal(var v) -> new GioviBool(value.equals(v));
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
			case GioviDefault _ -> new GioviBool(value < defaultValue());
			case GioviNatural(var v) -> new GioviBool(value < v.doubleValue());
			case GioviInt(var v) -> new GioviBool(value < v);
			case GioviReal(var v) -> new GioviBool(value < v);
			default -> GioviNumber.super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value <= defaultValue());
			case GioviNatural(var v) -> new GioviBool(value <= v.doubleValue());
			case GioviInt(var v) -> new GioviBool(value <= v);
			case GioviReal(var v) -> new GioviBool(value <= v);
			default -> GioviNumber.super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value > defaultValue());
			case GioviNatural(var v) -> new GioviBool(value > v.doubleValue());
			case GioviInt(var v) -> new GioviBool(value > v);
			case GioviReal(var v) -> new GioviBool(value > v);
			default -> GioviNumber.super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value >= defaultValue());
			case GioviNatural(var v) -> new GioviBool(value >= v.doubleValue());
			case GioviInt(var v) -> new GioviBool(value >= v);
			case GioviReal(var v) -> new GioviBool(value >= v);
			default -> GioviNumber.super.greaterEqual(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviReal(defaultValue());
		}
		return GioviNumber.super.dot(right);
	}

}
