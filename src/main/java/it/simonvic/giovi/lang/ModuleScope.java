package it.simonvic.giovi.lang;

import java.util.Optional;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviFunction;

public class ModuleScope extends Scope {

	public ModuleScope() {
		super();
	}

	public ModuleScope(ModuleScope scope) {
		super(scope);
	}

	@Override
	public Optional<GioviParserVisitResult> getSymbol(String identifier) {
		if (variables.containsKey(identifier)) {
			return Optional.of(variables.get(identifier));
		}

		if (constants.containsKey(identifier)) {
			return Optional.of(constants.get(identifier));
		}

		if (functions.containsKey(identifier)) {
			return Optional.of(new GioviFunction(functions.get(identifier)));
		}

		// FIXME: this allows accessing primitive types from any module
		// solve this once we move primitive types to an actual module
		if (primitiveTypes.containsKey(identifier)) {
			return Optional.of(primitiveTypes.get(identifier));
		}

		if (structDefinitions.containsKey(identifier)) {
			return Optional.of(structDefinitions.get(identifier));
		}

		if (modules.containsKey(identifier)) {
			return Optional.of(modules.get(identifier));
		}

		return Optional.empty();
	}

}
