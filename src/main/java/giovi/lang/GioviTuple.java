package giovi.lang;

import static it.simonvic.giovi.util.GioviUtils.nativeIndexOf;

import giovi.GTuple;
import giovi.GioviParserVisitResult;
import giovi.Range;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;
import it.simonvic.giovi.util.GioviTuples;

@DescribedBy(GioviTypeTuple.class)
public record GioviTuple(GioviTypeTuple type, GTuple value)
	implements GioviProduct<GTuple> {

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

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviTypeStruct v -> new GioviStruct(v, this.value.as(v.signature()));
			case GioviTypeRecord v -> new GioviRecord(v, this.value.as(v.signature()));
			case GioviTypeTuple v -> new GioviTuple(v, this.value.as(v.signature()));
			default -> GioviProduct.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviTuple v -> GioviTuples.redirection(this, v);
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
			case Range(Integer _, _, _)
				-> new GioviTuple(
					new GioviTypeTuple(this.type.signature().copy((Range<Integer>) right)),
					this.value.copy((Range<Integer>) right)
				);
			case GioviInt(var index)
				-> value.getField(nativeIndexOf(index, value.getFieldsCount()))
					.orElseThrow(() -> new NoSuchField(index));
			default -> GioviProduct.super.dot(right);
		};
	}

}
