package it.simonvic.giovi.lang.values;

import java.util.Collection;

import it.simonvic.giovi.exceptions.GioviException;

public abstract sealed class GioviType<T>
	extends GioviValue<T>
	permits GioviTypePrimitive, GioviTypeStruct {

	public GioviType(T value, T defaultValue) {
		super(value, defaultValue);
	}


	public abstract boolean canUpcast(GioviType<?> to);

	public static GioviType<?> inferGioviType(Collection<GioviValue<?>> entries) {
		GioviType<?> inferred = null;
		GioviType<?> next;
		for (GioviValue<?> entry : entries) {
			next = entry.getGioviType();
			if (inferred == null || inferred.canUpcast(next)) {
				inferred = next;
			}
		}
		if (inferred == null) {
			throw new GioviException("Impossible to infer list type");
		}
		return inferred;
	}
}
