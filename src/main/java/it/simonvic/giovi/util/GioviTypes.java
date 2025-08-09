package it.simonvic.giovi.util;

import java.util.Collection;

import giovi.lang.GioviType;
import giovi.lang.GioviVoid;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.GioviException;

public class GioviTypes {

	public static GioviType<?> inferGioviType(Collection<Var<?>> entries) {
		if (entries.isEmpty()) return GioviVoid.TYPE; // TODO: emh... lol
		GioviType<?> inferred = null;
		GioviType<?> next;
		for (Var<?> entry : entries) {
			next = entry.getGioviType();
			if (inferred == null) {
				inferred = next;
				continue;
			}
			if (next.equals(inferred) || next.isWidenableTo(inferred)) {
				continue;
			}
			if (inferred.isWidenableTo(next)) {
				inferred = next;
				continue;
			}
			throw new GioviException(
				"Impossible to infer list type.\n\tExpecting:\t" + inferred + "\n\tbut got:\t"
					+ next);
		}
		if (inferred == null) {
			throw new GioviException("Impossible to infer list type");
		}
		return inferred;
	}

}
