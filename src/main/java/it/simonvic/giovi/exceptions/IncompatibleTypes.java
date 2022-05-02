package it.simonvic.giovi.exceptions;

public class IncompatibleTypes extends RuntimeException {
	public IncompatibleTypes(Class<?> type, Class<?> newType) {
		super("Incompatible types. Can't assign " + newType + " to " + type);
	}
}
