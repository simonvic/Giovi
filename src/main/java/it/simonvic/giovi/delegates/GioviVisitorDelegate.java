package it.simonvic.giovi.delegates;

import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.lang.GioviParserVisitResult;

public abstract class GioviVisitorDelegate extends GioviParserBaseVisitor<GioviParserVisitResult> {

	protected final GioviVisitor gioviVisitor;

	public GioviVisitorDelegate(GioviVisitor gioviVisitor) {
		this.gioviVisitor = gioviVisitor;
	}

}
