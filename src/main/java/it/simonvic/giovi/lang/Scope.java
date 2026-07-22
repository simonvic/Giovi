package it.simonvic.giovi.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import giovi.Function;
import giovi.GioviParserVisitResult;
import giovi.Module;
import giovi.Module.InnerModule;
import giovi.Module.NativeModule;
import giovi.lang.*;
import it.simonvic.giovi.Const;
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

	protected final Map<String, Const<?>> constants = new HashMap<>();

	public void declareConstant(String identifier, GioviValue<?> value) {
		declareConstant(identifier, new Const<>(value));
	}

	public void declareConstant(String identifier, Const<?> constant) {
		if (constants.containsKey(identifier)) {
			throw new ConstantAlreadyDeclared(identifier);
		}
		constants.put(identifier, constant);
	}

	public boolean isDeclaredConstant(String identifier) {
		return constants.containsKey(identifier)
			|| (parent != null && parent.isDeclaredConstant(identifier));
	}

	public Optional<Const<?>> getConstantValue(String identifier) {
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
		declare(function.signature().identifier(), function);
	}

	public void declare(String identifier, Function function) {
		if (functions.containsKey(identifier)) {
			throw new FunctionAlreadyDeclared(function.signature());
		}
		functions.put(identifier, function);
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

	protected final Map<String, GioviTypeStruct> structDefinitions = new HashMap<>();

	public void define(GioviTypeStruct type) {
		define(type.signature().typename(), type);
	}

	public void define(String identifier, GioviTypeStruct type) {
		if (structDefinitions.containsKey(identifier)) {
			throw new StructAlreadyDefined(identifier);
		}
		structDefinitions.put(identifier, type);
	}

	public boolean isDefinedStruct(String identifier) {
		return structDefinitions.containsKey(identifier)
			|| (parent != null && parent.isDefinedStruct(identifier));
	}

	public Optional<GioviTypeStruct> getStructType(String identifier) {
		if (structDefinitions.containsKey(identifier)) {
			return Optional.of(structDefinitions.get(identifier));
		}
		if (parent == null) {
			return Optional.empty();
		}
		return parent.getStructType(identifier);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Modules

	protected final Map<String, Module> modules = new HashMap<>();

	public void declare(NativeModule module) {
		declare(module.identifier(), module);
	}

	public void declare(InnerModule module) {
		var declaredModule = modules.get(module.identifier());
		if (declaredModule != null) {
			if (
				declaredModule instanceof InnerModule(_, _, _, var bodyCtx)
					&& bodyCtx == module.bodyCtx()
			) {
				// NOTE: module already declared with early eager inner module scan
				return;
			}
			throw new ModuleAlreadyDeclared(module.identifier());
		}
		modules.put(module.identifier(), module);
	}

	public void declare(String identifier, Module module) {
		if (modules.containsKey(identifier)) {
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

	// TODO: move to GioviType

	public static final Map<String, GioviTypePrimitive<?>> primitiveTypes = Map.of(
		"Natural", GioviType.NAT,
		"Int", GioviType.INT,
		"Real", GioviType.REAL,
		"Bool", GioviType.BOOL,
		"Text", GioviType.TEXT
	);

	public Optional<GioviType<?>> getType(String typename) {
		return Optional.<GioviType<?>>ofNullable(primitiveTypes.get(typename))
			.or(() -> getStructType(typename));
	}

	/// Breadth-first search of a symbol.
	///
	/// The following order will be used when looking up the symbol:
	/// - variables
	/// - constants
	/// - functions
	/// - types
	/// - modules
	///
	/// @param identifier identifier of the symbol
	/// @return symbol
	public Optional<GioviParserVisitResult> getSymbol(String identifier) {
		// TODO(grammar): since we use `::` prefix to explicitly refer to functions, why not use `$` prefix to refer to modules? Why not extending the concept to types for consistency?
		return Optional
			.<GioviParserVisitResult>ofNullable(variables.get(identifier))
			.or(() -> Optional.ofNullable(constants.get(identifier)))
			.or(() -> Optional.ofNullable(functions.get(identifier)).map(GioviFunction::new))
			.or(() -> Optional.ofNullable(primitiveTypes.get(identifier)))
			.or(() -> Optional.ofNullable(structDefinitions.get(identifier)))
			.or(() -> Optional.ofNullable(modules.get(identifier)))
			.or(() -> parent.getSymbol(identifier));
	}

	public void merge(Scope scope) {
		scope.variables.forEach((k, v) -> this.declare(k, v));
	}

}
