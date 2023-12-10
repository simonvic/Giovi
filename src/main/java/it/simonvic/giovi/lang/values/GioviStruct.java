package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.AccessKey;
import it.simonvic.giovi.lang.GioviParserVisitResult;
import it.simonvic.giovi.lang.values.natives.Struct;
import it.simonvic.giovi.util.GioviLists;

public abstract sealed class GioviStruct
	extends GioviValue<Struct>
	permits GioviStructTyped, GioviStructTuple {

	public GioviStruct(Struct value, Struct defaultValue) {
		super(value, defaultValue);
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		if (gioviValue instanceof GioviStruct struct) {
			value.from(struct.unbox());
		} else {
			super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(Struct struct) {
		this.defaultValue.from(struct);
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue.unbox()) {
			case Struct v -> setDefaultValue(v);
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public void boxDefault() {
		value = defaultValue.copy();
	}

	//                                                                                OPERATORATION
	//                                                                                  redirection
	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			case GioviStruct v -> box(v.copy());
			default -> super.redirection(content);
		}
		return this;
	}

	//                                                                                   arithmetic
	//                                                                                      boolean
	//                                                                                   relational
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		throw new OperatorNotSupported("=");
	}

	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		throw new OperatorNotSupported("!=");
	}

	//                                                                                       access
	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey r -> value.getField(r.text());
			default -> super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey r -> switch (r.text()) {
				case "%" -> new GioviInt(value.getFieldsCount());
				case "@" -> GioviLists.of(value.getFieldNames());
				default -> super.access(right);
			};
			case GioviInt r -> value.getField(r.unbox());
			default -> super.access(right);
		};
	}

}
