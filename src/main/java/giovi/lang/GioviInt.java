package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.Natural;

public final class GioviInt extends GioviNumber<Integer> {

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
	public GioviTypeInt getGioviType() {
		return new GioviTypeInt();
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

	//                                                                                OPERATORATION
	//                                                                                  redirection

	// FIXME: we should not automatically cast when redirecting (unless we can widen)

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> value = defaultValue;
			case GioviNumber<?> v -> value = v.intValue().unbox();
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> value += defaultValue;
			case GioviNumber<?> v -> value += v.intValue().unbox();
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> value -= defaultValue;
			case GioviNumber<?> v -> value -= v.intValue().unbox();
			default -> super.redirectionMinus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> value *= defaultValue;
			case GioviNumber<?> v -> value *= v.intValue().unbox();
			default -> super.redirectionMultiply(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> value /= defaultValue;
			case GioviNumber<?> v -> value /= v.intValue().unbox();
			default -> super.redirectionDivide(right);
		}
		return this;
	}

	//                                                                                   arithmetic
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
			case GioviDefault v -> new GioviInt(value + defaultValue);
			case GioviNatural v -> new GioviInt(value + v.unbox().intValue());
			case GioviInt v -> new GioviInt(value + v.unbox());
			case GioviReal v -> new GioviReal(value + v.unbox());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviInt(value - defaultValue);
			case GioviNatural v -> new GioviInt(value - v.unbox().intValue());
			case GioviInt v -> new GioviInt(value - v.unbox());
			case GioviReal v -> new GioviReal(value - v.unbox());
			default -> super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviInt(value * defaultValue);
			case GioviNatural v -> new GioviInt(value * v.unbox().intValue());
			case GioviInt v -> new GioviInt(value * v.unbox());
			case GioviReal v -> new GioviReal(value * v.unbox());
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviInt(value / defaultValue);
			case GioviNatural v -> new GioviReal(value.doubleValue() / v.unbox().doubleValue());
			case GioviInt v -> new GioviReal(value.doubleValue() / v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() / v.unbox());
			default -> super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviInt(value % defaultValue);
			case GioviNatural v -> new GioviInt(value % v.unbox().intValue());
			case GioviInt v -> new GioviInt(value % v.unbox());
			case GioviReal v -> new GioviReal(value.doubleValue() % v.unbox());
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviInt((int) Math.pow(value, defaultValue));
			case GioviNatural v -> new GioviInt((int) Math.pow(value, v.unbox().intValue()));
			case GioviInt v -> new GioviInt((int) Math.pow(value, v.unbox()));
			case GioviReal v -> new GioviReal(Math.pow(value.doubleValue(), v.unbox()));
			default -> super.power(right);
		};
	}


	//                                                                                      boolean

	//                                                                                   relational
	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviNatural v -> new GioviBool(value.equals(v.unbox().intValue()));
			case GioviInt v -> new GioviBool(value.equals(v.unbox()));
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
			case GioviDefault v -> new GioviBool(value < defaultValue);
			case GioviNatural v -> new GioviBool(value < v.unbox().intValue());
			case GioviInt v -> new GioviBool(value < v.unbox());
			case GioviReal v -> new GioviBool(value < v.unbox());
			default -> super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value <= defaultValue);
			case GioviNatural v -> new GioviBool(value <= v.unbox().intValue());
			case GioviInt v -> new GioviBool(value <= v.unbox());
			case GioviReal v -> new GioviBool(value <= v.unbox());
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value > defaultValue);
			case GioviNatural v -> new GioviBool(value > v.unbox().intValue());
			case GioviInt v -> new GioviBool(value > v.unbox());
			case GioviReal v -> new GioviBool(value > v.unbox());
			default -> super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value >= defaultValue);
			case GioviNatural v -> new GioviBool(value >= v.unbox().intValue());
			case GioviInt v -> new GioviBool(value >= v.unbox());
			case GioviReal v -> new GioviBool(value >= v.unbox());
			default -> super.greaterEqual(right);
		};
	}

	//                                                                                       access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviInt(defaultValue);
		}
		return super.dot(right);
	}


}
