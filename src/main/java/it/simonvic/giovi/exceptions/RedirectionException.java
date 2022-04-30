package it.simonvic.giovi.exceptions;

public class RedirectionException extends RuntimeException {

	public RedirectionException() {
	}

	public RedirectionException(String message) {
		super(message);
	}

	public RedirectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RedirectionException(Throwable cause) {
		super(cause);
	}

	public RedirectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
