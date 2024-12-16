package it.simonvic.giovi.lang;

import java.util.LinkedHashMap;

import giovi.GioviParserVisitResult;

public record FormalParameters(
	LinkedHashMap<String, FormalParameter> parameters,
	LinkedHashMap<String, FormalParameter> byLabel
) implements GioviParserVisitResult {

	public FormalParameters(LinkedHashMap<String, FormalParameter> parameters) {
		this(parameters, new LinkedHashMap<>());
		for (var p : parameters.values()) {
			if (this.byLabel.put(p.label(), p) != null) {
				throw new IllegalArgumentException("Duplicate formal parameter label " + p.label());
			}
		}
	}

	public FormalParameters(FormalParameter... parameters) {
		this(new LinkedHashMap<>(), new LinkedHashMap<>());
		for (var p : parameters) {
			if (this.parameters.put(p.identifier(), p) != null) {
				throw new IllegalArgumentException("Duplicate formal parameter " + p.identifier());
			}
			if (this.byLabel.put(p.label(), p) != null) {
				throw new IllegalArgumentException("Duplicate formal parameter label " + p.label());
			}
		}
	}

	public FormalParameters copy() {
		var params = new LinkedHashMap<String, FormalParameter>();
		parameters.forEach((identifier, param) -> params.put(identifier, param.copy()));
		return new FormalParameters(params);
	}

}
