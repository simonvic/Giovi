package it.simonvic.giovi.lang.functions;

import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.values.GioviValue;

import java.util.LinkedHashMap;
import java.util.Optional;

public class GioviFunction extends GioviObject {

	private final GioviValue<?> returnValue;
	private final LinkedHashMap<String, GioviFunctionParameter> parameters;
	private final GioviParser.BlockContext body;


	public GioviFunction(GioviParser.BlockContext body) {
		this(body, null, null);
	}

	public GioviFunction(GioviParser.BlockContext body, GioviValue<?> returnValue) {
		this(body, returnValue, null);
	}

	public GioviFunction(GioviParser.BlockContext body, LinkedHashMap<String, GioviFunctionParameter> parameters) {
		this(body, null, parameters);
	}

	public GioviFunction(GioviParser.BlockContext body, GioviValue<?> returnValue, LinkedHashMap<String, GioviFunctionParameter> parameters) {
		this.body = body;
		this.returnValue = returnValue;
		this.parameters = parameters;
	}

	public Optional<GioviValue<?>> getReturnValue() {
		return Optional.ofNullable(returnValue);
	}

	public LinkedHashMap<String, GioviFunctionParameter> getParameters() {
		return parameters;
	}

	public GioviParser.BlockContext getBody() {
		return body;
	}

}
