package it.simonvic.giovi.exceptions;

import it.simonvic.giovi.lang.values.GioviStruct;

public class StructException extends GioviException {

	public StructException(GioviStruct gioviStruct, String identifier) {
		super("Struct " + gioviStruct + " doesn't have the field " + identifier);
	}
}
