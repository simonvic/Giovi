package it.simonvic.giovi.lang;

import it.simonvic.giovi.lang.values.GioviValue;

public class FunctionScope extends Scope {

	private GioviValue<?> returnValue;
	private boolean shouldReturn = false;

	public FunctionScope(Scope parentScope) {
		super(parentScope);
	}

	@Override
	public boolean shouldReturn() {
		return shouldReturn;
	}

	@Override
	public void doReturn(GioviValue<?> returnValue) {
		this.returnValue = returnValue;
		this.shouldReturn = true;
	}

	@Override
	public GioviValue<?> getReturnValue() {
		return returnValue;
	}

	@Override
	public boolean isInFunction() {
		return true;
	}

}
