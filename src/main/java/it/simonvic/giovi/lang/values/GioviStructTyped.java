package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviStructDefinition;
import it.simonvic.giovi.lang.primitives.Struct;

public final class GioviStructTyped extends GioviStruct {

	private final GioviStructDefinition definition;

	public GioviStructTyped(Struct value, Struct defaultValue, GioviStructDefinition definition) {
		super(value, defaultValue);
		this.definition = definition;
	}

	public GioviStructTyped(Struct value, GioviStructDefinition definition) {
		this(value, value.copy(), definition);
	}

	public GioviStructTyped(GioviStructDefinition definition) {
		this(definition.struct().copy(), definition);
	}

	public GioviStructDefinition getDefinitionStruct() {
		return definition;
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypeStructTyped(definition);
	}

	@Override
	public GioviValue<Struct> copy() {
		return new GioviStructTyped(value.copy(), defaultValue.copy(), definition);
	}

}

