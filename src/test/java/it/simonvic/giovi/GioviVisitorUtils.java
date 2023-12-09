package it.simonvic.giovi;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviFunctionSignature;
import it.simonvic.giovi.lang.GioviParserVisitResult;

public class GioviVisitorUtils {

	public static GioviParser parserOf(String code) {
		var lexer = new GioviLexer(CharStreams.fromString(code));
		var tokens = new CommonTokenStream(lexer);
		return new GioviParser(tokens);
	}

	public static GioviVisitor visit(String code) {
		return visit(new GioviVisitor(), code);
	}

	public static GioviParserVisitResult visitExpression(String code) {
		return visitExpression(new GioviVisitor(), code);
	}

	public static GioviVisitor visit(GioviVisitor visitor, String code) {
		var lexer = new GioviLexer(CharStreams.fromString(code));
		var tokens = new CommonTokenStream(lexer);
		visitor.visit(new GioviParser(tokens).begin());
		return visitor;
	}

	public static GioviParserVisitResult visitExpression(GioviVisitor visitor, String code) {
		return visitor.visit(parserOf(code).expression());
	}

	public static GioviFunctionSignature visitFunctionDeclaration(String code) {
		return visitFunctionDeclaration(new GioviVisitor(), code);
	}

	public static GioviFunctionSignature visitFunctionDeclaration(
		GioviVisitor visitor,
		String code) {
		return (GioviFunctionSignature) visitor.visit(parserOf(code).functionDeclaration());
	}


}
