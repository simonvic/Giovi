package giovi.lang;

import java.util.LinkedHashMap;

import giovi.*;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviTuples;

public record GioviTupleNamed(TupleNamed signature, TupleNamed value)
	implements GioviValue<TupleNamed> {

	// TODO: find a way to avoid casting everytime, without having to maintain a double reference

	public GioviTupleNamed(TupleNamed value) {
		this(signatureFrom(value), value);
	}

	private static TupleNamed signatureFrom(TupleNamed value) {
		var fields = new LinkedHashMap<String, VarD<?>>();
		value.forEach((n, field) -> fields.put(
			n,
			new VarD<>(field.defaultValue().copy(), field.defaultValue().copy())));
		return new TupleNamed(fields);
	}

	public GioviTupleNamed() {
		this(new TupleNamed(), new TupleNamed());
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public TupleNamed defaultValue() {
		return value.copy();
	}

	@Override
	public GioviTypeTupleNamed getGioviType() {
		return new GioviTypeTupleNamed(signature);
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		try {
			return switch (type) {
				case GioviTypeStruct(var definition) -> new GioviStruct(
					this.value.widenTo(definition));
				case GioviTypeTupleNamed(var definition) -> new GioviTupleNamed(
					this.value.widenTo(definition));
				default -> throw new NonWideneable(this, type);
			};
		} catch (NonWideneable ex) {
			throw new NonWideneable(this, type, ex);
		}
	}

	@Override
	public GioviTupleNamed copy() {
		return new GioviTupleNamed(value.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

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

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviTupleNamed();
			case FieldKey(var identifier) -> value.getField(identifier)
				.orElseThrow(() -> new NoSuchField(identifier));
			default -> GioviValue.super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value.getFieldsCount());
				case KEYS -> Range.of(0, value.getFieldsCount() - 1, 1);
				case VALUES -> GioviLists.of(((TupleNamed) value).getFieldNames());
			};
			case Range(Integer start, Integer end, Integer step, var strategy) -> new GioviTuple(
				this.value.copy(Range.of(start, end, step)));
			case GioviInt(var index) -> value
				.getField(index >= 0 ? index : value.getFieldsCount() + index)
				.orElseThrow(() -> new NoSuchField(index));
			default -> GioviValue.super.access(right);
		};
	}


}
