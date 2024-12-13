package it.simonvic.giovi.lang;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviTypeVoid;

public record FunctionSignature(
	String identifier,
	LinkedHashMap<String, FormalParameter> parameters,
	GioviType<?> returnType
) implements GioviParserVisitResult {

	public static final FunctionSignature EMPTY = new FunctionSignature(
		"",
		new LinkedHashMap<>(),
		new GioviTypeVoid());

	public FunctionSignature(String identifier) {
		this(identifier, new LinkedHashMap<>(), new GioviTypeVoid());
	}

	public FunctionSignature(String identifier, GioviType<?> returnType) {
		this(identifier, new LinkedHashMap<>(), returnType);
	}

	public FunctionSignature(
		String identifier,
		LinkedHashMap<String, FormalParameter> parameters
	) {
		this(identifier, parameters, new GioviTypeVoid());
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		parameters.forEach((_, param) -> sb.append(param.toString() + ","));
		return String.format("%s(%s) => %s", identifier, sb.toString(), returnType);
	}

	public List<FormalParameter> getParametersList() {
		return new ArrayList<>(parameters.values());
	}

	public FunctionSignature copy() {
		var params = new LinkedHashMap<String, FormalParameter>();
		parameters.forEach((identifier, param) -> params.put(identifier, param.copy()));
		return new FunctionSignature(identifier, params, returnType.copy());
	}

}
