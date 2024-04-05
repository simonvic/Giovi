package giovi.lang;

import giovi.*;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.util.GioviLists;

public final class GioviTuple extends GioviValue<Tuple> {

	public GioviTuple(Tuple value, Tuple defaultValue) {
		super(value, defaultValue);
	}

	public GioviTuple(Tuple value) {
		super(value, new Tuple());
	}

	public GioviTuple() {
		super(new Tuple(), new Tuple());
	}

	@Override
	public GioviTypeTuple getGioviType() {
		return new GioviTypeTuple();
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		// TODO: the heavylifting goes here
		throw new NonWideneable(this, type);
	}

	@Override
	public GioviTuple copy() {
		return new GioviTuple(value.copy(), defaultValue.copy());
	}

	public int getFieldsCount() {
		return value.getFieldsCount();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return super.as(right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> value = defaultValue.copy();
			default -> super.redirection(content);
		}
		return this;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviTuple t -> {
				if (this.getFieldsCount() != t.getFieldsCount()) {
					yield new GioviBool(false);
				}
				for (int i = 0; i < getFieldsCount(); i++) {
					try {
						if (!value.getField(i).equal(t.value.getField(i)).asNativeBool()) {
							yield new GioviBool(false);
						}
					} catch (OperatorNotSupported ex) {
						yield new GioviBool(false);
					}

				}
				yield new GioviBool(true);
			}
			default -> super.equal(right);
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
			case GioviDefault __ -> new GioviTuple(defaultValue.copy());
			case FieldKey(var identifier) -> switch (value) {
				case TupleNamed named -> named.getField(identifier);
				default -> super.dot(right);
			};
			default -> super.dot(right);
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
					default -> super.access(right);
				};
			};
			case Range(Integer start, Integer end, Integer step, var strategy) -> new GioviTuple(
				this.value.copy(Range.of(start, end, step)));
			case GioviInt i -> {
				int index = i.unbox();
				index = index >= 0 ? index : value.getFieldsCount() + index;
				yield value.getField(index);
			}
			default -> super.access(right);
		};
	}

}
