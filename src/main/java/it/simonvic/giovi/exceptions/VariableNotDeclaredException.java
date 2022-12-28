package it.simonvic.giovi.exceptions;

public class VariableNotDeclaredException extends GioviException {
	public VariableNotDeclaredException() {
		super();
	}

	public VariableNotDeclaredException(String identifier) {
		super("Variable not defined: " + identifier);
	}

}
