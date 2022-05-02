package it.simonvic.giovi.lang.text;

public class ExactTextBlock extends ExactText {

	public ExactTextBlock(String text) {
		super(text);
	}

	@Override
	protected void process() {
		super.process();
		trimIncidentalWhiteSpaces();
	}

	@Override
	protected void trimDelimiters() {
		this.text = this.text.substring(2, this.text.length() - 2);
	}

	protected void trimIncidentalWhiteSpaces() {
		this.text = TextBlock.trimIncidentalWhiteSpaces(this.text);
	}


}
