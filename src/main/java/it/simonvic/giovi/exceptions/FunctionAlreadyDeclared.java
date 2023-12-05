package it.simonvic.giovi.exceptions;

import it.simonvic.giovi.lang.GioviFunctionSignature;

public class FunctionAlreadyDeclared extends GioviException {
	public FunctionAlreadyDeclared(GioviFunctionSignature signature) {
		super("Function already declared with signature: " + signature);
	}
}
