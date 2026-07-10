package it.simonvic.giovi.util;

import java.util.ArrayList;

import giovi.GTuple;
import it.simonvic.giovi.VarD;

public class GTuples {

	public static GTuple ofNative(Object... fields) {
		var l = new ArrayList<VarD<?>>(fields.length);
		for (var field : fields) {
			l.add(VarD.of(field));
		}
		return new GTuple(l);
	}


}
