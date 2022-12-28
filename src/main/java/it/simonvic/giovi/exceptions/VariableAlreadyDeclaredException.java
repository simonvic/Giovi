package it.simonvic.giovi.exceptions;

public class VariableAlreadyDeclaredException extends GioviException {
	public VariableAlreadyDeclaredException(String identifier) {
		super("Variable already defined: " + identifier);
	}
}
