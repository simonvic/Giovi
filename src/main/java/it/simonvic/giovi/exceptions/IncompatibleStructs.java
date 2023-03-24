package it.simonvic.giovi.exceptions;

public class IncompatibleStructs extends GioviException {

	public IncompatibleStructs() {
		super("Incompatible structs");
	}

	public IncompatibleStructs(String message) {
		super(message);
	}
}
