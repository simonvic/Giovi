package it.simonvic.giovi.util;

import java.util.Collection;

import giovi.lang.GioviType;
import giovi.lang.GioviVoid;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.GioviException;

public class GioviTypes {

	// TODO: A tuple/record/struct could not be directly narrower or wider than another one
	// but we may still want to infer the minimum common widest type
	/*
		A <- <
			legend: <n:"n" s:"s">, # wider than other legend
		>
		B <- <
			size: 69,
			legend: <n:"n">, # narrower than other legend
		>
		Neither one is wider nor narrower than the other
		but we may want C
		C <- <
			size: 69
			legend: <n:"n" s:"s">
		>
	*/

	// TODO: should we allow record widening to be a disjoint union instead?

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
			// find minimum widest type here or rework widening?
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
