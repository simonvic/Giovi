package it.simonvic.giovi.lang;

import java.util.LinkedHashMap;

import it.simonvic.giovi.antlr.GioviParser.BlockContext;
import it.simonvic.giovi.lang.values.GioviType;

public record GioviFunctionSignature(
	String identifier,
	BlockContext body,
	GioviType<?> returnType,
	LinkedHashMap<String, GioviFunctionSignatureParameter> parameters)
	implements GioviParserVisitResult {


}
