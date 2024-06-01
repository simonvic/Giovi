package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.Struct;
import giovi.TupleNamed;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.UnknownTupleField;

public final class GioviStruct extends GioviTupleNamed {

	// TODO: find a way to avoid casting everytime, without having to maintain a double reference

	public GioviStruct(Struct value, Struct defaultValue) {
		super(value, defaultValue);
	}

	public GioviStruct(Struct value) {
		super(value, value.copy());
	}

	@Override
	public GioviTypeStruct getGioviType() {
		// FIXME: this would produce two different types as their structs would likely have different value
		return new GioviTypeStruct((Struct) defaultValue.copy());
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		try {
			return switch (type) {
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
	public GioviStruct copy() {
		return new GioviStruct((Struct) value.copy(), (Struct) defaultValue.copy());
	}

	public String getTypename() {
		return ((Struct) value).getTypename();
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
			case GioviStruct v -> {
				if (this.getFieldsCount() != v.getFieldsCount()) {
					return super.redirection(content);
				}
				for (var entry : ((Struct) value).entrySet()) {
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
				for (var entry : ((Struct) value).entrySet()) {
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
			case GioviDefault _ -> new GioviStruct((Struct) defaultValue.copy());
			default -> super.dot(right);
		};
	}


}

