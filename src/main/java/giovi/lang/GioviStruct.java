package giovi.lang;

import giovi.*;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviStructs;

@DescribedBy(GioviTypeStruct.class)
public record GioviStruct(GioviTypeStruct type, Struct value)
	implements GioviValue<Struct> {

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public GioviTypeStruct getGioviType() {
		return type;
	}

	@Override
	public Struct zero() {
		return type.spawn().value();
	}

	@Override
	public GioviStruct copy() {
		return new GioviStruct(type, value.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviTypeStruct t when t.equals(this.type) -> this;
			default -> GioviValue.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _,GioviSelf _ -> type.spawn();
			case GioviTuple v -> GioviStructs.redirection(this, v);
			case GioviRecord v -> GioviStructs.redirection(this, v);
			case GioviStruct v -> GioviStructs.redirection(this, v);
			default -> GioviValue.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.equal(zero()));
			case GioviStruct(_, var v) -> new GioviBool(this.value.equal(v));
			case GioviRecord(_, var v) -> new GioviBool(this.value.equal(v));
			case GioviTuple(_, var v) -> new GioviBool(this.value.equal(v));
			default -> GioviValue.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case MemberKey(var identifier) when value.containsField(identifier) -> {
				var fieldVar = value.getField(identifier).orElseThrow();
				if (fieldVar.unwrap() instanceof GioviSelf) {
					// init field
					fieldVar.setValueUnsafe(type.spawn());
				}
				yield fieldVar;
			}
			case MemberKey(var identifier) when type.signature().containsConstant(identifier)
				-> type.signature().getConstant(identifier);
			case MemberKey(var identifier) when type.signature().containsFunction(identifier)
				-> type.signature().getFunction(identifier);
			default -> GioviValue.super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value.getFieldsCount());
				case KEYS -> Range.of(0, value.getFieldsCount() - 1, 1);
				case VALUES -> GioviLists.of(value.getFieldNames());
			};
			case Range(Integer _, _, _, _)
				-> new GioviRecord(
					new GioviTypeRecord(this.type.signature().copy((Range<Integer>) right)),
					this.value.copy((Range<Integer>) right)
				);
			case GioviInt(var index)
				-> value.getField(index >= 0 ? index : value.getFieldsCount() + index)
					.orElseThrow(() -> new NoSuchField(index));
			default -> GioviValue.super.access(right);
		};
	}


}

