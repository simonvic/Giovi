package giovi.lang;

import java.util.LinkedHashMap;

import giovi.*;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviTuples;

// TODO: change back to classes so we can (ab)use inheritance to avoid reimplementing operators
public record GioviStruct(Struct signature, Struct value) implements GioviValue<Struct> {

	public GioviStruct(Struct value) {
		this(signatureFrom(value), value);
	}

	private static Struct signatureFrom(Struct value) {
		var fields = new LinkedHashMap<String, VarD<?>>();
		value.forEach((n, field) -> fields.put(
			n,
			new VarD<>(field.defaultValue().copy(), field.defaultValue().copy())));
		return new Struct(value.getTypename(), fields);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public GioviTypeStruct getGioviType() {
		return new GioviTypeStruct(signature);
	}

	@Override
	public Struct defaultValue() {
		return signature.copy();
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		try {
			return switch (type) {
				case GioviTypeStruct(var definition) -> new GioviStruct(
					this.value.widenTo(definition));
				default -> throw new NonWideneable(this, type);
			};
		} catch (NonWideneable ex) {
			throw new NonWideneable(this, type, ex);
		}
	}

	@Override
	public GioviStruct copy() {
		return new GioviStruct(value.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			// TODO: can we assume equal typename means equal definition?
			case GioviTypeStruct t when t.getTypename().equals(value.getTypename()) -> this;
			default -> GioviValue.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviTupleNamed(defaultValue().copy());
			case GioviTuple v -> GioviTuples.redirection(this, v);
			case GioviTupleNamed v -> GioviTuples.redirection(this, v);
			case GioviStruct v -> GioviTuples.redirection(this, v);
			default -> GioviValue.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviBool(value.equal(defaultValue()));
			case GioviStruct(_, var v) -> new GioviBool(this.value.equal(v));
			case GioviTupleNamed(_, var v) -> new GioviBool(this.value.equal(v));
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
			case GioviDefault _ -> new GioviStruct(defaultValue().copy());
			case MemberKey(var identifier) when value.containsField(identifier) -> value
				.getField(identifier).get();
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
			case Range(Integer start, Integer end, Integer step, _) -> new GioviTuple(
				this.value.copy(Range.of(start, end, step)));
			case GioviInt(var index) -> value
				.getField(index >= 0 ? index : value.getFieldsCount() + index)
				.orElseThrow(() -> new NoSuchField(index));
			default -> GioviValue.super.access(right);
		};
	}


}

