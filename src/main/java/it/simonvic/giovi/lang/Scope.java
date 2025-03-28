package it.simonvic.giovi.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import giovi.Function;
import giovi.StructSignature;
import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.*;

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

	private final Map<String, VarD<?>> variables = new HashMap<>();

	public void declare(String identifier, VarD<?> var) {
		if (variables.containsKey(identifier)) {
			throw new VariableAlreadyDeclared(identifier);
		}
		variables.put(identifier, var);
	}

	public boolean isDeclaredVariable(String identifier) {
		return variables.containsKey(identifier)
			|| (parent != null && parent.isDeclaredVariable(identifier));
	}

	public Optional<VarD<?>> getVariable(String identifier) {
		if (variables.containsKey(identifier)) {
			return Optional.of(variables.get(identifier));
		}
		if (parent == null) {
			return Optional.empty();
		}
		return parent.getVariable(identifier);
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

	public Optional<GioviValue<?>> getConstantValue(String identifier) {
		if (constants.containsKey(identifier)) {
			return Optional.of(constants.get(identifier));
		}
		if (parent == null) {
			return Optional.empty();
		}
		return parent.getConstantValue(identifier);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Functions

	// TODO: when implementing overloading, use a Set
	private final Map<String, Function> functions = new HashMap<>();

	public void declare(Function function) {
		if (functions.containsKey(function.signature().identifier())) {
			throw new FunctionAlreadyDeclared(function.signature());
		}
		functions.put(function.signature().identifier(), function);
	}

	public Optional<Function> getFunction(String identifier) {
		if (!functions.containsKey(identifier)) {
			if (parent == null) {
				return Optional.empty();
			}
			return parent.getFunction(identifier);
		}
		return Optional.of(functions.get(identifier));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Structs

	private final Map<String, StructSignature> structDefinitions = new HashMap<>();

	public void define(StructSignature signature) {
		if (structDefinitions.containsKey(signature.typename())) {
			throw new StructAlreadyDefined(signature.typename());
		}
		structDefinitions.put(signature.typename(), signature);
	}

	public boolean isDefinedStruct(String identifier) {
		return structDefinitions.containsKey(identifier)
			|| (parent != null && parent.isDefinedStruct(identifier));
	}

	public Optional<StructSignature> getStructSignature(String identifier) {
		if (structDefinitions.containsKey(identifier)) {
			return Optional.of(structDefinitions.get(identifier));
		}
		if (parent == null) {
			return Optional.empty();
		}
		return parent.getStructSignature(identifier);
	}

}
