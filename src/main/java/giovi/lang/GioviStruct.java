package giovi.lang;

import giovi.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.util.GioviLists;

public final class GioviStruct extends GioviValue<Struct> {

	public GioviStruct(Struct value, Struct defaultValue) {
		super(value, defaultValue);
	}

	public GioviStruct(Struct value) {
		super(value, value.copy());
	}

	@Override
	public GioviTypeStruct getGioviType() {
		return new GioviTypeStruct(value.copy());
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		throw new NonWideneable(this, type);
	}

	@Override
	public GioviStruct copy() {
		return new GioviStruct(value.copy(), defaultValue.copy());
	}

	public int getFieldsCount() {
		return value.getFieldsCount();
	}

	public GioviValue<?> getField(String identifier) {
		return value.getField(identifier);
	}

	public void boxField(int index, GioviValue<?> fieldValue) {
		try {
			value.getField(index).box(fieldValue);
		} catch (ClassCastException | NonWideneable e) {
			throw new GioviException(e);
		}
	}

	public void boxField(String field, GioviValue<?> fieldValue) {
		try {
			value.getField(field).box(fieldValue);
			// TODO: remove this crap once missing keys are handled properly
		} catch (NullPointerException | ClassCastException | NonWideneable e) {
			throw new GioviException(e);
		}
	}

	public boolean containsField(String field) {
		return value.containsField(field);
	}

	public String getTypename() {
		return value.getTypename();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			// TODO: can we assume equal typename means equal definition?
			case GioviTypeStruct t when t.getTypename().equals(this.getTypename()) -> this;
			default -> super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> value = defaultValue.copy();
			case GioviStruct v -> {
				if (this.getFieldsCount() != v.getFieldsCount()) {
					return super.redirection(content);
				}
				for (var entry : value.entrySet()) {
					String identifier = entry.getKey();
					if (!v.containsField(identifier)) {
						return super.redirection(content);
					}
					this.getField(identifier).redirection(v.getField(identifier));
				}
			}
			case GioviTuple v when v.value instanceof TupleNamed tn -> {
				if (this.getFieldsCount() != v.getFieldsCount()) {
					return super.redirection(content);
				}
				for (var entry : value.entrySet()) {
					String identifier = entry.getKey();
					if (!this.containsField(identifier) || !tn.containsField(identifier)) {
						return super.redirection(content);
					}
					this.getField(identifier).redirection(tn.getField(identifier));
				}
			}
			case GioviTuple v -> {
				if (this.getFieldsCount() != v.getFieldsCount()) {
					return super.redirection(content);
				}
				for (int i = 0; i < getFieldsCount(); i++) {
					value.getField(i).redirection(v.value.getField(i));
				}
			}
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
			case GioviDefault __ -> new GioviStruct(defaultValue.copy());
			case FieldKey(var identifier) -> value.getField(identifier);
			default -> super.dot(right);
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

