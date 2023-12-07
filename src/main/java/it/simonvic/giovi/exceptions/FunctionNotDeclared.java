package it.simonvic.giovi.exceptions;

public class FunctionNotDeclared extends Throwable {
	public FunctionNotDeclared(String identifier) {
		super("Function not declared with identifier: " + identifier);
	}

}
