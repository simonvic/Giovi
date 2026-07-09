package it.simonvic.giovi;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import giovi.GioviParserVisitResult;
import giovi.Module.NativeModule;
import giovi.lang.GioviValue;
import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.utils.TestVisitor;

public class GioviVisitorUtils {

	private static class DummyTestVisitor extends TestVisitor {
	}

	public static GioviParser parserOf(String code) {
		var lexer = new GioviLexer(CharStreams.fromString(code));
		var tokens = new CommonTokenStream(lexer);
		var parser = new GioviParser(tokens);
		parser.addErrorListener(new GioviParserErrorListener(code));
		return parser;
	}

	public static TestVisitor visit(String code) {
		return visit(new DummyTestVisitor(), code);
	}

	public static GioviParserVisitResult visitExpression(String code) {
		return visitExpression(new DummyTestVisitor(), code);
	}

	public static boolean visitNativeBool(String code) {
		return visitExpression(code).asNativeBool();
	}

	public static boolean visitNativeBool(TestVisitor visitor, String code) {
		return visitExpression(visitor, code).asNativeBool();
	}

	public static GioviValue<?> visitValue(String code) {
		return visitExpression(code).asGioviValue();
	}

	public static GioviValue<?> visitValue(TestVisitor visitor, String code) {
		return visitExpression(visitor, code).asGioviValue();
	}

	public static Var<?> visitVar(String code) {
		return visitExpression(code).asVar();
	}

	public static TestVisitor visit(TestVisitor visitor, String code) {
		visitor.visit(parserOf(code).begin());
		return visitor;
	}

	public static TestVisitor visit(String code, TestVisitor visitor) {
		visitor.visit(parserOf(code).begin());
		return visitor;
	}

	public static GioviParserVisitResult visitExpression(TestVisitor visitor, String code) {
		return visitor.visit(parserOf(code).expression());
	}

	public static FunctionSignature visitFunctionDeclaration(String code) {
		return visitFunctionDeclaration(new DummyTestVisitor(), code);
	}

	public static FunctionSignature visitFunctionDeclaration(TestVisitor visitor, String code) {
		return (FunctionSignature) visitor.visit(parserOf(code).functionDeclaration());
	}

}
