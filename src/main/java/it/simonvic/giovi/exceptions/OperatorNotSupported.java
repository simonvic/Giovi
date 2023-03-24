package it.simonvic.giovi.exceptions;

public class OperatorNotSupported extends GioviException {
	public OperatorNotSupported(String operator) {
		super("The operator '" + operator + "' is not applicable to given types");
	}
}
