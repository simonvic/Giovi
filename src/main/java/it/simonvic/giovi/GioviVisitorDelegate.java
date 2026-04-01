package it.simonvic.giovi;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.lang.Scope;

public abstract class GioviVisitorDelegate extends GioviParserBaseVisitor<GioviParserVisitResult> {

	protected final GioviVisitor gioviVisitor;

	public GioviVisitorDelegate(GioviVisitor gioviVisitor) {
		this.gioviVisitor = gioviVisitor;
	}

	protected Scope getCurrentScope() {
		return gioviVisitor.getCurrentScope();
	}

	protected void setCurrentScope(Scope scope) {
		gioviVisitor.setCurrentScope(scope);
	}

	protected void enterLoopScope() {
		gioviVisitor.enterLoopScope();
	}

	protected void enterFunctionScope() {
		gioviVisitor.enterFunctionScope();
	}

	protected void exitScope() {
		gioviVisitor.exitScope();
	}


}
