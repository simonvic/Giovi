package it.simonvic.giovi.util;

import java.util.ArrayList;

import giovi.Tuple;
import it.simonvic.giovi.VarD;

public class Tuples {

	public static Tuple ofNative(Object... fields) {
		var l = new ArrayList<VarD<?>>();
		for (var field : fields) {
			l.add(VarD.of(field));
		}
		return new Tuple(l);
	}


}
