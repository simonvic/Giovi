package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviBool(Boolean value, Boolean defaultValue) implements GioviPrimitive<Boolean> {

	public static GioviBool valueOf(String s) {
		return new GioviBool(Boolean.valueOf(s));
	}

	public GioviBool(Boolean value) {
		this(value, false);
	}

	public GioviBool() {
		this(false, false);
	}

	@Override
	public GioviTypeBool getGioviType() {
		return new GioviTypeBool();
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		if (type instanceof GioviTypeBool) {
			return this;
		}
		throw new NonWideneable(this, type);
	}

	@Override
	public GioviBool copy() {
		return new GioviBool(this.value, this.defaultValue);
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
			case GioviDefault _ -> new GioviBool(defaultValue, defaultValue);
			case GioviBool v -> new GioviBool(v.value(), defaultValue);
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
			case GioviDefault _ -> new GioviBool(value && defaultValue);
			case GioviBool v -> new GioviBool(value && v.unbox());
			// case IntValue v -> new BoolValue(value && v.asBool());
			default -> GioviPrimitive.super.and(right);
		};
	}

	@Override
	public GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value || defaultValue);
			case GioviBool v -> new GioviBool(value || v.unbox());
			// case IntValue v -> new BoolValue(value || v.asBool());
			default -> GioviPrimitive.super.or(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.equals(defaultValue));
			case GioviBool v -> new GioviBool(value.equals(v.unbox()));
			// case IntValue v -> new BoolValue(value == v.asBool());
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
			return new GioviBool(defaultValue);
		}
		return GioviPrimitive.super.dot(right);
	}

}
