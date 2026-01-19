package giovi.lang;

import java.util.ArrayList;
import java.util.stream.Stream;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.Range;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeText.class)
public record GioviText(String value) implements GioviPrimitive<String> {

	public static final GioviTypeText TYPE = new GioviTypeText();

	public GioviText() {
		this("");
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public String zero() {
		return "";
	}

	@Override
	public GioviTypeText getGioviType() {
		return GioviText.TYPE;
	}

	@Override
	public GioviText copy() {
		return new GioviText(this.value);
	}

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviTypeText _ -> new GioviText(this.value);
			case GioviTypeBool _ -> GioviBool.valueOf(this.value);
			case GioviTypeNatural _ -> GioviNatural.valueOf(this.value);
			case GioviTypeInt _ -> GioviInt.valueOf(this.value);
			case GioviTypeReal _ -> GioviReal.valueOf(this.value);
			default -> GioviPrimitive.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviText(zero());
			case GioviText(var v) -> new GioviText(v);
			default -> GioviPrimitive.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviText(value + zero());
			//			case GioviText(var v) -> new GioviText(value + v.unbox());
			case GioviValue<?> v -> new GioviText(value + v);
			default -> GioviPrimitive.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviText(value.replace(zero(), ""));
			case GioviText(var v) -> new GioviText(value.replace(v, ""));
			default -> GioviPrimitive.super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviNumber<?> v -> new GioviText(value.repeat(v.intValue().value()));
			default -> GioviPrimitive.super.power(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviText(var v) -> new GioviList(
				GioviText.TYPE,
				new ArrayList<>(Stream.of(value
					.split(v)) // TODO: sanitize v, it should not be treated as regex
					.map(this::toVar)
					.toList()));
			default -> GioviPrimitive.super.divide(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(zero()) == 0);
			case GioviText(var v) -> new GioviBool(value.compareTo(v) == 0);
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
			case GioviDefault _ -> new GioviBool(value.compareTo(zero()) < 0);
			case GioviText(var v) -> new GioviBool(value.compareTo(v) < 0);
			default -> GioviPrimitive.super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(zero()) <= 0);
			case GioviText(var v) -> new GioviBool(value.compareTo(v) <= 0);
			default -> GioviPrimitive.super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(zero()) > 0);
			case GioviText(var v) -> new GioviBool(value.compareTo(v) > 0);
			default -> GioviPrimitive.super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.compareTo(zero()) >= 0);
			case GioviText(var v) -> new GioviBool(value.compareTo(v) >= 0);
			default -> GioviPrimitive.super.greaterEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.contains(zero()));
			case GioviText(var v) -> new GioviBool(value.contains(v));
			default -> GioviPrimitive.super.contains(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviText();
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
					GioviText.TYPE,
					new ArrayList<>(value.chars().boxed().map(this::toVar).toList()));
			};
			case Range<?> range when range instanceof Range(Integer start, Integer end, _, _) -> {
				var sb = new StringBuilder();
				range.iterate(index -> sb.append(this.value.charAt((int) index)));
				yield new GioviText(sb.toString());
			}
			case GioviInt i -> {
				int index = i.value();
				index = index >= 0 ? index : value.length() + index;
				yield new GioviText(String.valueOf(value.charAt(index)));
			}
			default -> GioviPrimitive.super.access(right);
		};
	}

	Var<GioviText> toVar(Integer c) {
		return new Var<>(new GioviText(Character.toString(c)));
	}

	Var<GioviText> toVar(String s) {
		return new Var<>(new GioviText(s));
	}

}
