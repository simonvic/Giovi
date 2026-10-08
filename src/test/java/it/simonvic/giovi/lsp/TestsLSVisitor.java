package it.simonvic.giovi.lsp;

import java.util.Optional;

import giovi.Module.NativeModule;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.lsp.LSVisitResult.Type;

public abstract class TestsLSVisitor extends LSVisitor {

	public TestsLSVisitor() {
		super(new NativeModule("test", GioviVisitor.ROOT_MODULE), null);
	}

	public Optional<Type> lookup(String identifier) {
		return getCurrentEnv().lookup(identifier);
	}

}
