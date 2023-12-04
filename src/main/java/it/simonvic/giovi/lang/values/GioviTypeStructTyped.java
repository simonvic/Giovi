package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviStructDefinition;
import it.simonvic.giovi.lang.values.natives.Type;

public final class GioviTypeStructTyped extends GioviTypeStruct<Type.Struct.Typed> {

	private final GioviStructDefinition definition;

	public GioviTypeStructTyped(
		Type.Struct.Typed value,
		Type.Struct.Typed defaultValue,
		GioviStructDefinition definition) {
		super(value, defaultValue);
		this.definition = definition;
	}

	public GioviTypeStructTyped(Type.Struct.Typed value, GioviStructDefinition definition) {
		this(value, value.copy(), definition);
	}

	public GioviTypeStructTyped(GioviStructDefinition definition) {
		this(new Type.Struct.Typed(), definition);
	}

	public GioviStructDefinition getDefinition() {
		return definition;
	}

	@Override
	public GioviStructTyped spawn() {
		return new GioviStructTyped(definition);
	}

	@Override
	public GioviType<Type.Struct.Typed> copy() {
		return new GioviTypeStructTyped(value.copy(), defaultValue.copy(), definition);
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStructTyped t -> this.getDefinition().isSubset(t.getDefinition());
			default -> false;
		};
	}

}

