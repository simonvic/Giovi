package it.simonvic.giovi.exceptions;

public class VariableAlreadyDeclared extends GioviException {

	public VariableAlreadyDeclared(String identifier) {
		super("Variable already declared: " + identifier);
	}

}
