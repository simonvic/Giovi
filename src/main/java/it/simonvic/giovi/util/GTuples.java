package it.simonvic.giovi.util;

import java.util.ArrayList;

import giovi.GTuple;
import it.simonvic.giovi.Member;

public class GTuples {

	public static GTuple ofNative(Object... fields) {
		var l = new ArrayList<Member<?>>(fields.length);
		for (var field : fields) {
			l.add(Members.of(field));
		}
		return new GTuple(l);
	}


}
