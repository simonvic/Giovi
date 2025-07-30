package it.simonvic.giovi.exceptions;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.GioviToken;

public class OperatorNotSupported extends GioviException {

	public OperatorNotSupported(
		GioviParserVisitResult lhs,
		GioviToken op,
		GioviParserVisitResult rhs
	) {
		super("""
			Operator %s (%s) not supported
				by:   %s
				with: %s
			""".formatted(op, op.label, lhs, rhs));
	}

	public OperatorNotSupported(GioviParserVisitResult r, GioviToken op) {
		super("The operator '" + op + "(" + op.label + ")" + "' is not supported by " + r);
	}

}
