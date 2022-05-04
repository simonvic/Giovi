package it.simonvic.giovi.lang;

public class LoopScope extends Scope {

	private boolean shouldBreak = false;
	private boolean shouldContinue = false;

	public LoopScope(Scope currentScope) {
		super(currentScope);
	}

	@Override
	public void doBreak() {
		shouldBreak = true;
	}

	@Override
	public void doContinue() {
		shouldContinue = true;
	}

	@Override
	public void onContinued() {
		shouldContinue = false;
	}

	@Override
	public boolean shouldContinue() {
		return shouldContinue;
	}

	@Override
	public boolean shouldBreak() {
		return shouldBreak;
	}
}
