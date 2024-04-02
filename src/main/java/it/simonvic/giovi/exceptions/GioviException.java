package it.simonvic.giovi.exceptions;

public class GioviException extends RuntimeException {
	public GioviException() {
	}

	public GioviException(String message) {
		super(message);
	}

	public GioviException(Throwable cause, String message) {
		super(message, cause);
	}

	public GioviException(Throwable cause) {
		super(cause);
	}

	public GioviException(String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
