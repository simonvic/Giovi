package it.simonvic.giovi.exceptions;

public class TypeNotFound extends RuntimeException {

	public TypeNotFound(String type) {
		super("Type not found: " + type);
	}

}
