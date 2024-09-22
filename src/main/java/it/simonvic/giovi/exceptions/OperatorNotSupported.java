package it.simonvic.giovi.exceptions;

import it.simonvic.giovi.GioviToken;

public class OperatorNotSupported extends GioviException {

	// TODO: add rhs and lhs

	public OperatorNotSupported(GioviToken op) {
		super("The operator '" + op + "(" + op.label + ")" + "' is not applicable to given types");
	}

}
