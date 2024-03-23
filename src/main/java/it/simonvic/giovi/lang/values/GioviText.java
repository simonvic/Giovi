package it.simonvic.giovi.lang.values;

import java.util.ArrayList;
import java.util.stream.Stream;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.AccessKey;
import it.simonvic.giovi.lang.GioviParserVisitResult;
import it.simonvic.giovi.lang.Range;
import it.simonvic.giovi.util.GioviTypes;

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
	public GioviTypeText getGioviType() {
		return new GioviTypeText();
	}

	@Override
	public GioviText copy() {
		return new GioviText(this.value, this.defaultValue);
	}

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (right instanceof GioviTypePrimitive<?> t) {
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
		return super.as(right);
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
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			case GioviText v -> value = v.unbox();
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> value += defaultValue;
			case GioviText v -> value += v.unbox();
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		switch (right) {
			case GioviDefault v -> value = value.replace(defaultValue, "");
			case GioviText v -> value = value.replace(v.unbox(), "");
			default -> super.redirectionMinus(right);
		}
		return this;
	}

	//                                                                                   arithmetic
	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviText(value + defaultValue);
			//			case GioviText v -> new GioviText(value + v.unbox());
			case GioviValue<?> v -> new GioviText(value + v);
			default -> super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviText(value.replace(defaultValue, ""));
			case GioviText v -> new GioviText(value.replace(v.unbox(), ""));
			default -> super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNumber<?> v -> new GioviText(value.repeat(v.intValue().unbox()));
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviText v -> new GioviList(
				GioviTypes.of(GioviText.class),
				new ArrayList<>(Stream.of(value
					.split(v.unbox()))
					.map(this::toGioviText)
					.toList()));
			default -> super.divide(right);
		};
	}

	//                                                                                      boolean
	//                                                                                   relational
	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) == 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) == 0);
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
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) < 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) < 0);
			default -> super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) <= 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) <= 0);
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) > 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) > 0);
			default -> super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.compareTo(defaultValue) >= 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) >= 0);
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.contains(defaultValue));
			case GioviText v -> new GioviBool(value.contains(v.unbox()));
			default -> super.contains(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey.text()) {
				case "%" -> new GioviInt(value.length());
				case "@" -> Range.of(0, value.length() - 1, 1);
				case "*" -> new GioviList(
					GioviTypes.of(GioviText.class),
					new ArrayList<>(value.chars().boxed().map(this::toGioviChar).toList()));
				default -> throw new GioviException("Invalid access key: " + accessKey);
			};
			case Range<?> range -> {
				if (range.getStart() instanceof Integer start
					&& range.getEnd() instanceof Integer end) {
					var sb = new StringBuilder();
					range.iterate(index -> sb.append(this.value.charAt((int) index)));
					yield new GioviText(sb.toString());
				}
				yield super.access(right);
			}
			case GioviInt i -> {
				int index = i.unbox();
				index = index >= 0 ? index : value.length() + index;
				yield new GioviText(String.valueOf(value.charAt(index)));
			}
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
