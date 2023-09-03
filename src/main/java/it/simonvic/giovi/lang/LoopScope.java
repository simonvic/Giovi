package it.simonvic.giovi.lang;

public class LoopScope extends Scope {

	private boolean shouldBreak = false;
	private boolean shouldSkip = false;

	public LoopScope(Scope currentScope) {
		super(currentScope);
	}

	@Override
	public void doBreak() {
		shouldBreak = true;
	}

	@Override
	public void doSkip() {
		shouldSkip = true;
	}

	@Override
	public boolean shouldSkip() {
		return shouldSkip;
	}

	@Override
	public boolean shouldBreak() {
		return shouldBreak;
	}
}
