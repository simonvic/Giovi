package giovi.lang;

import static it.simonvic.giovi.util.GioviUtils.nativeIndexOf;

import giovi.*;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviStructs;

@DescribedBy(GioviTypeStruct.class)
public record GioviStruct(GioviTypeStruct type, GStruct value)
	implements GioviProduct<GStruct> {

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public GioviTypeStruct getGioviType() {
		return type;
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
			default -> GioviProduct.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviSelf _ -> type.spawn(); // TODO: what???
			case GioviTuple v -> GioviStructs.redirection(this, v);
			case GioviRecord v -> GioviStructs.redirection(this, v);
			case GioviStruct v -> GioviStructs.redirection(this, v);
			default -> GioviProduct.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviStruct(_, var v) -> GioviBool.of(this.value.equal(v));
			case GioviRecord(_, var v) -> GioviBool.of(this.value.equal(v));
			case GioviTuple(_, var v) -> GioviBool.of(this.value.equal(v));
			default -> GioviProduct.super.equal(right);
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
			default -> GioviProduct.super.dot(right);
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
				-> value.getField(nativeIndexOf(index, value.getFieldsCount()))
					.orElseThrow(() -> new NoSuchField(index));
			default -> GioviProduct.super.access(right);
		};
	}

	@Override
	public GioviParserVisitResult dotRef(GioviParserVisitResult right) {
		return switch (right) {
			case MemberKey(var identifier) when type.signature().containsFunction(identifier)
				-> type.signature().getFunction(identifier);
			default -> GioviProduct.super.dotRef(right);
		};
	}


}

