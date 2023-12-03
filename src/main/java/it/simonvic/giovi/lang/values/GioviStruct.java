package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.AccessKey;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.functions.GioviFunction;
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
	public GioviObject redirection(GioviObject content) {
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
	public GioviObject equal(GioviObject right) {
		throw new OperatorNotSupported("=");
	}

	public GioviObject notEqual(GioviObject right) {
		throw new OperatorNotSupported("!=");
	}

	//                                                                                       access
	@Override
	public GioviObject dot(GioviObject right) {
		return switch (right) {
			case AccessKey r -> value.getField(r.get());
			case GioviFunction r -> super.dot(r); // TODO: implement
			default -> super.dot(right);
		};
	}

	@Override
	public GioviObject access(GioviObject right) {
		return switch (right) {
			case AccessKey r -> switch (r.get()) {
				case "%" -> new GioviInt(value.getFieldsCount());
				case "@" -> GioviLists.of(value.getFieldNames());
				default -> super.access(right);
			};
			case GioviInt r -> value.getField(r.unbox());
			default -> super.access(right);
		};
	}

}
