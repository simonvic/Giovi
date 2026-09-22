package giovi.lang;

import static it.simonvic.giovi.util.GioviUtils.nativeIndexOf;

import giovi.GRecord;
import giovi.GioviParserVisitResult;
import giovi.MemberKey;
import giovi.Range;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviRecords;

@DescribedBy(GioviTypeRecord.class)
public record GioviRecord(GioviTypeRecord type, GRecord value)
	implements GioviProduct<GRecord> {

	public GioviRecord() {
		this(new GioviTypeRecord(), new GRecord());
	}

	@Override
	public String toString() {
		return "record." + value.toString();
	}

	@Override
	public GioviTypeRecord getGioviType() {
		return type;
	}

	@Override
	public GioviRecord copy() {
		return new GioviRecord(type, value.copy());
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
			case GioviTuple v -> GioviRecords.redirection(this, v);
			case GioviRecord v -> GioviRecords.redirection(this, v);
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
			case MemberKey(var identifier)
				-> value.getField(identifier).orElseThrow(() -> new NoSuchField(identifier));
			default -> GioviProduct.super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case MemberKey(var identifier) when identifier.startsWith("_") -> switch (identifier) {
				case "_fields" -> GioviLists.of(value.getFieldNames());
				default -> GioviProduct.super.access(right);
			};
			case Range(Integer _, _, _)
				-> new GioviRecord(
					new GioviTypeRecord(this.type.signature().copy((Range<Integer>) right)),
					this.value.copy((Range<Integer>) right)
				);
			// TODO: make this available for tuple only? More explicit that we're discarding names
			case GioviInt(var index)
				-> value.getField(nativeIndexOf(index, value.getFieldsCount()))
					.orElseThrow(() -> new NoSuchField(index));
			default -> GioviProduct.super.access(right);
		};
	}


}
