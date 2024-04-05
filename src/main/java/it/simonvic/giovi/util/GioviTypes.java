package it.simonvic.giovi.util;

import java.util.Collection;

import giovi.lang.GioviType;
import giovi.lang.GioviTypeVoid;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.GioviException;

public class GioviTypes {

	public static GioviType<?> inferGioviType(Collection<GioviValue<?>> entries) {
		if (entries.isEmpty()) return new GioviTypeVoid(); // TODO: emh... lol
		GioviType<?> inferred = null;
		GioviType<?> next;
		for (GioviValue<?> entry : entries) {
			next = entry.getGioviType();
			if (inferred == null) {
				inferred = next;
				continue;
			}
			if (next.equals(inferred) || next.canWidenTo(inferred)) {
				continue;
			}
			if (inferred.canWidenTo(next)) {
				inferred = next;
				continue;
			}
			throw new GioviException(
				"Impossible to infer list type. Expecting\n\t" + inferred + " but got\n\t" + next);
		}
		if (inferred == null) {
			throw new GioviException("Impossible to infer list type");
		}
		return inferred;
	}

}
