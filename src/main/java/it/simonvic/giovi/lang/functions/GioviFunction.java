package it.simonvic.giovi.lang.functions;

import java.util.LinkedHashMap;
import java.util.Optional;

import it.simonvic.giovi.antlr.GioviParser.BlockContext;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.values.GioviType;

public class GioviFunction extends GioviObject {

	private final GioviType<?> returnType;
	private final LinkedHashMap<String, GioviFunctionParameter> parameters;
	private final BlockContext body;


	public GioviFunction(BlockContext body) {
		this(body, null, null);
	}

	public GioviFunction(BlockContext body, GioviType<?> returnType) {
		this(body, returnType, null);
	}

	public GioviFunction(BlockContext body, LinkedHashMap<String, GioviFunctionParameter> parameters) {
		this(body, null, parameters);
	}

	public GioviFunction(BlockContext body, GioviType<?> returnType, LinkedHashMap<String, GioviFunctionParameter> parameters) {
		this.body = body;
		this.returnType = returnType;
		this.parameters = parameters;
	}

	public Optional<GioviType<?>> getReturnType() {
		return Optional.ofNullable(returnType);
	}

	public LinkedHashMap<String, GioviFunctionParameter> getParameters() {
		return parameters;
	}

	public BlockContext getBody() {
		return body;
	}

}
