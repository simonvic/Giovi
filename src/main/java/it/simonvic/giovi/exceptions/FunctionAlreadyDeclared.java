package it.simonvic.giovi.exceptions;

import it.simonvic.giovi.lang.GioviFunction;

public class FunctionAlreadyDeclared extends GioviException {
	public FunctionAlreadyDeclared(GioviFunction signature) {
		super("Function already declared with signature: " + signature);
	}
}
