package it.simonvic.giovi.exceptions;

import it.simonvic.giovi.lang.FunctionSignature;

public class FunctionAlreadyDeclared extends GioviException {
	public FunctionAlreadyDeclared(FunctionSignature signature) {
		super("Function already declared with signature: " + signature);
	}
}
