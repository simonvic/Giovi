package it.simonvic.giovi.lang;

import java.util.HashMap;
import java.util.Map;

import it.simonvic.giovi.exceptions.*;
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

	// TODO: wtf! Remove this crap. Scope should not know about LoopScope or FunctionScope

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


	//////////////////////////////////////////////////////////////////////////////////////////////// Variables

	private final Map<String, GioviValue<?>> variables = new HashMap<>();

	public void declare(String identifier, GioviValue<?> value) {
		if (variables.containsKey(identifier)) {
			throw new VariableAlreadyDeclared(identifier);
		}
		variables.put(identifier, value);
	}

	public boolean isDeclaredVariable(String identifier) {
		return variables.containsKey(identifier)
			|| (parentScope != null && parentScope.isDeclaredVariable(identifier));
	}

	public void setVariableValue(String identifier, GioviValue<?> value) {
		if (variables.containsKey(identifier)) {
			variables.get(identifier).box(value);
		} else {
			if (parentScope == null) {
				throw new VariableNotDeclared(identifier);
			}
			parentScope.setVariableValue(identifier, value);
		}
	}

	public GioviValue<?> getVariableValue(String identifier) {
		if (variables.containsKey(identifier)) {
			return variables.get(identifier);
		}
		if (parentScope == null) {
			throw new VariableNotDeclared(identifier);
		}
		return parentScope.getVariableValue(identifier);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// Constants

	private final Map<String, GioviValue<?>> constants = new HashMap<>();

	public void declareConstant(String identifier, GioviValue<?> value) {
		if (constants.containsKey(identifier)) {
			throw new ConstantAlreadyDeclared(identifier);
		}
		constants.put(identifier, value);
	}

	public boolean isDeclaredConstant(String identifier) {
		return constants.containsKey(identifier)
			|| (parentScope != null && parentScope.isDeclaredConstant(identifier));
	}

	public GioviValue<?> getConstantValue(String identifier) {
		if (constants.containsKey(identifier)) {
			return constants.get(identifier);
		}
		if (parentScope == null) {
			throw new VariableNotDeclared(identifier);
		}
		return parentScope.getConstantValue(identifier);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Functions

	private final Map<String, GioviFunction> functionSignatures = new HashMap<>();

	public void declare(String identifier, GioviFunction signature) {
		if (functionSignatures.containsKey(identifier)) {
			throw new FunctionAlreadyDeclared(signature);
		}
		functionSignatures.put(identifier, signature);
	}

	public boolean isDeclaredFunction(String identifier) {
		return functionSignatures.containsKey(identifier)
			|| (parentScope != null && parentScope.isDeclaredFunction(identifier));
	}

	public GioviFunction getFunctionSignature(String identifier) {
		if (functionSignatures.containsKey(identifier)) {
			return functionSignatures.get(identifier);
		}
		if (parentScope == null) {
			throw new FunctionNotDeclared(identifier);
		}
		return parentScope.getFunctionSignature(identifier);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// Structs

	private final Map<String, GioviStructDefinition> structDefinitions = new HashMap<>();

	public void define(String identifier, GioviStructDefinition definition) {
		if (structDefinitions.containsKey(identifier)) {
			throw new StructAlreadyDefined(identifier);
		}
		structDefinitions.put(identifier, definition);
	}

	public boolean isDefinedStruct(String identifier) {
		return structDefinitions.containsKey(identifier)
			|| (parentScope != null && parentScope.isDefinedStruct(identifier));
	}

	public GioviStructDefinition getDefinitionStruct(String identifier) {
		if (structDefinitions.containsKey(identifier)) {
			return structDefinitions.get(identifier);
		}
		if (parentScope == null) {
			throw new StructNotDefined(identifier);
		}
		return parentScope.getDefinitionStruct(identifier);
	}

}
