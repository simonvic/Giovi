package giovi.lang;

import static it.simonvic.giovi.util.GioviUtils.nativeIndexOf;

import java.util.ArrayList;
import java.util.stream.Stream;

import giovi.GioviParserVisitResult;
import giovi.MemberKey;
import giovi.Range;
import it.simonvic.giovi.Entry;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeText.class)
public record GioviText(String value) implements GioviPrimitive<String> {

	public GioviText() {
		this("");
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public GioviTypeText getGioviType() {
		return GioviType.TEXT;
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
			case GioviText(var v) -> new GioviText(v);
			default -> GioviPrimitive.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			//			case GioviText(var v) -> new GioviText(value + v.unbox());
			case GioviValue<?> v -> new GioviText(value + v);
			default -> GioviPrimitive.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
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
				GioviType.LIST_OF_TEXT,
				new ArrayList<>(
					Stream.of(value.split(v)) // TODO: sanitize v, it should not be treated as regex
						.map(this::toVar)
						.toList()
				)
			);
			default -> GioviPrimitive.super.divide(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviText(var v) -> GioviBool.of(value.compareTo(v) == 0);
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
			case GioviText(var v) -> GioviBool.of(value.compareTo(v) < 0);
			default -> GioviPrimitive.super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviText(var v) -> GioviBool.of(value.compareTo(v) <= 0);
			default -> GioviPrimitive.super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviText(var v) -> GioviBool.of(value.compareTo(v) > 0);
			default -> GioviPrimitive.super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviText(var v) -> GioviBool.of(value.compareTo(v) >= 0);
			default -> GioviPrimitive.super.greaterEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviText(var v) -> GioviBool.of(value.contains(v));
			default -> GioviPrimitive.super.contains(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case MemberKey(var identifier) when identifier.startsWith("_") -> switch (identifier) {
				case "_count" -> new GioviInt(value.length());
				case "_range" -> Range.of(0, value.length() - 1, 1);
				case "_chars" -> new GioviList(
					GioviType.LIST_OF_TEXT,
					new ArrayList<>(value.chars().boxed().map(this::toVarEntry).toList())
				);
				default -> GioviPrimitive.super.access(right);
			};
			case Range<?> range when range instanceof Range(Integer _, _, _) -> {
				var sb = new StringBuilder();
				for (var index : (Range<Integer>) range) {
					sb.append(this.value.charAt(index));
				}
				yield new GioviText(sb.toString());
			}
			case GioviInt(var index)
				-> new GioviText(
					String.valueOf(value.charAt(nativeIndexOf(index, value.length())))
				);
			default -> GioviPrimitive.super.access(right);
		};
	}

	Entry<GioviText> toVarEntry(Integer c) {
		return new Entry<>(new GioviText(Character.toString(c)));
	}

	Entry<GioviText> toVar(String s) {
		return new Entry<>(new GioviText(s));
	}

}
