package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviParserVisitResult;

public final class GioviBool extends GioviPrimitive<Boolean> {

	public static GioviBool valueOf(String s) {
		return new GioviBool(Boolean.valueOf(s));
	}

	public GioviBool(Boolean value, Boolean defaultValue) {
		super(value, defaultValue);
	}

	public GioviBool(Boolean value) {
		super(value, false);
	}

	public GioviBool() {
		super(false, false);
	}

	@Override
	public GioviBool copy() {
		return new GioviBool(this.value, this.defaultValue);
	}

	@Override
	public GioviValue<?> castTo(GioviType<?> type) {
		if (type instanceof GioviTypePrimitive t) {
			if (t.unbox().getClazz() == GioviBool.class) {
				return new GioviBool(this.value);
			}
			// if (t.unbox() == GioviInt.class) {
			// 	return new GioviInt(value ? 1 : 0);
			// }
		}
		return super.castTo(type);
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		switch (gioviValue.unbox()) {
			case Boolean v -> box(v);
			// case Integer v -> set(v > 0);
			default -> super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue.unbox()) {
			case Boolean v -> setDefaultValue(v);
			// case Integer v -> setDefaultValue(v > 0);
			default -> super.setDefaultValue(defaultValue);
		}
	}

	//                                                                                OPERATORATION
	//                                                                                  redirection
	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			case GioviBool v -> value = v.unbox();
			default -> super.redirection(content);
		}
		return this;
	}

	//                                                                                   arithmetic
	//                                                                                      boolean
	@Override
	public GioviParserVisitResult not() {
		return new GioviBool(!value);
	}

	@Override
	public GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value && defaultValue);
			case GioviBool v -> new GioviBool(value && v.unbox());
			// case IntValue v -> new BoolValue(value && v.asBool());
			default -> super.and(right);
		};
	}

	@Override
	public GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value || defaultValue);
			case GioviBool v -> new GioviBool(value || v.unbox());
			// case IntValue v -> new BoolValue(value || v.asBool());
			default -> super.or(right);
		};
	}

	//                                                                                   relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviBool v -> new GioviBool(value.equals(v.unbox()));
			// case IntValue v -> new BoolValue(value == v.asBool());
			default -> super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	//                                                                                       access


}
