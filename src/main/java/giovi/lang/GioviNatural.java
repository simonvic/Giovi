package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.Natural;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeNatural.class)
public record GioviNatural(Natural value) implements GioviNumber<Natural> {

	public static GioviNatural valueOf(String s) throws NumberFormatException {
		return new GioviNatural(Natural.valueOf(s));
	}

	public GioviNatural() {
		this(new Natural());
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public GioviTypeNatural getGioviType() {
		return GioviType.NAT;
	}

	@Override
	public GioviNatural copy() {
		return new GioviNatural(new Natural(this.value.intValue()));
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
		return switch (content) {
			case GioviNatural(var v) -> new GioviNatural(v);
			default -> GioviNumber.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult positive() {
		return new GioviNatural(value);
	}

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNatural(var v)
				-> new GioviNatural(new Natural(value.intValue() + v.intValue()));
			case GioviInt(var v) -> new GioviInt(value.intValue() + v);
			case GioviReal(var v) -> new GioviReal(value.doubleValue() + v);
			default -> GioviNumber.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			// TODO: check if result is still Natural or Int (same for redirMinus)
			case GioviNatural(var v)
				-> new GioviNatural(new Natural(value.intValue() - v.intValue()));
			case GioviInt(var v) -> new GioviInt(value.intValue() - v);
			case GioviReal(var v) -> new GioviReal(value.doubleValue() - v);
			default -> GioviNumber.super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNatural(var v)
				-> new GioviNatural(new Natural(value.intValue() * v.intValue()));
			case GioviInt(var v) -> new GioviInt(value.intValue() * v);
			case GioviReal(var v) -> new GioviReal(value.doubleValue() * v);
			default -> GioviNumber.super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNatural(var v) -> new GioviReal(value.doubleValue() / v.doubleValue());
			case GioviInt(var v) -> new GioviReal(value.doubleValue() / v);
			case GioviReal(var v) -> new GioviReal(value.doubleValue() / v);
			default -> GioviNumber.super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNatural(var v)
				-> new GioviNatural(new Natural(value.intValue() % v.intValue()));
			case GioviInt(var v) -> new GioviInt(value.intValue() % v);
			case GioviReal(var v) -> new GioviReal(value.doubleValue() % v);
			default -> GioviNumber.super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNatural(var v)
				-> new GioviNatural(new Natural((int) Math.pow(value.intValue(), v.intValue())));
			case GioviInt(var v) -> new GioviInt((int) Math.pow(value.intValue(), v));
			case GioviReal(var v) -> new GioviReal(Math.pow(value.doubleValue(), v));
			default -> GioviNumber.super.power(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNatural(var v) -> GioviBool.of(value.intValue() == v.intValue());
			case GioviInt(var v) -> GioviBool.of(value.intValue() == v);
			case GioviReal(var v) -> GioviBool.of(value.doubleValue() == v);
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
			case GioviNatural(var v) -> GioviBool.of(value.intValue() < v.intValue());
			case GioviInt(var v) -> GioviBool.of(value.intValue() < v);
			case GioviReal(var v) -> GioviBool.of(value.doubleValue() < v);
			default -> GioviNumber.super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNatural(var v) -> GioviBool.of(value.intValue() <= v.intValue());
			case GioviInt(var v) -> GioviBool.of(value.intValue() <= v);
			case GioviReal(var v) -> GioviBool.of(value.doubleValue() <= v);
			default -> GioviNumber.super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNatural(var v) -> GioviBool.of(value.intValue() > v.intValue());
			case GioviInt(var v) -> GioviBool.of(value.intValue() > v);
			case GioviReal(var v) -> GioviBool.of(value.doubleValue() > v);
			default -> GioviNumber.super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNatural(var v) -> GioviBool.of(value.intValue() >= v.intValue());
			case GioviInt(var v) -> GioviBool.of(value.intValue() >= v);
			case GioviReal(var v) -> GioviBool.of(value.doubleValue() >= v);
			default -> GioviNumber.super.greaterEqual(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

}
