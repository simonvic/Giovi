package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.GioviObject;

public final class GioviText extends GioviValue<String> {

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
	public GioviType<?> getGioviType() {
		return new GioviTypePrimitive(GioviText.class);
	}

	@Override
	public GioviText copy() {
		return new GioviText(this.value, this.defaultValue);
	}

	@Override
	public GioviValue<?> castTo(GioviType<?> type) {
		if (type instanceof GioviTypePrimitive t) {
			var typeClass = t.unbox();
			if (typeClass == GioviText.class) return new GioviText(this.value);
			if (typeClass == GioviBool.class) return GioviBool.valueOf(this.value);
			try {
				if (typeClass == GioviInt.class) return GioviInt.valueOf(this.value);
				if (typeClass == GioviReal.class) return GioviReal.valueOf(this.value);
				if (typeClass == GioviNatural.class) return GioviNatural.valueOf(this.value);
			} catch (NumberFormatException ex) {
				throw new GioviException(ex);
			}
		}
		return super.castTo(type);
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		switch (gioviValue.unbox()) {
			case String v -> box(v);
			default -> super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue.unbox()) {
			case String v -> setDefaultValue(v);
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public GioviObject redirection(GioviObject content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
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
			//			case GioviText v -> new GioviText(value + v.unbox());
			case GioviValue<?> v -> new GioviText(value + v);
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
