package it.simonvic.giovi.exceptions;

import giovi.GTuple;

public class IncompatibleFieldsCount extends RedirectionException {

	public IncompatibleFieldsCount(GTuple expected, GTuple got) {
		super(
			"Different fields count.\n"
				+ "Expecting:\t" + expected.getFieldsCount() + " for " + expected + "\n"
				+ "but got:\t" + got.getFieldsCount() + " from " + got
		);
	}

}
