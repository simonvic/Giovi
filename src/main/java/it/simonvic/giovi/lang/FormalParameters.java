package it.simonvic.giovi.lang;

import java.util.LinkedHashMap;

import giovi.GioviParserVisitResult;

public record FormalParameters(
	LinkedHashMap<String, FormalParameter> parameters
) implements GioviParserVisitResult {

	public FormalParameters(FormalParameter... parameters) {
		this(new LinkedHashMap<>());
		for (var p : parameters) {
			this.parameters.put(p.identifier(), p);
		}
	}

	public FormalParameters copy() {
		var params = new LinkedHashMap<String, FormalParameter>();
		parameters.forEach((identifier, param) -> params.put(identifier, param.copy()));
		return new FormalParameters(params);
	}

}
