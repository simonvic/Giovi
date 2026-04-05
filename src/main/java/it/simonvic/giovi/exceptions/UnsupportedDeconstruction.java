package it.simonvic.giovi.exceptions;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import giovi.GioviParserVisitResult;
import giovi.Record;
import giovi.Tuple;
import giovi.lang.GioviValue;
import it.simonvic.giovi.antlr.GioviParser.IteratorNestedProductContext;
import it.simonvic.giovi.antlr.GioviParser.NestedIteratorContext;
import it.simonvic.giovi.antlr.GioviParser.UnaryIteratorContext;

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

	public UnsupportedDeconstruction(
		GioviValue<?> value,
		List<UnaryIteratorContext> iteratorsCtx
	) {
		super("Can't deconstruct " + value + " into " + iteratorsCtx);
	}

	public UnsupportedDeconstruction(
		Record value,
		UnaryIteratorContext iteratorCtx,
		Exception cause
	) {
		super(cause, "Can't deconstruct " + value + " into " + iteratorCtx);
	}

	public UnsupportedDeconstruction(
		Tuple value,
		NoSuchField cause
	) {
		super(cause, "Can't deconstruct " + value);
	}

	public UnsupportedDeconstruction(Tuple value, List<NestedIteratorContext> iteratorsCtx) {
		super("Can't deconstruct " + value + " into " + iteratorsCtx);
	}

	public UnsupportedDeconstruction(Record value, NestedIteratorContext iteratorsCtx) {
		super("Can't deconstruct " + value + " into " + iteratorsCtx);
	}

	public UnsupportedDeconstruction(
		Record value,
		IteratorNestedProductContext iteratorCtx,
		Exception cause
	) {
		super(cause, "Can't deconstruct " + value + " into " + iteratorCtx);
	}


}

