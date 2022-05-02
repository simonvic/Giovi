package it.simonvic.giovi.lang.text;

import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.lang.Block;

import java.util.regex.Pattern;

public class Text extends TextBase {

	public Text(String text) {
		super(text);
	}

	@Override
	protected void process() {
		super.process();
		injectVariables(GioviVisitor.getCurrentScope());
	}

	@Override
	protected void trimDelimiters() {
		this.text = this.text.substring(1, this.text.length() - 1);
	}

	public void injectVariables(Block block) {
		this.text = injectVariables(this.text, block);
	}

	//lol...
	private static String injectVariables(String text, Block block) {
		String s = Pattern
				.compile("\\$[a-zA-Z]\\w*")
				.matcher(text)
				.replaceAll(matchResult
						-> block.getVariableValue(matchResult.group().substring(1)).toString());
		return Pattern
				.compile("\\$\\{([^{\\$]*)}")
				.matcher(s)
				.replaceAll(matchResult
						-> block.getVariableValue(matchResult.group(1)).toString());
	}
}
