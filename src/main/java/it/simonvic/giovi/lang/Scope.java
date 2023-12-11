package it.simonvic.giovi.lang;

import java.util.HashMap;
import java.util.Map;

import it.simonvic.giovi.exceptions.*;
import it.simonvic.giovi.lang.values.GioviValue;

public class Scope {


	private final Scope parent;

	public Scope(Scope parent) {
		this.parent = parent;
	}

	public Scope() {
		this.parent = null;
	}

	public Scope getParent() {
		return parent;
	}

	public boolean shouldSkip() {
		return parent != null && parent.shouldSkip();
	}

	public void doSkip() {
		if (parent == null) {
			throw new GioviException("Skip statement in non-loop scope");
		}
		parent.doSkip();
	}

	public boolean shouldBreak() {
		return parent != null && parent.shouldBreak();
	}

	public void doBreak() {
		if (parent == null) {
			throw new GioviException("Break statement in non-loop scope");
		}
		parent.doBreak();
	}

	public boolean shouldReturn() {
		return parent != null && parent.shouldReturn();
	}

	public void doReturn(GioviValue<?> returnValue) {
		if (parent == null) {
			throw new GioviException("Return statement in non-function scope");
		}
		parent.doReturn(returnValue);
	}

	public GioviValue<?> getReturnValue() {
		if (parent == null) {
			throw new GioviException("Return not available in non-function scope");
		}
		return parent.getReturnValue();
	}

	public boolean isInLoop() {
		return parent != null && parent.isInLoop();
	}

	public boolean isInFunction() {
		return parent != null && parent.isInFunction();
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
			|| (parent != null && parent.isDeclaredVariable(identifier));
	}

	public void setVariableValue(String identifier, GioviValue<?> value) {
		if (variables.containsKey(identifier)) {
			variables.get(identifier).box(value);
		} else {
			if (parent == null) {
				throw new VariableNotDeclared(identifier);
			}
			parent.setVariableValue(identifier, value);
		}
	}

	public GioviValue<?> getVariableValue(String identifier) {
		if (variables.containsKey(identifier)) {
			return variables.get(identifier);
		}
		if (parent == null) {
			throw new VariableNotDeclared(identifier);
		}
		return parent.getVariableValue(identifier);
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
			|| (parent != null && parent.isDeclaredConstant(identifier));
	}

	public GioviValue<?> getConstantValue(String identifier) {
		if (constants.containsKey(identifier)) {
			return constants.get(identifier);
		}
		if (parent == null) {
			throw new VariableNotDeclared(identifier);
		}
		return parent.getConstantValue(identifier);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Functions

	// TODO: when implementing overloading, use a Set
	private final Map<String, GioviFunctionSignature> functionSignatures = new HashMap<>();

	public void declare(GioviFunctionSignature signature) {
		if (functionSignatures.containsKey(signature.identifier())) {
			throw new FunctionAlreadyDeclared(signature);
		}
		functionSignatures.put(signature.identifier(), signature);
	}

	public GioviFunctionSignature getFunctionSignature(String identifier)
		throws FunctionNotDeclared {
		if (!functionSignatures.containsKey(identifier)) {
			if (parent == null) {
				throw new FunctionNotDeclared(identifier);
			}
			return parent.getFunctionSignature(identifier);
		}
		return functionSignatures.get(identifier);
	}


	// public GioviFunctionSignature getFunctionSignature(GioviFunctionCallSignature signature)
	// 	throws FunctionNotDeclared, MultipleFunctionSignatures {
	// 	Stream<GioviFunctionSignature> stream = functionSignatures.stream()
	// 		.filter(signature::isCompatible);
	// 	return switch (stream.count()) {
	// 		case 0l -> {
	// 			if (parent == null) {
	// 				throw new FunctionNotDeclared(signature);
	// 			}
	// 			yield parent.getFunctionSignature(signature);
	// 		}
	// 		case 1l -> stream.findFirst().get();
	// 		default -> throw new MultipleFunctionSignatures(signature);
	// 	};
	// }

	//////////////////////////////////////////////////////////////////////////////////////////////// Structs

	private final Map<String, GioviStructDefinition> structDefinitions = new HashMap<>();

	public void define(GioviStructDefinition definition) {
		if (structDefinitions.containsKey(definition.typename())) {
			throw new StructAlreadyDefined(definition.typename());
		}
		structDefinitions.put(definition.typename(), definition);
	}

	public boolean isDefinedStruct(String identifier) {
		return structDefinitions.containsKey(identifier)
			|| (parent != null && parent.isDefinedStruct(identifier));
	}

	public GioviStructDefinition getDefinitionStruct(String identifier) {
		if (structDefinitions.containsKey(identifier)) {
			return structDefinitions.get(identifier);
		}
		if (parent == null) {
			throw new StructNotDefined(identifier);
		}
		return parent.getDefinitionStruct(identifier);
	}

}
