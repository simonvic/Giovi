package it.simonvic.giovi.visitors;

import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.lang.GioviParserVisitResult;

public abstract class GioviHelperVisitor extends GioviParserBaseVisitor<GioviParserVisitResult> {

	protected final GioviVisitor gioviVisitor;

	public GioviHelperVisitor(GioviVisitor gioviVisitor) {
		this.gioviVisitor = gioviVisitor;
	}
}
