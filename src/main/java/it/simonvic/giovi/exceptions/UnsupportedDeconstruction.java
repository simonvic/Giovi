package it.simonvic.giovi.exceptions;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.antlr.GioviParser.NestedIteratorContext;

public class UnsupportedDeconstruction extends GioviException {

	public UnsupportedDeconstruction(
		GioviParserVisitResult value,
		ParserRuleContext iteratorCtx,
		Exception cause
	) {
		super(cause, "Can't deconstruct " + value + " into " + iteratorCtx.getText());
	}

	public UnsupportedDeconstruction(
		GioviParserVisitResult value,
		ParserRuleContext iteratorCtx
	) {
		super("Can't deconstruct " + value + " into " + iteratorCtx.getText());
	}

	public UnsupportedDeconstruction(
		GioviParserVisitResult value,
		List<NestedIteratorContext> iteratorsCtx
	) {
		super("Can't deconstruct " + value + " into " + iteratorsCtx);
	}


}

