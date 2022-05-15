package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.functions.GioviFunction;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestFunctions {

	private GioviParser setupParser(String text) {
		GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new GioviParser(tokens);
	}

	@Test
	void returningInFunctionWithNoReturn_shouldThrowException() {
		GioviParser parser = setupParser("""
			someFunction() {
				69 =>
			}
			someFunction()
			""");
		assertThrows(GioviException.class, () -> new GioviVisitor().visit(parser.begin()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"fun(){} fun(69)",
		"fun(x Int){} fun()",
		"fun(x Int, y Int){} fun(69)"
	})
	void functionCall_wrongParametersNumber_shouldThrowException(String code) {

		assertThrows(GioviException.class, () -> new GioviVisitor().visit(setupParser(code).begin()));
	}

	@Disabled("Should the function return the default value???")
	@Test
	void nonReturningInFunctionWithReturn_shouldThrowException() {
		GioviParser parser = setupParser("""
			someFunction() => Int {}
			someFunction()
			""");
		assertThrows(GioviException.class, () -> new GioviVisitor().visit(parser.begin()));
	}

	@Test
	void testDeclaration_noParams_noReturn() {
		GioviParser parser = setupParser("""
			sayHelloWorld() {
				"hello world" > \\out
			}
			""");
		GioviVisitor visitor = new GioviVisitor();
		visitor.visit(parser.begin());
		GioviFunction actual = visitor.getCurrentScope().getFunction("sayHelloWorld");
		GioviFunction expected = new GioviFunction(null, null, new LinkedHashMap<>());
		assertEquals(actual.getReturnValue(), expected.getReturnValue());
		assertEquals(actual.getParameters(), expected.getParameters());
	}
}
