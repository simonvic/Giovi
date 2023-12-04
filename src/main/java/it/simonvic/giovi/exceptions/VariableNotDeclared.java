package it.simonvic.giovi.exceptions;

public class VariableNotDeclared extends GioviException {

	public VariableNotDeclared(String identifier) {
		super("Variable not defined: " + identifier);
	}

}
