package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public non-sealed class GioviBool extends GioviValue<Boolean> {

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
	public void box(GioviValue<?> value) {
		switch (value.unbox()) {
			case Boolean v -> box(v);
//			case Integer v -> set(v > 0);
			default -> super.box(value);
		}
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue.unbox()) {
			case Boolean v -> setDefaultValue(v);
//			case Integer v -> setDefaultValue(v > 0);
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public GioviObject not() {
		return new GioviBool(!value);
	}

	@Override
	public GioviObject redirection(GioviObject content) {
		switch (content) {
			case GioviDefault v -> value = defaultValue;
			case GioviBool v -> value = v.unbox();
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviObject and(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value && defaultValue);
			case GioviBool v -> new GioviBool(value && v.unbox());
//			case IntValue v -> new BoolValue(value && v.asBool());
			default -> super.and(right);
		};
	}

	@Override
	public GioviObject or(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value || defaultValue);
			case GioviBool v -> new GioviBool(value || v.unbox());
//			case IntValue v -> new BoolValue(value || v.asBool());
			default -> super.or(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviBool v -> new GioviBool(value.equals(v.unbox()));
//			case IntValue v -> new BoolValue(value == v.asBool());
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}

}
