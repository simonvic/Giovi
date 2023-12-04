package it.simonvic.giovi.exceptions;

public class StructNotDefined extends GioviException {

	public StructNotDefined(String identifier) {
		super("Struct not defined: " + identifier);
	}

}
