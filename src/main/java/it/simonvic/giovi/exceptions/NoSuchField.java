package it.simonvic.giovi.exceptions;

public class NoSuchField extends GioviException {

	public NoSuchField(String identifier) {
		super("No such field with name " + identifier);
	}

	public NoSuchField(Integer index) {
		super("No field at index " + index);
	}

}
