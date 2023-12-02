package it.simonvic.giovi.util;

import java.util.Collection;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.primitives.Type;
import it.simonvic.giovi.lang.values.*;

public class GioviTypes {

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

	public static GioviType<?> of(Class<? extends GioviValue<?>> clazz) {
		if (clazz == GioviNatural.class
			|| clazz == GioviInt.class
			|| clazz == GioviReal.class
			|| clazz == GioviBool.class
			|| clazz == GioviText.class) {
			return new GioviTypePrimitive(new Type.Primitive<>(clazz));
		}
		throw new GioviException("Unknown type for class " + clazz);
	}


}
