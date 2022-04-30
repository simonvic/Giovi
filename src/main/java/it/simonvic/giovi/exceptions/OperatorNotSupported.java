package it.simonvic.giovi.exceptions;

public class OperatorNotSupported extends RuntimeException {
	public OperatorNotSupported(String operator) {
		super("The operator '" + operator + "' is not aplicable to given types");
	}
}
