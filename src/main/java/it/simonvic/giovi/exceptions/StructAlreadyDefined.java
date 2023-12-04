package it.simonvic.giovi.exceptions;

public class StructAlreadyDefined extends GioviException {

	public StructAlreadyDefined(String identifier) {
		super("Struct already defined: " + identifier);
	}

}
