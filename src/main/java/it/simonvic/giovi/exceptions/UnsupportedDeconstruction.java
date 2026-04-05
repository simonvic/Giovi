package it.simonvic.giovi.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

import giovi.GioviParserVisitResult;
import giovi.Tuple;

public class UnsupportedDeconstruction extends GioviException {

	public UnsupportedDeconstruction(
		GioviParserVisitResult value,
		ParserRuleContext iteratorCtx,
		Exception cause
	) {
		super(cause, "Can't deconstruct " + value + " into " + iteratorCtx.getText());
	}

	public UnsupportedDeconstruction(
		Tuple value,
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
		Tuple value,
		ParserRuleContext iteratorCtx
	) {
		super("Can't deconstruct " + value + " into " + iteratorCtx.getText());
	}

	public UnsupportedDeconstruction(Tuple value, Exception cause) {
		super(cause, "Can't deconstruct " + value);
	}

}

