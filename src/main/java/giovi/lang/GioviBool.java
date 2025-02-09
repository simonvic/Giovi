package giovi.lang;

import giovi.GioviParserVisitResult;

public record GioviBool(Boolean value) implements GioviPrimitive<Boolean> {

	public static GioviBool valueOf(String s) {
		return new GioviBool(Boolean.valueOf(s));
	}

	public GioviBool() {
		this(false);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Boolean defaultValue() {
		return false;
	}

	@Override
	public GioviTypeBool getGioviType() {
		return new GioviTypeBool();
	}

	@Override
	public GioviBool copy() {
		return new GioviBool(this.value);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviTypeBool _ -> new GioviBool(this.value);
			default -> GioviPrimitive.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviBool(defaultValue());
			case GioviBool(var v) -> new GioviBool(v);
			default -> GioviPrimitive.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	@Override
	public GioviParserVisitResult not() {
		return new GioviBool(!value);
	}

	@Override
	public GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value && defaultValue());
			case GioviBool(var v) -> new GioviBool(value && v);
			// case IntValue(var v) -> new BoolValue(value && v.asBool());
			default -> GioviPrimitive.super.and(right);
		};
	}

	@Override
	public GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value || defaultValue());
			case GioviBool(var v) -> new GioviBool(value || v);
			// case IntValue(var v) -> new BoolValue(value || v.asBool());
			default -> GioviPrimitive.super.or(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.equals(defaultValue()));
			case GioviBool(var v) -> new GioviBool(value.equals(v));
			// case IntValue(var v) -> new BoolValue(value == v.asBool());
			default -> GioviPrimitive.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviBool();
		}
		return GioviPrimitive.super.dot(right);
	}

}
