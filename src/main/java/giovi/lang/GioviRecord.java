package giovi.lang;

import giovi.AccessKey;
import giovi.GioviParserVisitResult;
import giovi.MemberKey;
import giovi.Range;
import giovi.Record;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviRecords;

public record GioviRecord(Record signature, Record value)
	implements GioviValue<Record> {

	// TODO: find a way to avoid casting everytime, without having to maintain a double reference

	public GioviRecord() {
		this(new Record(), new Record());
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Record defaultValue() {
		return signature.copy();
	}

	@Override
	public GioviTypeRecord getGioviType() {
		return new GioviTypeRecord(signature);
	}

	@Override
	public GioviRecord copy() {
		return new GioviRecord(signature, value.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviRecord(signature, signature.copy());
			case GioviTuple v -> GioviRecords.redirection(this, v);
			case GioviRecord v -> GioviRecords.redirection(this, v);
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
			case GioviDefault _ -> new GioviRecord();
			case MemberKey(var identifier)
				-> value.getField(identifier).orElseThrow(() -> new NoSuchField(identifier));
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
			case Range(Integer start, Integer end, Integer step, _)
				-> new GioviRecord(
					this.signature.copy((Range<Integer>) right),
					this.value.copy((Range<Integer>) right));
			case GioviInt(var index)
				-> value.getField(index >= 0 ? index : value.getFieldsCount() + index)
					.orElseThrow(() -> new NoSuchField(index));
			default -> GioviValue.super.access(right);
		};
	}


}
