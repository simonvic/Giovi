package it.simonvic.giovi.exceptions;

public class VariableNotDefinedException extends GioviException {
	public VariableNotDefinedException() {
		super();
	}

	public VariableNotDefinedException(String identifier) {
		super("Variable not defined: " + identifier);
	}

}
