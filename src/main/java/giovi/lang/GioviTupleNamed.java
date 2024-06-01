package giovi.lang;

import giovi.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.UnknownTupleField;
import it.simonvic.giovi.util.GioviLists;

public sealed class GioviTupleNamed extends GioviTuple permits GioviStruct {

	// TODO: find a way to avoid casting everytime, without having to maintain a double reference

	public GioviTupleNamed(TupleNamed value, TupleNamed defaultValue) {
		super(value, defaultValue);
	}

	public GioviTupleNamed(TupleNamed value) {
		super(value, value.copy());
	}

	public GioviTupleNamed() {
		super(new TupleNamed(), new TupleNamed());
	}

	@Override
	public GioviTypeTuple getGioviType() {
		return new GioviTypeTupleNamed((TupleNamed) defaultValue.copy());
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
				default -> throw new NonWideneable(this, type);
			};
		} catch (NonWideneable ex) {
			throw new NonWideneable(this, type, ex);
		}
	}

	@Override
	public GioviTupleNamed copy() {
		return new GioviTupleNamed((TupleNamed) value.copy(), (TupleNamed) defaultValue.copy());
	}

	public GioviValue<?> getField(String identifier) throws UnknownTupleField {
		return ((TupleNamed) value).getField(identifier);
	}

	public void boxField(int index, GioviValue<?> fieldValue) {
		try {
			value.getField(index).box(fieldValue);
		} catch (ClassCastException e) {
			throw new GioviException(e);
		}
	}

	public void boxField(String field, GioviValue<?> fieldValue) {
		try {
			((TupleNamed) value).getField(field).box(fieldValue);
		} catch (ClassCastException | UnknownTupleField e) {
			throw new GioviException(e);
		}
	}

	public boolean containsField(String field) {
		return ((TupleNamed) value).containsField(field);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> {
				super. redirection(content);
				this.value = (TupleNamed) super.value;
			}
			case GioviStruct v -> {
				if (this.getFieldsCount() != v.getFieldsCount()) {
					return super.redirection(content);
				}
				for (var entry : ((TupleNamed) value).entrySet()) {
					String identifier = entry.getKey();
					try {
						this.getField(identifier).redirection(v.getField(identifier));
					} catch (UnknownTupleField e) {
						return super.redirection(content);
					}
				}
			}
			case GioviTuple v when v.value instanceof TupleNamed tn -> {
				if (this.getFieldsCount() != v.getFieldsCount()) {
					return super.redirection(content);
				}
				for (var entry : ((TupleNamed) value).entrySet()) {
					String identifier = entry.getKey();
					try {
						this.getField(identifier).redirection(tn.getField(identifier));
					} catch (UnknownTupleField e) {
						return super.redirection(content);
					}
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

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviTupleNamed((TupleNamed) defaultValue.copy());
			case FieldKey(var identifier) -> {
				try {
					yield ((TupleNamed) value).getField(identifier);
				} catch (UnknownTupleField e) {
					yield super.dot(right);
				}
			}
			default -> super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value.getFieldsCount());
				case KEYS -> Range.of(0, value.getFieldsCount() - 1, 1);
				case VALUES -> GioviLists.of(((TupleNamed) value).getFieldNames());
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
