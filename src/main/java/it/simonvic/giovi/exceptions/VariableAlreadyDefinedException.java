package it.simonvic.giovi.exceptions;

public class VariableAlreadyDefinedException extends RuntimeException{
	public VariableAlreadyDefinedException() {
	}

	public VariableAlreadyDefinedException(String identifier) {
		super("Variable already defined: " + identifier);
	}
}
