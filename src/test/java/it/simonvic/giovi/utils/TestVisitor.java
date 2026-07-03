package it.simonvic.giovi.utils;

import java.util.Optional;

import giovi.Module.NativeModule;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.VarD;

public abstract class TestVisitor extends GioviVisitor {

	public TestVisitor() {
		super(new NativeModule("test", GioviVisitor.ROOT_MODULE));
	}

	public Optional<VarD<?>> lookup(String identifier) {
		return getCurrentScope().getVariable(identifier);
	}

}
