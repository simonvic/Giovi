package it.simonvic.giovi.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviValue;

public class UnsupportedIterator extends GioviException {

	public UnsupportedIterator(ParserRuleContext iteratorCtx, GioviParserVisitResult iterated) {
		String iteratedText = (iterated instanceof GioviValue<?> v)
			? v.getGioviType().toString()
			: iterated.toString();
		super("Can't interate with " + iteratorCtx.getText() + " on " + iteratedText);
	}


}
