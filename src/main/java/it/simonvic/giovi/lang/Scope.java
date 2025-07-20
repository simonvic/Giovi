package it.simonvic.giovi.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import giovi.Function;
import giovi.GioviParserVisitResult;
import giovi.Module;
import giovi.Module.InnerModule;
import giovi.Module.NativeModule;
import giovi.StructSignature;
import giovi.lang.*;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.*;

// TODO: add StructScope? Especially when we'll implement nested structs definition
// TODO: unify "ScopeResource" behaviour for constants, variables, functions etc.
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

	protected final Map<String, VarD<?>> variables = new HashMap<>();

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

	protected final Map<String, GioviValue<?>> constants = new HashMap<>();

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
	protected final Map<String, Function> functions = new HashMap<>();

	public void declare(Function function) {
		if (functions.containsKey(function.signature().identifier())) {
			throw new FunctionAlreadyDeclared(function.signature());
		}
		functions.put(function.signature().identifier(), function);
	}

	public boolean isDeclaredFunction(String identifier) {
		return functions.containsKey(identifier)
			|| (parent != null && parent.isDeclaredFunction(identifier));
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

	protected final Map<String, StructSignature> structDefinitions = new HashMap<>();

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

	//////////////////////////////////////////////////////////////////////////////////////////////// Modules

	protected final Map<String, Module> modules = new HashMap<>();

    public void declare(NativeModule module) {
        declare(module.identifier(), module);
    }

    public void declare(InnerModule module) {
        declare(module.identifier(), module);
    }

	public void declare(String identifier, Module module) {
		if (modules.containsKey(identifier)) {
			// TODO: lmao
			var declaredModule = modules.get(identifier);
			if (declaredModule instanceof InnerModule(_, _, _, var declaredBodyCtx)
				&& module instanceof InnerModule(_, _, _, var bodyCtx)
				&& declaredBodyCtx == bodyCtx) {
				return;
			}
			throw new ModuleAlreadyDeclared(identifier);
		}
		modules.put(identifier, module);
	}

	public boolean isDeclaredModule(String identifier) {
		return modules.containsKey(identifier)
			|| (parent != null && parent.isDeclaredModule(identifier));
	}

	public Optional<Module> getModule(String identifier) {
		if (!modules.containsKey(identifier)) {
			if (parent == null) {
				return Optional.empty();
			}
			return parent.getModule(identifier);
		}
		return Optional.of(modules.get(identifier));
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// Types

	public static final Map<String, GioviTypePrimitive<?>> primitiveTypes = Map.of(
		"Natural", new GioviTypeNatural(),
		"Int", new GioviTypeInt(),
		"Real", new GioviTypeReal(),
		"Bool", new GioviTypeBool(),
		"Text", new GioviTypeText());

	public Optional<GioviType<?>> getType(String typename) {
		if (primitiveTypes.containsKey(typename)) {
			return Optional.of(primitiveTypes.get(typename));
		}
		var structSignatureOpt = getStructSignature(typename);
		if (structSignatureOpt.isPresent()) {
			return Optional.of(new GioviTypeStruct(structSignatureOpt.orElseThrow()));
		}
		return Optional.empty();
	}

	/// Breadth-first search of a symbol.
	/// The following order will be used when looking up the symbol:
	///   - variables
	///   - constants
	///   - functions
	///   - types
	///   - modules
	public Optional<GioviParserVisitResult> getSymbol(String identifier) {
		if (variables.containsKey(identifier)) {
			return Optional.of(variables.get(identifier));
		}

		if (constants.containsKey(identifier)) {
			return Optional.of(constants.get(identifier));
		}

		// TODO(grammar): since we use `::` prefix to explicitly refer to functions, why not use `$` prefix to refer to modules? Why not extending the concept to types for consistency?
		if (functions.containsKey(identifier)) {
			return Optional.of(new GioviFunction(functions.get(identifier)));
		}

		if (primitiveTypes.containsKey(identifier)) {
			return Optional.of(primitiveTypes.get(identifier));
		}

		if (structDefinitions.containsKey(identifier)) {
			return Optional.of(new GioviTypeStruct(structDefinitions.get(identifier)));
		}

		if (modules.containsKey(identifier)) {
			return Optional.of(modules.get(identifier));
		}

		if (parent != null) {
			return parent.getSymbol(identifier);
		}

		return Optional.empty();
	}

}
