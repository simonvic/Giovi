package it.simonvic.giovi.lang;

public class TextBlock extends Text {

	public TextBlock(String text) {
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
		this.text = trimIncidentalWhiteSpaces(this.text);
	}

	public static String trimIncidentalWhiteSpaces(String text) {
		int minIncidentalWhiteSpace = text.lines()
				.skip(1)
				.mapToInt(TextBlock::startingWhitespacesCount)
				.min().orElse(0);

		StringBuilder s = new StringBuilder();
		text.lines().forEach(line -> {
			if (line.length() > minIncidentalWhiteSpace) {
//				@TODO trim first character used
				s.append(line.substring(minIncidentalWhiteSpace)).append('\n');
			}
		});
		return s.toString();
	}

	public static int startingWhitespacesCount(String text) {
		if (text.length() < 1) return 0;
		char firstChar = text.charAt(0);
		if (!Character.isWhitespace(firstChar)) {
			return 0;
		}
		int count = 0;
		for (char c : text.toCharArray()) {
			if (c == firstChar) {
				count++;
			} else {
				break;
			}
		}
		return count;
	}
}
