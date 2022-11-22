package it.simonvic.giovi.exceptions;

public class VariableAlreadyDefinedException extends GioviException {
	public VariableAlreadyDefinedException(String identifier) {
		super("Variable already defined: " + identifier);
	}
}
