package it.simonvic.giovi.utils;

import java.util.Optional;

import giovi.GioviParserVisitResult;
import giovi.Module.NativeModule;
import it.simonvic.giovi.GioviVisitor;

public abstract class TestVisitor extends GioviVisitor {

	public TestVisitor() {
		super(new NativeModule("test", GioviVisitor.ROOT_MODULE));
	}

	public Optional<GioviParserVisitResult> lookup(String identifier) {
		return getCurrentScope().getSymbol(identifier);
	}

}
