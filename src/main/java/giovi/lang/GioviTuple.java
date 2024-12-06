package giovi.lang;

import java.util.ArrayList;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.Range;
import giovi.Record;
import giovi.Tuple;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviTuples;

public record GioviTuple(Tuple signature, Tuple value)
	implements GioviValue<Tuple> {

	public GioviTuple() {
		this(new Tuple(), new Tuple());
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Tuple defaultValue() {
		return signature.copy();
	}

	@Override
	public GioviTypeTuple getGioviType() {
		return new GioviTypeTuple(signature);
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		try {
			return switch (type) {
				case GioviTypeStruct(var definition)
					-> new GioviStruct(definition, this.value.widenTo(definition));
				case GioviTypeRecord(var definition)
					-> new GioviRecord(definition, this.value.widenTo(definition));
				case GioviTypeTuple(var definition)
					-> new GioviTuple(definition, this.value.widenTo(definition));
				default -> throw new NonWideneable(this, type);
			};
		} catch (NonWideneable ex) {
			throw new NonWideneable(this, type, ex);
		}
	}

	@Override
	public GioviTuple copy() {
		// TODO: unnecessary copy?
		return new GioviTuple(signature.copy(), value.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviTuple(signature, signature.copy());
			case GioviTuple v -> GioviTuples.redirection(this, v);
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
			case GioviDefault _ -> new GioviTuple(signature.copy(), signature.copy());
			default -> GioviValue.super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value.getFieldsCount());
				case KEYS -> Range.of(0, value.getFieldsCount() - 1, 1);
				case VALUES -> switch (value) {
					case Record record -> GioviLists.of(record.getFieldNames());
					default -> GioviValue.super.access(right);
				};
			};
			case Range(Integer start, Integer end, Integer step, _)
				-> new GioviTuple(
					this.signature.copy((Range<Integer>) right),
					this.value.copy((Range<Integer>) right));
			case GioviInt(var index)
				-> value.getField(index >= 0 ? index : value.getFieldsCount() + index)
					.orElseThrow(() -> new NoSuchField(index));
			default -> GioviValue.super.access(right);
		};
	}

}
