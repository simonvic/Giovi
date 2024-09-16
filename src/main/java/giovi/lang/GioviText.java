package giovi.lang;

import java.util.ArrayList;
import java.util.stream.Stream;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.Range;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviText(String value, String defaultValue) implements GioviPrimitive<String> {

	public GioviText(String value) {
		this(value, "");
	}

	public GioviText() {
		this("", "");
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public GioviTypeText getGioviType() {
		return new GioviTypeText();
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		if (type instanceof GioviTypeText) {
			return this;
		}
		throw new NonWideneable(this, type);
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
		return GioviPrimitive.super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviText(defaultValue, defaultValue);
			case GioviText v -> new GioviText(v.value(), defaultValue);
			default -> GioviPrimitive.super.redirection(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviText(value + defaultValue, defaultValue);
			case GioviText v -> new GioviText(value + v.value(), defaultValue);
			default -> GioviPrimitive.super.redirectionPlus(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviText(value.replace(defaultValue, ""), defaultValue);
			case GioviText v -> new GioviText(value.replace(v.unbox(), ""), defaultValue);
			default -> GioviPrimitive.super.redirectionMinus(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviText(value + defaultValue);
			//			case GioviText v -> new GioviText(value + v.unbox());
			case GioviValue<?> v -> new GioviText(value + v);
			default -> GioviPrimitive.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviText(value.replace(defaultValue, ""));
			case GioviText v -> new GioviText(value.replace(v.unbox(), ""));
			default -> GioviPrimitive.super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNumber<?> v -> new GioviText(value.repeat(v.intValue().unbox()));
			default -> GioviPrimitive.super.power(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviText v -> new GioviList(
				new GioviTypeText(),
				new ArrayList<>(Stream.of(value
					.split(v.unbox()))
					.map(this::toGioviText)
					.toList()));
			default -> GioviPrimitive.super.divide(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(defaultValue) == 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) == 0);
			default -> GioviPrimitive.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	@Override
	public GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(defaultValue) < 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) < 0);
			default -> GioviPrimitive.super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(defaultValue) <= 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) <= 0);
			default -> GioviPrimitive.super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(defaultValue) > 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) > 0);
			default -> GioviPrimitive.super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(defaultValue) >= 0);
			case GioviText v -> new GioviBool(value.compareTo(v.unbox()) >= 0);
			default -> GioviPrimitive.super.greaterEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.contains(defaultValue));
			case GioviText v -> new GioviBool(value.contains(v.unbox()));
			default -> GioviPrimitive.super.contains(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviText(defaultValue);
		}
		return GioviPrimitive.super.dot(right);
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value.length());
				case KEYS -> Range.of(0, value.length() - 1, 1);
				case VALUES -> new GioviList(
					new GioviTypeText(),
					new ArrayList<>(value.chars().boxed().map(this::toGioviChar).toList()));
			};
			case Range<?> range when range instanceof Range(Integer start, Integer end, var step, var strategy) -> {
				var sb = new StringBuilder();
				range.iterate(index -> sb.append(this.value.charAt((int) index)));
				yield new GioviText(sb.toString());
			}
			case GioviInt i -> {
				int index = i.unbox();
				index = index >= 0 ? index : value.length() + index;
				yield new GioviText(String.valueOf(value.charAt(index)));
			}
			default -> GioviPrimitive.super.access(right);
		};
	}

	GioviText toGioviChar(Integer c) {
		return new GioviText(Character.toString(c));
	}

	GioviText toGioviText(String s) {
		return new GioviText(s);
	}

}
