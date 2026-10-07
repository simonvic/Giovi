package it.simonvic.giovi.lsp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import it.simonvic.giovi.lsp.LSVisitResult.Type;
import it.simonvic.giovi.lsp.LSVisitResult.Type.TypePrimitive;

public sealed class Environment {

	public static final class EnvironmentFunction extends Environment {

		public EnvironmentFunction(Environment parent) {
			super(parent);
		}

		public EnvironmentFunction() {
			super();
		}

	}

	public static final class EnvironmentLoop extends Environment {

		public EnvironmentLoop(Environment parent) {
			super(parent);
		}

		public EnvironmentLoop() {
			super();
		}

	}

	private final Environment parent;

	public Environment(Environment parent) {
		this.parent = parent;
	}

	public Environment() {
		this.parent = null;
	}

	public Environment parent() {
		return parent;
	}

	public boolean declare(
		Map<String, Type> environment,
		String identifier,
		Type type
	) {
		if (environment.containsKey(identifier)) {
			return false;
		}
		environment.put(identifier, type);
		return true;
	}

	public boolean isDeclared(
		Map<String, Type> environment,
		String identifier
	) {
		return environment.containsKey(identifier)
			|| (parent != null && parent.isDeclaredConstant(identifier));
	}

	public Optional<Type> get(
		Map<String, Type> environment,
		String identifier
	) {
		if (environment.containsKey(identifier)) {
			return Optional.of(environment.get(identifier));
		}
		if (parent == null) {
			return Optional.empty();
		}
		return parent.getConstant(identifier);
	}

	public Optional<Type> lookup(String identifier) {
		return Optional.ofNullable(variables.get(identifier))
			.or(() -> Optional.ofNullable(constants.get(identifier)))
			.or(() -> Optional.ofNullable(functions.get(identifier)))
			.or(() -> Optional.ofNullable(types.get(identifier)))
			.or(() -> Optional.ofNullable(modules.get(identifier)))
			.or(() -> parent == null ? Optional.empty() : parent.lookup(identifier));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Variables

	private final Map<String, Type> variables = new HashMap<>();

	public boolean declareVariable(String identifier, Type type) {
		return declare(variables, identifier, type);
	}

	public boolean isDeclaredVariable(String identifier) {
		return isDeclared(variables, identifier);
	}

	public Optional<Type> getVariable(String identifier) {
		return get(variables, identifier);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Constants

	private final Map<String, Type> constants = new HashMap<>();

	public boolean declareConstant(String identifier, Type type) {
		return declare(constants, identifier, type);
	}

	public boolean isDeclaredConstant(String identifier) {
		return isDeclared(constants, identifier);
	}

	public Optional<Type> getConstant(String identifier) {
		return get(constants, identifier);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Functions

	private final Map<String, Type> functions = new HashMap<>();

	public boolean declareFunction(String identifier, Type type) {
		return declare(functions, identifier, type);
	}

	public boolean isDeclaredFunction(String identifier) {
		return isDeclared(functions, identifier);
	}

	public Optional<Type> getFunction(String identifier) {
		return get(functions, identifier);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Types

	private final Map<String, Type> types = new HashMap<>();

	public static final Map<String, TypePrimitive> primitiveTypes = Map.of();

	public boolean declareType(String identifier, Type type) {
		return declare(types, identifier, type);
	}

	public boolean isDeclaredType(String identifier) {
		return isDeclared(types, identifier);
	}

	public Optional<Type> getType(String identifier) {
		return switch (identifier) {
			case "Natural" -> Optional.of(Type.NAT);
			case "Int" -> Optional.of(Type.INT);
			case "Real" -> Optional.of(Type.REAL);
			case "Bool" -> Optional.of(Type.BOOL);
			case "Text" -> Optional.of(Type.TEXT);
			default -> get(types, identifier);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Modules

	private final Map<String, Type> modules = new HashMap<>();

	public void declareModule(String identifier, Type type) {
		declare(modules, identifier, type);
	}

	public boolean isDeclaredModule(String identifier) {
		return isDeclared(modules, identifier);
	}

	public Optional<Type> getModule(String identifier) {
		return get(modules, identifier);
	}

}
