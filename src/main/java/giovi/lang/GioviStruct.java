package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.Struct;
import giovi.VarD;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.UnknownTupleField;
import it.simonvic.giovi.util.GioviTuples;

public record GioviStruct(Struct value) implements GioviValue<Struct> {

	// TODO: find a way to avoid casting everytime, without having to maintain a double reference

	@Override
	public GioviTypeStruct getGioviType() {
		// FIXME: this would produce two different types as their structs would likely have different value
		return new GioviTypeStruct(defaultValue());
	}

	@Override
	public Struct defaultValue() {
		return value.copy(); // TODO: use signature
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
		return new GioviStruct(value.copy());
	}

	public String getTypename() {
		return value.getTypename();
	}

	public int getFieldsCount() {
		return value.getFieldsCount();
	}

	public VarD<?> getField(String identifier) throws UnknownTupleField {
		return value.getField(identifier);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			// TODO: can we assume equal typename means equal definition?
			case GioviTypeStruct t when t.getTypename().equals(this.getTypename()) -> this;
			default -> GioviValue.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviTupleNamed(defaultValue().copy());
			case GioviTuple v -> GioviTuples.redirection(this, v);
			case GioviTupleNamed v -> GioviTuples.redirection(this, v);
			case GioviStruct v -> GioviTuples.redirection(this, v);
			default -> GioviValue.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviStruct(defaultValue().copy());
			default -> GioviValue.super.dot(right);
		};
	}


}

