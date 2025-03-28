package giovi.lang;

import giovi.*;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.util.GioviTuples;

public record GioviTuple(TupleSignature signature, Tuple value)
	implements GioviValue<Tuple> {

	public GioviTuple() {
		this(new TupleSignature(), new Tuple());
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Tuple defaultValue() {
		return signature.spawn();
	}

	@Override
	public GioviTypeTuple getGioviType() {
		return new GioviTypeTuple(signature);
	}

	@Override
	public GioviTuple copy() {
		return new GioviTuple(signature, value.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviTuple(signature, signature.spawn());
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
			case GioviDefault _ -> new GioviTuple(signature, signature.spawn());
			default -> GioviValue.super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value.getFieldsCount());
				case KEYS -> Range.of(0, value.getFieldsCount() - 1, 1);
				default -> GioviValue.super.access(right);
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
