package it.simonvic.giovi.exceptions;

public class VariableNotDefinedException extends RuntimeException {
	public VariableNotDefinedException() {
	}

	public VariableNotDefinedException(String identifier) {
		super("Variable not defined: " + identifier);
	}

}
