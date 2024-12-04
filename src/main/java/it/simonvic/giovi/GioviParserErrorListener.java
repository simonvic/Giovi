package it.simonvic.giovi;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import it.simonvic.giovi.exceptions.GioviException;

public class GioviParserErrorListener extends BaseErrorListener {

	private static final int CONTEXT_SIZE = 2;
	private final String code;

	public GioviParserErrorListener(String code) {
		this.code = code;
	}

	@Override
	public void syntaxError(
		Recognizer<?, ?> recognizer,
		Object offendingSymbol,
		int line,
		int charPositionInLine,
		String msg,
		RecognitionException e
	) {
		String badLine = code.lines().toList().get(line - 1);
		var errorLineBuilder = new StringBuilder();
		errorLineBuilder.repeat(" ", charPositionInLine);
		errorLineBuilder.append("^ " + msg);
		throw new GioviException(e, msg + "\n" + badLine);
	}

}

