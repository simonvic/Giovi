package it.simonvic.giovi.util;

import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.Scope;
import it.simonvic.giovi.lang.values.GioviText;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TextProcessor {


	private String text;

	public TextProcessor(String text) {
		this.text = text;
	}

	public GioviText collect() {
		return new GioviText(text);
	}

	public static TextProcessor of(String text) {
		return new TextProcessor(text);
	}

	public TextProcessor trimDelimiters(int delimitersCount) {
		this.text = trimDelimiters(this.text, delimitersCount);
		return this;
	}

	public TextProcessor injectVariables(Scope scope) {
		this.text = injectVariables(this.text, scope);
		return this;
	}

	public TextProcessor injectExpressions(GioviVisitor visitor) {
		this.text = injectExpressions(this.text, visitor);
		return this;
	}

	public TextProcessor trimIncidentalWhitespaces() {
		this.text = trimIncidentalWhiteSpaces(this.text);
		return this;
	}


	public static String trimDelimiters(String text, int delimitersCount) {
		return text.substring(delimitersCount, text.length() - delimitersCount);
	}

	public static String injectVariables(String text, Scope scope) {
		return Pattern
			.compile("\\$[a-zA-Z]\\w*")
			.matcher(text)
			.replaceAll(matchResult
				-> scope.getVariableValue(matchResult.group().substring(1)).toString());
	}

	public static String injectExpressions(String text, GioviVisitor visitor) {
		return Pattern
			.compile("\\$\\{([^{\\$]*)}")
			.matcher(text)
			.replaceAll(matchResult -> {
				GioviLexer lexer = new GioviLexer(CharStreams.fromString(matchResult.group(1)));
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				GioviParser parser = new GioviParser(tokens);
				GioviObject expression = visitor.visit(parser.expression());
				return expression != null
					? expression.toString()
					: "";
			});
	}

	public static String trimIncidentalWhiteSpaces(String text) {
		if (text.isEmpty()) return text;

		// find leader character used for indentation
		char indentationLeader = indentationLeader(text);

		// get minimum count of incidental indentation whitespaces
		int minIncidentalWhiteSpace = text.lines()
			.skip(1)
			.mapToInt(line -> TextProcessor.startingIndentationCharCount(line, indentationLeader))
			.min()
			.orElse(0);

		StringBuilder s = new StringBuilder();
		List<String> linesList = new ArrayList<>(text.lines().toList());
		switch (linesList.size()) {
			case 0:
				return "";
			case 1:
				return linesList.get(0);
		}

		// if last line (i.e. line with delimiter) is empty, skip it
//		int lastLineIndex = linesList.size() - 1;
//		if (linesList.get(lastLineIndex).isEmpty()) {
//			linesList.remove(lastLineIndex);
//		}

		// if first line (i.e. line with delimiter) is empty, skip it
		if (linesList.get(0).isEmpty()) {
			linesList.remove(0);
		}

		// build text and remove all incidental indentation characters
		linesList.forEach(line -> {
			s.append(line.replaceFirst(indentationLeader + "{" + minIncidentalWhiteSpace + "}", ""));
			s.append("\n");
		});

		// delete last new line if last line (i.e. line with delimiter) is not empty
		if (text.charAt(text.length() - 1) != '\n') {
			s.deleteCharAt(s.length() - 1);
		}
		return s.toString();
	}

	private static int startingIndentationCharCount(String line, char indentationLeader) {
		if (line.length() == 0) return 0;
		int count = 0;
		char firstChar = line.charAt(0);
		if (firstChar != '\t' && firstChar != ' ') {
			return 0;
		}
		for (char c : line.toCharArray()) {
			if (c != indentationLeader) {
				return count;
			}
			count++;
		}
		return count;
	}

	public static char indentationLeader(String text) {
		String[] lines = text.split("\n");
		if (lines.length <= 1 || lines[1].length() == 0) return '\t';
		return lines[1].charAt(0) == ' ' ? ' ' : '\t';
	}

}
