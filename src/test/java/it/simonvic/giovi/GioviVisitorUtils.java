package it.simonvic.giovi;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviFunctionSignature;
import it.simonvic.giovi.lang.GioviParserVisitResult;
import it.simonvic.giovi.lang.GioviStructDefinition;
import it.simonvic.giovi.lang.values.GioviValue;

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

	public static boolean visitNativeBool(String code) {
		return visitExpression(code).asNativeBool();
	}

	public static GioviValue<?> visitValue(String code) {
		return visitExpression(code).asGioviValue();
	}

	public static GioviValue<?> visitValue(GioviVisitor visitor, String code) {
		return visitExpression(visitor, code).asGioviValue();
	}

	public static Object visitNativeValue(String code) {
		return visitValue(code).unbox();
	}

	public static Object visitNativeValue(GioviVisitor visitor, String code) {
		return visitValue(visitor, code).unbox();
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

	public static GioviStructDefinition visitStructDefinition(String code) {
		return visitStructDefinition(new GioviVisitor(), code);
	}

	public static GioviStructDefinition visitStructDefinition(GioviVisitor visitor, String code) {
		return (GioviStructDefinition) visitor.visit(parserOf(code).structDefinition());
	}


}
