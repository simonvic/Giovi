package giovi.lang;

import giovi.*;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.UnknownTupleField;
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
		return new GioviTypeTuple(defaultValue.copy());
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		try {
			return switch (type) {
				case GioviTypeTuple t -> {
					var widened = this.value.widenTo(t.getDefinition());
					yield new GioviTuple(widened);
				}
				case GioviTypeStruct t -> {
					var widened = this.value.widenTo(t.getDefinition());
					yield new GioviStruct(widened);
				}
				default -> throw new NonWideneable(this, type);
			};
		} catch (NonWideneable ex) {
			throw new NonWideneable(this, type, ex);
		}
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

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> value = defaultValue.copy();
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
			case GioviDefault _ -> new GioviBool(value.equal(defaultValue));
			case GioviStruct t -> new GioviBool(this.value.equal(t.value));
			case GioviTuple t -> new GioviBool(this.value.equal(t.value));
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
			case GioviDefault _ -> new GioviTuple(defaultValue.copy());
			case FieldKey(var identifier) -> switch (value) {
				case TupleNamed named -> {
					try {
						yield named.getField(identifier);
					} catch (UnknownTupleField e) {
						yield super.dot(right);
					}
				}
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
