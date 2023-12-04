package it.simonvic.giovi.exceptions;

public class ConstantAlreadyDeclared extends GioviException {
	public ConstantAlreadyDeclared(String identifier) {
		super("Constant already declared: " + identifier);
	}
}
