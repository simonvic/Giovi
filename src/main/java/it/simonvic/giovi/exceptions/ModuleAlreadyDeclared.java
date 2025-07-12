package it.simonvic.giovi.exceptions;

public class ModuleAlreadyDeclared extends GioviException {

	public ModuleAlreadyDeclared(String identifier) {
		super("Module already declared: " + identifier);
	}

}
