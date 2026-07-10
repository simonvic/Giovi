package it.simonvic.giovi.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

import giovi.GTuple;
import giovi.GioviParserVisitResult;

public class UnsupportedDeconstruction extends GioviException {

	public UnsupportedDeconstruction(
		GioviParserVisitResult value,
		ParserRuleContext iteratorCtx,
		Exception cause
	) {
		super(cause, "Can't deconstruct " + value + " into " + iteratorCtx.getText());
	}

	public UnsupportedDeconstruction(
		GTuple value,
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
		GTuple value,
		ParserRuleContext iteratorCtx
	) {
		super("Can't deconstruct " + value + " into " + iteratorCtx.getText());
	}

	public UnsupportedDeconstruction(GTuple value, Exception cause) {
		super(cause, "Can't deconstruct " + value);
	}

}

