package it.simonvic.giovi.lang.functions;

import it.simonvic.giovi.antlr.GioviParser.BlockContext;
import it.simonvic.giovi.lang.GioviObject;

import java.util.LinkedHashMap;
import java.util.Optional;

public class GioviFunction extends GioviObject {

	private final Class<?> returnType;
	private final LinkedHashMap<String, GioviFunctionParameter> parameters;
	private final BlockContext body;


	public GioviFunction(BlockContext body) {
		this(body, null, null);
	}

	public GioviFunction(BlockContext body, Class<?> returnType) {
		this(body, returnType, null);
	}

	public GioviFunction(BlockContext body, LinkedHashMap<String, GioviFunctionParameter> parameters) {
		this(body, null, parameters);
	}

	public GioviFunction(BlockContext body, Class<?> returnType, LinkedHashMap<String, GioviFunctionParameter> parameters) {
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

	public BlockContext getBody() {
		return body;
	}

}
