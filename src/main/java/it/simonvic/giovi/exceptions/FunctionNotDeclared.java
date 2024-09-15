package it.simonvic.giovi.exceptions;

public class FunctionNotDeclared extends GioviException {
	public FunctionNotDeclared(String identifier) {
		super("Function not declared with identifier: " + identifier);
	}

}
