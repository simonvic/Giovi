package it.simonvic.giovi.exceptions;

public class TypeNotFound extends GioviException {

	public TypeNotFound(String type) {
		super("Type not found: " + type);
	}

}
