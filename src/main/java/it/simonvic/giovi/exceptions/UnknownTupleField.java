package it.simonvic.giovi.exceptions;

import giovi.TupleNamed;

public class UnknownTupleField extends Exception {

	public UnknownTupleField(TupleNamed tuple, String identifier) {
		super("Unknown field " + identifier + " in " + tuple);
	}

}

