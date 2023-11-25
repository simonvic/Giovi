package it.simonvic.giovi.lang.values;

import java.util.List;

import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.AccessKey;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.functions.GioviFunction;
import it.simonvic.giovi.lang.primitives.Struct;
import it.simonvic.giovi.util.GioviLists;

public final class GioviStruct extends GioviValue<Struct> {

	protected String type;

	public GioviStruct(Struct value) {
		this("", value);
	}

	public GioviStruct(Struct value, Struct defaultValue) {
		this("", value, defaultValue);
	}

	public GioviStruct(String type, Struct value) {
		super(value, value.copy());
		this.type = type;
	}

	public GioviStruct(String type, Struct value, Struct defaultValue) {
		super(value, defaultValue);
		this.type = type;
	}


	@Override
	public String toString() {
		return type + super.toString();
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypeStruct(this.type);
	}

	@Override
	public GioviValue<Struct> copy() {
		return new GioviStruct(type, value.copy(), defaultValue.copy());
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
	public GioviValue<?> castTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypePrimitive primitiveType -> {
				var clazz = primitiveType.unbox();
				if (clazz == GioviText.class) {
					yield new GioviText(this.toString());
				}
				yield super.castTo(type);
			}
			case GioviTypeStruct structType -> {
				if (structType.unbox().equals(this.type)) {
					yield new GioviStruct(structType.unbox(), value.copy(), defaultValue.copy());
				}
				yield super.castTo(type);
			}
			default -> super.castTo(type);
		};
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
