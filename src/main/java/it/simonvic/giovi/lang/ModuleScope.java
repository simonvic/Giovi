package it.simonvic.giovi.lang;

import java.util.Optional;

import giovi.GioviParserVisitResult;
import giovi.MemberKey;
import giovi.lang.GioviFunction;
import giovi.lang.GioviTypeStruct;

public class ModuleScope extends Scope implements GioviParserVisitResult {

	private final String identifier;

	/**
	 * @param parent
	 * @param identifier
	 */
	public ModuleScope(Scope parent, String identifier) {
		super(parent);
		this.identifier = identifier;
	}

	/**
	 * @param parent
	 */
	public ModuleScope(String identifier) {
		super();
		this.identifier = identifier;
	}

	public String identifier() {
		return identifier;
	}

	@Override
	public String toString() {
		return (getParent() != null ? getParent().toString() : "") + "$" + identifier;
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof MemberKey(var identifier)) {
			var symbolOpt = getSymbol(identifier);
			if (symbolOpt.isPresent()) {
				return symbolOpt.orElseThrow();
			}
		}
		return GioviParserVisitResult.super.dot(right);
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
			return Optional.of(new GioviTypeStruct(structDefinitions.get(identifier)));
		}

		if (modules.containsKey(identifier)) {
			return Optional.of(modules.get(identifier));
		}

		return Optional.empty();
	}

}
