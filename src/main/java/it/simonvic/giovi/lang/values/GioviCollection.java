package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.Accessor;
import it.simonvic.giovi.lang.GioviObject;

public abstract sealed class GioviCollection<T> extends GioviValue<T> permits GioviList, GioviMap {

	public GioviCollection(T value, T defaultValue) {
		super(value, defaultValue);
	}

	public GioviValue<?> access(GioviObject accessor) {
		return switch(accessor) {
			case GioviValue<?> v -> access(v);
			case Accessor v -> access(v);
			default -> throw new GioviException("Unknown accessor");
		};
	}

	public abstract GioviValue<?> access(GioviValue<?> accessor);
	public abstract GioviValue<?> access(Accessor accessor);

}
