package it.simonvic.giovi.lang.values;

import java.util.ArrayList;
import java.util.stream.Stream;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.AccessKey;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.Range;

public final class GioviText extends GioviPrimitive<String> {

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
	public String toString() {
		return "'" + super.toString() + "'";
	}

	@Override
	public GioviText copy() {
		return new GioviText(this.value, this.defaultValue);
	}

	@Override
	public GioviValue<?> castTo(GioviType<?> type) {
		if (type instanceof GioviTypePrimitive t) {
			var typeClass = t.unbox().getClazz();
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

	//                                                                                OPERATORATION
	//                                                                                  redirection
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

	//                                                                                   arithmetic
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
	public GioviObject divide(GioviObject right) {
		return switch (right) {
			case GioviText v -> new GioviList(new ArrayList<>(
				Stream.of(value.split(v.unbox()))
					.map(this::toGioviText)
					.toList()));
			default -> super.divide(right);
		};
	}

	//                                                                                      boolean
	//                                                                                   relational
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
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) < 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) < 0);
			default -> super.less(right);
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
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) > 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) > 0);
			default -> super.greater(right);
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
	public GioviObject contains(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.contains(defaultValue));
			case GioviText v -> new GioviBool(value.contains(v.unbox()));
			default -> super.contains(right);
		};
	}

	@Override
	public GioviObject access(GioviObject right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey.get()) {
				case "%" -> new GioviInt(value.length());
				case "@" -> Range.of(0, value.length() - 1, 1);
				case "*" -> new GioviList(
					new ArrayList<>(value.chars().boxed().map(this::toGioviChar).toList()));
				default -> throw new GioviException("Invalid access key: " + accessKey);
			};
			case Range<?> r -> {
				if (r.getStart() instanceof Integer start && r.getEnd() instanceof Integer end) {
					yield new GioviText(value.substring(start, end));
				}
				yield super.access(right);
			}
			case GioviInt index -> new GioviText(
				String.valueOf(value.charAt(index.unbox() % value.length())));
			default -> super.access(right);
		};
	}

	GioviText toGioviChar(Integer c) {
		return new GioviText(Character.toString(c));
	}

	GioviText toGioviText(String s) {
		return new GioviText(s);
	}

}
