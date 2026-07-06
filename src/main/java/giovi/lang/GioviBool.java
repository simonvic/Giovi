package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeBool.class)
public record GioviBool(Boolean value) implements GioviPrimitive<Boolean> {

	public static final GioviBool FALSE = new GioviBool(false);
	public static final GioviBool TRUE = new GioviBool(true);

	public static GioviBool of(boolean b) {
		return b ? TRUE : FALSE;
	}

	public static GioviBool valueOf(String s) {
		return GioviBool.of(Boolean.valueOf(s));
	}

	public GioviBool() {
		this(false);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public GioviTypeBool getGioviType() {
		return GioviType.BOOL;
	}

	@Override
	public GioviBool copy() {
		return GioviBool.of(this.value);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviTypeBool _ -> GioviBool.of(this.value);
			default -> GioviPrimitive.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviBool(var v) -> GioviBool.of(v);
			default -> GioviPrimitive.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	@Override
	public GioviParserVisitResult not() {
		return GioviBool.of(!value);
	}

	@Override
	public GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case GioviBool(var v) -> GioviBool.of(value && v);
			// case IntValue(var v) -> new BoolValue(value && v.asBool());
			default -> GioviPrimitive.super.and(right);
		};
	}

	@Override
	public GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case GioviBool(var v) -> GioviBool.of(value || v);
			// case IntValue(var v) -> new BoolValue(value || v.asBool());
			default -> GioviPrimitive.super.or(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviBool(var v) -> GioviBool.of(value.equals(v));
			// case IntValue(var v) -> new BoolValue(value == v.asBool());
			default -> GioviPrimitive.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

}
