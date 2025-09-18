package it.simonvic.giovi.lsp;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class SyntaxErrorListener extends BaseErrorListener {

	public static record SyntaxError(
		int startLine, int startCol, int endLine, int endCol, String msg
	) {

		public Range getRange() {
			return new Range(
				new Position(startLine, startCol),
				new Position(endLine, endCol));
		}

	}

	public List<SyntaxError> errors = new ArrayList<>();

	@Override
	public void syntaxError(
		Recognizer<?, ?> recognizer,
		Object offendingSymbol,
		int line,
		int charPositionInLine,
		String msg,
		RecognitionException e
	) {
		var token = (CommonToken) offendingSymbol;
		int startLine = token.getLine();
		int startCol = token.getCharPositionInLine();
		errors.add(new SyntaxError(startLine, startCol, startLine, startCol, msg));
	}

	public List<SyntaxError> getErrors() {
		return errors;
	}

}

