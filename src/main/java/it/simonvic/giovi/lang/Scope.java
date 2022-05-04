package it.simonvic.giovi.lang;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.VariableAlreadyDefinedException;
import it.simonvic.giovi.exceptions.VariableNotDefinedException;
import it.simonvic.giovi.lang.values.*;

import java.util.HashMap;

public class Scope {

	private final HashMap<String, GioviValue<?>> variables = new HashMap<>();
	private final Scope parentScope;

	public Scope(Scope parentScope) {
		this.parentScope = parentScope;
	}

	public Scope() {
		this.parentScope = null;
	}


	public void onContinued() {
	}

	public boolean shouldContinue() {
		return false;
	}

	public boolean shouldBreak() {
		return false;
	}

	public void doBreak() {
		if (this instanceof LoopScope l) {
			l.doBreak();
		} else if (parentScope != null) {
			parentScope.doBreak();
		}
		throw new GioviException("Break statement in non-loop scope");
	}

	public void doContinue() {
		if (this instanceof LoopScope l) {
			l.doContinue();
		} else if (parentScope != null) {
			parentScope.doContinue();
		}
		throw new GioviException("Continue statement in non-loop scope");
	}

	public boolean isInLoop() {
		if (this instanceof LoopScope l) {
			return true;
		} else if (parentScope != null) {
			return parentScope.isInLoop();
		}
		return false;
	}

	public void delete(String identifier) {
		variables.remove(identifier);
	}

	public void declare(String identifier, GioviValue<?> value) {
		if (isDefinedVariable(identifier)) throw new VariableAlreadyDefinedException(identifier);
		variables.put(identifier, value);
	}

	public void setVariableValue(String identifier, GioviValue<?> value) {
		Scope.setVariableValue(this, identifier, value);
	}

	private static void setVariableValue(Scope scope, String identifier, GioviValue<?> value) {
		if (scope == null) throw new VariableNotDefinedException(identifier);
		if (scope.isDefinedLocalVariable(identifier)) {
			scope.variables.get(identifier).set(value);
		} else {
			Scope.setVariableValue(scope.parentScope, identifier, value);
		}
	}

	public GioviValue<?> getVariableValue(String identifier) {
		return Scope.getVariableValue(this, identifier);
	}

	public boolean isDefinedVariable(String identifier) {
		return Scope.isDefinedVariable(this, identifier);
	}

	private GioviValue<?> getLocalVariableValue(String identifier) {
		return variables.get(identifier);
	}

	private boolean isDefinedLocalVariable(String identifier) {
		return variables.containsKey(identifier);
	}

	private static boolean isDefinedVariable(Scope scope, String identifier) {
		if (scope == null) return false;
		return scope.isDefinedLocalVariable(identifier) || Scope.isDefinedVariable(scope.parentScope, identifier);
	}

	private static GioviValue<?> getVariableValue(Scope scope, String identifier) {
		if (scope == null) throw new VariableNotDefinedException(identifier);
		return scope.isDefinedLocalVariable(identifier)
			? scope.getLocalVariableValue(identifier)
			: Scope.getVariableValue(scope.parentScope, identifier);

	}

	public Scope getParentScope() {
		return parentScope;
	}
}
