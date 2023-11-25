package it.simonvic.giovi.lang;

import java.util.HashMap;
import java.util.Map;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.VariableAlreadyDeclaredException;
import it.simonvic.giovi.exceptions.VariableNotDeclaredException;
import it.simonvic.giovi.lang.functions.FunctionScope;
import it.simonvic.giovi.lang.functions.GioviFunction;
import it.simonvic.giovi.lang.values.GioviStruct;
import it.simonvic.giovi.lang.values.GioviValue;

public class Scope {


	private final Scope parentScope;

	public Scope(Scope parentScope) {
		this.parentScope = parentScope;
	}

	public Scope() {
		this.parentScope = null;
	}

	public Scope getParentScope() {
		return parentScope;
	}


	public boolean shouldSkip() {
		if (this instanceof LoopScope s) {
			return s.shouldSkip();
		} else if (parentScope != null) {
			return parentScope.shouldSkip();
		}
		return false;
	}

	public void doSkip() {
		if (this instanceof LoopScope s) {
			s.doSkip();
		} else if (parentScope != null) {
			parentScope.doSkip();
		} else {
			throw new GioviException("Skip statement in non-loop scope");
		}
	}

	public boolean shouldBreak() {
		if (this instanceof LoopScope s) {
			return s.shouldBreak();
		} else if (parentScope != null) {
			return parentScope.shouldBreak();
		}
		return false;
	}

	public void doBreak() {
		if (this instanceof LoopScope l) {
			l.doBreak();
		} else if (parentScope != null) {
			parentScope.doBreak();
		} else {
			throw new GioviException("Break statement in non-loop scope");
		}
	}


	public boolean shouldReturn() {
		if (this instanceof FunctionScope s) {
			return s.shouldReturn();
		} else if (parentScope != null) {
			return parentScope.shouldReturn();
		}
		return false;
	}

	public void doReturn(GioviValue<?> returnValue) {
		if (this instanceof FunctionScope s) {
			s.doReturn(returnValue);
		} else if (parentScope != null) {
			parentScope.doReturn(returnValue);
		} else {
			throw new GioviException("Return statement in non-function scope");
		}
	}

	public GioviValue<?> getReturnValue() {
		if (this instanceof FunctionScope s) {
			return s.getReturnValue();
		} else if (parentScope != null) {
			return parentScope.getReturnValue();
		} else {
			throw new GioviException("Return not available in non-function scope");
		}
	}


	public boolean isInLoop() {
		return this instanceof LoopScope || (parentScope != null && parentScope.isInLoop());
	}

	public boolean isInFunction() {
		return this instanceof FunctionScope || (parentScope != null && parentScope.isInFunction());
	}


	////////////////////////////////////////////////
	// Variables

	private final Map<String, GioviValue<?>> variables = new HashMap<>();

	public void deleteVariable(String identifier) {
		variables.remove(identifier);
	}

	public void declare(String identifier, GioviValue<?> value) {
		if (isDeclaredLocalVariable(identifier)) throw new VariableAlreadyDeclaredException(identifier);
		variables.put(identifier, value);
	}

	public void setVariableValue(String identifier, GioviValue<?> value) {
		Scope.setVariableValue(this, identifier, value);
	}

	private static void setVariableValue(Scope scope, String identifier, GioviValue<?> value) {
		if (scope == null) throw new VariableNotDeclaredException(identifier);
		if (scope.isDeclaredLocalVariable(identifier)) {
			scope.variables.get(identifier).box(value);
		} else {
			Scope.setVariableValue(scope.parentScope, identifier, value);
		}
	}

	public GioviValue<?> getVariableValue(String identifier) {
		return Scope.getVariableValue(this, identifier);
	}

	public boolean isDeclaredVariable(String identifier) {
		return Scope.isDeclaredVariable(this, identifier);
	}

	private GioviValue<?> getLocalVariableValue(String identifier) {
		return variables.get(identifier);
	}

	private boolean isDeclaredLocalVariable(String identifier) {
		return variables.containsKey(identifier);
	}

	private static boolean isDeclaredVariable(Scope scope, String identifier) {
		if (scope == null) return false;
		return scope.isDeclaredLocalVariable(identifier)
			|| Scope.isDeclaredVariable(scope.parentScope, identifier);
	}

	private static GioviValue<?> getVariableValue(Scope scope, String identifier) {
		if (scope == null) throw new VariableNotDeclaredException(identifier);
		return scope.isDeclaredLocalVariable(identifier)
			? scope.getLocalVariableValue(identifier)
			: Scope.getVariableValue(scope.parentScope, identifier);

	}


