package it.simonvic.giovi;

import giovi.lang.GioviType;

public interface Typed {

	/**
	 * Get the Type (in Giovi space)
	 * @return Type
	 */
	GioviType<?> getGioviType();

}

