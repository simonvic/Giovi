package it.simonvic.giovi.util;

import java.util.ArrayList;

import giovi.GTuple;
import it.simonvic.giovi.GField;

public class GTuples {

	public static GTuple ofNative(Object... fields) {
		var l = new ArrayList<GField<?>>(fields.length);
		for (var field : fields) {
			l.add(GFields.of(field));
		}
		return new GTuple(l);
	}


}
