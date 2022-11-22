package it.simonvic.giovi.exceptions;

import it.simonvic.giovi.lang.type.GioviType;

public class IncompatibleTypes extends GioviException {
	public IncompatibleTypes(GioviType type, GioviType newType) {
		super("Incompatible types. Can't assign " + newType + " to " + type);
	}
}
