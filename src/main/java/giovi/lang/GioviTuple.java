package giovi.lang;

import giovi.*;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviTuples;

public record GioviTuple(Tuple value) implements GioviValue<Tuple> {

	public GioviTuple() {
		this(new Tuple());
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Tuple defaultValue() {
		return value.copy(); // TODO: replace with signature, or eventually build one from the value()
	}

	@Override
	public GioviTypeTuple getGioviType() {
		return new GioviTypeTuple(value().copy()); // TODO: replace with signature
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		try {
			return switch (type) {
				case GioviTypeStruct t -> {
					var widened = this.value.widenTo(t.getDefinition());
					yield new GioviStruct(widened);
				}
				case GioviTypeTupleNamed t -> {
					var widened = this.value.widenTo(t.getDefinition());
					yield new GioviTupleNamed(widened);
				}
				case GioviTypeTuple t -> {
					var widened = this.value.widenTo(t.getDefinition());
					yield new GioviTuple(widened);
				}
				default -> throw new NonWideneable(this, type);
			};
		} catch (NonWideneable ex) {
			throw new NonWideneable(this, type, ex);
		}
	}

	@Override
	public GioviTuple copy() {
		return new GioviTuple(value.copy());
	}

	public int getFieldsCount() {
		return value.getFieldsCount();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviTuple(defaultValue().copy());
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
			case GioviStruct(var v) -> new GioviBool(this.value.equal(v));
			case GioviTupleNamed(var v) -> new GioviBool(this.value.equal(v));
			case GioviTuple(var v) -> new GioviBool(this.value.equal(v));
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
			case GioviDefault _ -> new GioviTuple();
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
					case TupleNamed named -> GioviLists.of(named.getFieldNames());
					default -> GioviValue.super.access(right);
				};
			};
			case Range(Integer start, Integer end, Integer step, var strategy) -> new GioviTuple(
				this.value.copy(Range.of(start, end, step)));
			case GioviInt i -> {
				int index = i.value();
				index = index >= 0 ? index : value.getFieldsCount() + index;
				yield value.getField(index);
			}
			default -> GioviValue.super.access(right);
		};
	}

}
