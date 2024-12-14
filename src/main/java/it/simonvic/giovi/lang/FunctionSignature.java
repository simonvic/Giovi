package it.simonvic.giovi.lang;

import java.util.ArrayList;
import java.util.List;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviTypeVoid;

public record FunctionSignature(
	String identifier,
	FormalParameters parameters,
	GioviType<?> returnType
) implements GioviParserVisitResult {

	public static final FunctionSignature EMPTY = new FunctionSignature(
		"",
		new FormalParameters(),
		new GioviTypeVoid());

	public FunctionSignature(String identifier) {
		this(identifier, new FormalParameters(), new GioviTypeVoid());
	}

	public FunctionSignature(String identifier, GioviType<?> returnType) {
		this(identifier, new FormalParameters(), returnType);
	}

	public FunctionSignature(
		String identifier,
		FormalParameters parameters
	) {
		this(identifier, parameters, new GioviTypeVoid());
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		parameters.parameters().forEach((_, param) -> sb.append(param.toString() + ","));
		return String.format("%s(%s) => %s", identifier, sb.toString(), returnType);
	}

	public List<FormalParameter> getParametersList() {
		return new ArrayList<>(parameters.parameters().values());
	}

	public boolean hasParameter(String identifier) {
		return parameters.parameters().containsKey(identifier);
	}

	public FunctionSignature copy() {
		return new FunctionSignature(identifier, parameters.copy(), returnType.copy());
	}

	public FormalParameter getParameter(String identifier) {
		return parameters.parameters().get(identifier);
	}

}
