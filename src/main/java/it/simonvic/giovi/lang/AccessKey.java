package it.simonvic.giovi.lang;

public class AccessKey implements GioviParserVisitResult {
	private final String s;

	public AccessKey(String s) {
		this.s = s;
	}

	public String get() {
		return s;
	}

	@Override
	public String toString() {
		return s;
	}
}
