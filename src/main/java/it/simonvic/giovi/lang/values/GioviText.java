package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public non-sealed class GioviText extends GioviValue<String> {

	public GioviText(String value, String defaultValue) {
		super(value, defaultValue);
	}

	public GioviText(String value) {
		super(value, "");
	}

	public GioviText() {
		super("", "");
	}

	@Override
	public void box(GioviValue<?> value) {
		switch (value.unbox()) {
			case String v -> box(v);
			default -> super.box(value);
		}
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch(defaultValue.unbox()) {
			case String v -> setDefaultValue(v);
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public GioviObject redirection(GioviObject content) {
		switch (content) {
			case GioviDefault v -> value = defaultValue;
			case GioviText v -> value = v.unbox();
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviObject redirectionPlus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value += defaultValue;
			case GioviText v -> value += v.unbox();
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMinus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> value = value.replace(defaultValue, "");
			case GioviText v -> value = value.replace(v.unbox(), "");
			default -> super.redirectionMinus(right);
		}
		return this;
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviText(value + defaultValue);
			case GioviText v -> new GioviText(value + v.unbox());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviText(value.replace(defaultValue, ""));
			case GioviText v -> new GioviText(value.replace(v.unbox(), ""));
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) <= 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) <= 0);
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) >= 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) >= 0);
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) > 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) > 0);
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) < 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) < 0);
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) == 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) == 0);
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.contains(defaultValue));
			case GioviText v -> new GioviBool(value.contains(v.unbox()));
			default -> super.contains(right);
		};
	}

}
