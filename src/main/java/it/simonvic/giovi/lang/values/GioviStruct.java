package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.StructException;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.functions.GioviFunction;
import it.simonvic.giovi.lang.primitives.Struct;

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

	@Override
	public GioviObject redirection(GioviObject content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			case GioviStruct v -> box(v);
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviObject dot(String identifier) {
		GioviValue<?> field = null;
		switch (value) {
			case Struct.Named s -> field = s.get(identifier);
			case Struct.Unnamed s -> super.dot(identifier);
		}
		if (field == null) throw new StructException(this, identifier);
		return field;
	}

	@Override
	public GioviObject dot(GioviFunction function) {
		return super.dot(function);
	}

	@Override
	public String toString() {
		return type + super.toString();
	}
}
