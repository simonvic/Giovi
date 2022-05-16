package it.simonvic.giovi.lang.functions;

import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.values.GioviValue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class GioviFunction extends GioviObject {

	private final Class<?> returnType;
	private final LinkedHashMap<String, GioviFunctionParameter> parameters;
	private final GioviParser.BlockContext body;


	public GioviFunction(GioviParser.BlockContext body) {
		this(body, null, null);
	}

	public GioviFunction(GioviParser.BlockContext body, Class<?> returnType) {
		this(body, returnType, null);
	}

	public GioviFunction(GioviParser.BlockContext body, LinkedHashMap<String, GioviFunctionParameter> parameters) {
		this(body, null, parameters);
	}

	public GioviFunction(GioviParser.BlockContext body, Class<?> returnType, LinkedHashMap<String, GioviFunctionParameter> parameters) {
		this.body = body;
		this.returnType = returnType;
		this.parameters = parameters;
	}

	public Optional<Class<?>> getReturnType() {
		return Optional.ofNullable(returnType);
	}

	public LinkedHashMap<String, GioviFunctionParameter> getParameters() {
		return parameters;
	}

	public GioviParser.BlockContext getBody() {
		return body;
	}

}
