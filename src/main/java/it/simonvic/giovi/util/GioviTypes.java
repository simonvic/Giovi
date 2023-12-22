package it.simonvic.giovi.util;

import java.util.Collection;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.GioviStructDefinition;
import it.simonvic.giovi.lang.values.*;

public class GioviTypes {

	public static GioviType<?> inferGioviType(Collection<GioviValue<?>> entries) {
		GioviType<?> inferred = null;
		GioviType<?> next;
		for (GioviValue<?> entry : entries) {
			next = entry.getGioviType();
			if (inferred == null) {
				inferred = next;
				continue;
			}
			if (next.equals(inferred) || next.canUpcast(inferred)) {
				continue;
			}
			if (inferred.canUpcast(next)) {
				inferred = next;
				continue;
			}
			throw new GioviException("Impossible to infer list type. Expecting\n\t" + inferred + " but got\n\t" + next);
		}
		if (inferred == null) {
			throw new GioviException("Impossible to infer list type");
		}
		return inferred;
	}

	public static GioviType<?> of(Class<? extends GioviValue<?>> clazz) {
		if (clazz == GioviVoid.class) return new GioviTypeVoid();
		if (clazz == GioviBool.class) return new GioviTypeBool();
		if (clazz == GioviNatural.class) return new GioviTypeNatural();
		if (clazz == GioviInt.class) return new GioviTypeInt();
		if (clazz == GioviReal.class) return new GioviTypeReal();
		if (clazz == GioviText.class) return new GioviTypeText();
		throw new GioviException("Unknown type for class " + clazz);
	}

	public static GioviType<?> of(GioviStructDefinition structDefinition) {
		return new GioviTypeStruct(structDefinition);
	}


}
