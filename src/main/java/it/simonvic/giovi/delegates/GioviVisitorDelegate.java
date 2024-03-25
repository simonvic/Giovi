package it.simonvic.giovi.delegates;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;

public abstract class GioviVisitorDelegate extends GioviParserBaseVisitor<GioviParserVisitResult> {

	protected final GioviVisitor gioviVisitor;

	public GioviVisitorDelegate(GioviVisitor gioviVisitor) {
		this.gioviVisitor = gioviVisitor;
	}

}
