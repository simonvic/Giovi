package it.simonvic.giovi.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviValue;

public class UnsupportedPattern extends GioviException {

	public UnsupportedPattern(ParserRuleContext patternCtx, GioviParserVisitResult iterated) {
		String iteratedText = (iterated instanceof GioviValue<?> v)
			? v.getGioviType().toString()
			: iterated.toString();
		super("Can't interate with " + patternCtx.getText() + " on " + iteratedText);
	}


}
