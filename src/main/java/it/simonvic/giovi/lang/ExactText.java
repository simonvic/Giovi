package it.simonvic.giovi.lang;

import it.simonvic.giovi.GioviVisitor;

public class ExactText extends TextBase {

	public ExactText(String text) {
		super(text);
	}

	@Override
	protected void trimDelimiters() {
		this.text = this.text.substring(1, this.text.length() - 1);
	}

}
