package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.StructException;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.functions.GioviFunction;
import it.simonvic.giovi.lang.primitives.Struct;

public abstract sealed class GioviStruct extends GioviValue<Struct>
	permits GioviStructNamed, GioviStructUnnamed {

	public GioviStruct(Struct value, Struct defaultValue) {
		super(value, defaultValue);
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypePrimitive(GioviStruct.class);
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		if (gioviValue instanceof GioviStruct struct) {
			value.set(struct.unbox());
		} else {
			super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(Struct struct) {
		this.defaultValue.set(struct);
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
		value = Struct.copyOf(defaultValue);
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
}
