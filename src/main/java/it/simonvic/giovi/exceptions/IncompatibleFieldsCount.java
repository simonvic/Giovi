package it.simonvic.giovi.exceptions;

import giovi.Tuple;

public class IncompatibleFieldsCount extends RedirectionException {

	public IncompatibleFieldsCount(Tuple expected, Tuple got) {
		super(
			"Different fields count.\n"
				+ "Expecting:\t" + expected.getFieldsCount() + " for " + expected + "\n"
				+ "but got:\t" + got.getFieldsCount() + " from " + got
		);
	}

}