	////////////////////////////////////////////////
	// Constants

	private final Map<String, GioviValue<?>> constants = new HashMap<>();

	public void deleteConstants(String identifier) {
		constants.remove(identifier);
	}

	public void declareConstant(String identifier, GioviValue<?> value) {
		if (isDeclaredLocalConstant(identifier)) throw new VariableAlreadyDeclaredException(identifier);
		constants.put(identifier, value);
	}

	public GioviValue<?> getConstantValue(String identifier) {
		return Scope.getConstantValue(this, identifier);
	}

	public boolean isDeclaredConstant(String identifier) {
		return Scope.isDeclaredConstant(this, identifier);
	}

	private GioviValue<?> getLocalConstantValue(String identifier) {
		return constants.get(identifier);
	}

	private boolean isDeclaredLocalConstant(String identifier) {
		return constants.containsKey(identifier);
	}

	private static boolean isDeclaredConstant(Scope scope, String identifier) {
		if (scope == null) return false;
		return scope.isDeclaredLocalConstant(identifier)
			|| Scope.isDeclaredConstant(scope.parentScope, identifier);
	}

	private static GioviValue<?> getConstantValue(Scope scope, String identifier) {
		if (scope == null) throw new VariableNotDeclaredException(identifier);
		return scope.isDeclaredLocalConstant(identifier)
			? scope.getLocalConstantValue(identifier)
			: Scope.getConstantValue(scope.parentScope, identifier);
	}

	////////////////////////////////////////////////
	// Functions

	private final Map<String, GioviFunction> functionDefinitions = new HashMap<>();

	public void define(String identifier, GioviFunction function) {
		if (isDefinedFunction(identifier)) {
			throw new GioviException("Function already defined: " + identifier);
		}
		functionDefinitions.put(identifier, function);
	}

	public GioviFunction getDefinitionFunction(String identifier) {
		return Scope.getDefinitionFunction(this, identifier);
	}

	public boolean isDefinedFunction(String identifier) {
		return Scope.isDefinedFunction(this, identifier);
	}

	private boolean isDefinedLocalFunction(String identifier) {
		return functionDefinitions.containsKey(identifier);
	}


	private static boolean isDefinedFunction(Scope scope, String identifier) {
		if (scope == null) return false;
		return scope.isDefinedLocalFunction(identifier)
			|| Scope.isDefinedFunction(scope.parentScope, identifier);
	}

	private GioviFunction getDefinitionLocalFunction(String identifier) {
		return functionDefinitions.get(identifier);
	}

	private static GioviFunction getDefinitionFunction(Scope scope, String identifier) {
		if (scope == null) throw new GioviException("Function not defined: " + identifier);
		return scope.isDefinedLocalFunction(identifier)
			? scope.getDefinitionLocalFunction(identifier)
			: Scope.getDefinitionFunction(scope.parentScope, identifier);

	}

	////////////////////////////////////////////////
	// Structs

	private final Map<String, GioviStruct> structDefinitions = new HashMap<>();

	public void define(String identifier, GioviStruct struct) {
		if (isDefinedStruct(identifier)) {
			throw new GioviException("Struct already defined: " + identifier);
		}
		structDefinitions.put(identifier, struct);
	}

	public GioviStruct getDefinitionStruct(String identifier) {
		return Scope.getDefinitionStruct(this, identifier);
	}

	public boolean isDefinedStruct(String identifier) {
		return Scope.isDefinedStruct(this, identifier);
	}

	private boolean isDefinedLocalStruct(String identifier) {
		return structDefinitions.containsKey(identifier);
	}


	private static boolean isDefinedStruct(Scope scope, String identifier) {
		if (scope == null) return false;
		return scope.isDefinedLocalStruct(identifier)
			|| Scope.isDefinedStruct(scope.parentScope, identifier);
	}

	private GioviStruct getDefinitionLocalStruct(String identifier) {
		return structDefinitions.get(identifier);
	}

	private static GioviStruct getDefinitionStruct(Scope scope, String identifier) {
		if (scope == null) throw new GioviException("Struct not defined: " + identifier);
		return scope.isDefinedLocalStruct(identifier)
			? scope.getDefinitionLocalStruct(identifier)
			: Scope.getDefinitionStruct(scope.parentScope, identifier);

	}
}
