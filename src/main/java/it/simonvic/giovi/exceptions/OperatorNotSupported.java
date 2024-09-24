package it.simonvic.giovi.exceptions;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.GioviToken;

public class OperatorNotSupported extends GioviException {

	public OperatorNotSupported(
		GioviParserVisitResult lhs,
		GioviToken op,
		GioviParserVisitResult rhs) {
		super("" + lhs + " doesn't support operator " + op + "(" + op.label + ") with " + rhs);
	}

	public OperatorNotSupported(GioviParserVisitResult r, GioviToken op) {
		super("The operator '" + op + "(" + op.label + ")" + "' is not supported by " + r);
	}

}
