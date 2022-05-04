package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public interface GioviCollection {

	GioviValue<?> access(GioviObject accessor);
}
