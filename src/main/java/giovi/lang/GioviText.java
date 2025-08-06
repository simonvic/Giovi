package giovi.lang;

import java.util.ArrayList;
import java.util.stream.Stream;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.Range;
import it.simonvic.giovi.Op.*;
import it.simonvic.giovi.Var;

public record GioviText(String value)
	implements GioviPrimitive<String>,
	As, Redirection, RedirectionPlus, RedirectionMinus, Plus, Minus, Power,
	Divide, Equal, NotEqual,
	Less, LessEqual, Greater, GreaterEqual, Contains, IsContained, Dot, Access {

	public GioviText() {
		this("");
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public String defaultValue() {
		return "";
	}

	@Override
	public GioviTypeText getGioviType() {
		return new GioviTypeText();
	}

	@Override
	public GioviText copy() {
		return new GioviText(this.value);
	}

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviTypeText _ -> new GioviText(this.value());
			case GioviTypeBool _ -> GioviBool.valueOf(this.value);
			case GioviTypeNumber<?> number -> switch (number) {
				case GioviTypeNatural _ -> GioviNatural.valueOf(this.value);
				case GioviTypeInt _ -> GioviInt.valueOf(this.value);
				case GioviTypeReal _ -> GioviReal.valueOf(this.value);
			};
			default -> As.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviText(defaultValue());
			case GioviText(var v) -> new GioviText(v);
			default -> Redirection.super.redirection(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviText(value + defaultValue());
			case GioviText(var v) -> new GioviText(value + v);
			default -> RedirectionPlus.super.redirectionPlus(right);
		};
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviText(value.replace(defaultValue(), ""));
			case GioviText(var v) -> new GioviText(value.replace(v, ""));
			default -> RedirectionMinus.super.redirectionMinus(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviText(value + defaultValue());
			//			case GioviText(var v) -> new GioviText(value + v.unbox());
			case GioviValue<?> v -> new GioviText(value + v);
			default -> Plus.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviText(value.replace(defaultValue(), ""));
			case GioviText(var v) -> new GioviText(value.replace(v, ""));
			default -> Minus.super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNumber<?> v -> new GioviText(value.repeat(v.intValue().value()));
			default -> Power.super.power(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviText(var v) -> new GioviList(
				new GioviTypeText(),
				new ArrayList<>(Stream.of(value
					.split(v)) // TODO: sanitize v, it should not be treated as regex
					.map(this::toVar)
					.toList()));
			default -> Divide.super.divide(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviBool equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(defaultValue()) == 0);
			case GioviText(var v) -> new GioviBool(value.compareTo(v) == 0);
			default -> Equal.super.equal(right);
		};
	}

	@Override
	public GioviBool notEqual(GioviParserVisitResult right) {
		return new GioviBool(!equal(right).value());
	}

	@Override
	public GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(defaultValue()) < 0);
			case GioviText(var v) -> new GioviBool(value.compareTo(v) < 0);
			default -> Less.super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(defaultValue()) <= 0);
			case GioviText(var v) -> new GioviBool(value.compareTo(v) <= 0);
			default -> LessEqual.super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(defaultValue()) > 0);
			case GioviText(var v) -> new GioviBool(value.compareTo(v) > 0);
			default -> Greater.super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(defaultValue()) >= 0);
			case GioviText(var v) -> new GioviBool(value.compareTo(v) >= 0);
			default -> GreaterEqual.super.greaterEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.contains(defaultValue()));
			case GioviText(var v) -> new GioviBool(value.contains(v));
			default -> Contains.super.contains(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviText();
		}
		return Dot.super.dot(right);
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value.length());
				case KEYS -> Range.of(0, value.length() - 1, 1);
				case VALUES -> new GioviList(
					new GioviTypeText(),
					new ArrayList<>(value.chars().boxed().map(this::toVar).toList()));
			};
			case Range<?> range when range instanceof Range(Integer _, _, _, _) -> {
				var sb = new StringBuilder();
				range.iterate(index -> sb.append(this.value.charAt((int) index)));
				yield new GioviText(sb.toString());
			}
			case GioviInt i -> {
				int index = i.value();
				index = index >= 0 ? index : value.length() + index;
				yield new GioviText(String.valueOf(value.charAt(index)));
			}
			default -> Access.super.access(right);
		};
	}

	Var<GioviText> toVar(Integer c) {
		return new Var<>(new GioviText(Character.toString(c)));
	}

	Var<GioviText> toVar(String s) {
		return new Var<>(new GioviText(s));
	}

}
