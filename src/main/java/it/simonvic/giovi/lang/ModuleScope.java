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
		if (right instanceof MemberKey(var identifier)) {
			var symbolOpt = getSymbol(identifier);
			if (symbolOpt.isPresent()) {
				return symbolOpt.orElseThrow();
			}
		}
		return GioviParserVisitResult.super.dot(right);
	}


}
