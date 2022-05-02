package it.simonvic.giovi.lang.text;

public abstract class TextBase {

	protected String text;

	public TextBase(String text) {
		this.text = text;
		process();
	}

	protected void process() {
		trimDelimiters();
	}

	protected abstract void trimDelimiters();

	@Override
	public String toString() {
		return this.text;
	}
}
