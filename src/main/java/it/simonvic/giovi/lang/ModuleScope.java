package it.simonvic.giovi.lang;

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
		return switch (right) {
			// TODO: unify "member lookup of unkown type" so we don't end up with different behaviours
			case MemberKey(var identifier) when isDeclaredVariable(identifier)
				-> getVariable(identifier).orElseThrow();
			case MemberKey(var identifier) when isDefinedStruct(identifier)
				-> new GioviTypeStruct(getStructSignature(identifier).orElseThrow());
			case MemberKey(var identifier) when isDeclaredFunction(identifier)
				-> new GioviFunction(getFunction(identifier).orElseThrow());
			case MemberKey(var identifier) when isDeclaredModule(identifier)
				-> getModule(identifier).orElseThrow();
			case MemberKey(var identifier) when isDeclaredConstant(identifier)
				-> getConstantValue(identifier).orElseThrow();
			default -> GioviParserVisitResult.super.dot(right);
		};
	}


}
