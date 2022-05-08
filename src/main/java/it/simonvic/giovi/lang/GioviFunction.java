package it.simonvic.giovi.lang;

import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.values.GioviValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class GioviFunction extends GioviObject {

	private final GioviValue<?> returnValue;
	private final LinkedHashMap<String, GioviValue<?>> parameters;
	private final GioviParser.BlockContext body;


//	public GioviFunction(GioviParser.BlockContext body) {
//		this(body, null, null);
//	}
//
//	public GioviFunction(GioviParser.BlockContext body, GioviValue<?> returnValue) {
//		this(body, returnValue, null);
//	}
//
//	public GioviFunction(GioviParser.BlockContext body, Map<String, GioviValue<?>> parameters) {
//		this(body, null, parameters);
//	}

	public GioviFunction(GioviParser.BlockContext body, GioviValue<?> returnValue, LinkedHashMap<String, GioviValue<?>> parameters) {
		this.body = body;
		this.returnValue = returnValue;
		this.parameters = parameters;
	}

	public GioviValue<?> getReturnValue() {
		return returnValue;
	}

	public Map<String, GioviValue<?>> getParameters() {
		return parameters;
	}

	public GioviParser.BlockContext getBody() {
		return body;
	}
}
