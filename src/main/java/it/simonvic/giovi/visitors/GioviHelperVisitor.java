package it.simonvic.giovi.visitors;

import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.lang.GioviObject;

public abstract class GioviHelperVisitor extends GioviParserBaseVisitor<GioviObject> {

	protected final GioviVisitor gioviVisitor;

	public GioviHelperVisitor(GioviVisitor gioviVisitor) {
		this.gioviVisitor = gioviVisitor;
	}
}
