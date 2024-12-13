package it.simonvic.giovi.exceptions;

import org.antlr.v4.runtime.RecognitionException;

public class ParseException extends RuntimeException {

    public ParseException(RecognitionException cause, String msg) {
        super(msg, cause);
    }

}
