package giovi.lang;

import giovi.AccessKey;
import giovi.GTuple;
import giovi.GioviParserVisitResult;
import giovi.Range;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;
import it.simonvic.giovi.util.GioviTuples;

@DescribedBy(GioviTypeTuple.class)
public record GioviTuple(GioviTypeTuple type, GTuple value)
	implements GioviValue<GTuple> {

	public GioviTuple() {
		this(new GioviTypeTuple(), new GTuple());
	}

	@Override
	public String toString() {
		return "tuple." + value.toString();
	}

	@Override
	public GioviTypeTuple getGioviType() {
		return type;
	}

	@Override
	public GioviTuple copy() {
		return new GioviTuple(type, value.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
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
			case GioviStruct(_, var v) -> GioviBool.of(this.value.equal(v));
			case GioviRecord(_, var v) -> GioviBool.of(this.value.equal(v));
			case GioviTuple(_, var v) -> GioviBool.of(this.value.equal(v));
			default -> GioviValue.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value.getFieldsCount());
				case KEYS -> Range.of(0, value.getFieldsCount() - 1, 1);
				default -> GioviValue.super.access(right);
			};
			case Range(Integer _, _, _, _)
				-> new GioviTuple(
					new GioviTypeTuple(this.type.signature().copy((Range<Integer>) right)),
					this.value.copy((Range<Integer>) right)
				);
			case GioviInt(var index)
				-> value.getField(index >= 0 ? index : value.getFieldsCount() + index)
					.orElseThrow(() -> new NoSuchField(index));
			default -> GioviValue.super.access(right);
		};
	}

}
