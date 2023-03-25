package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.functions.GioviFunction;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestFunctions {

	private GioviParser setupParser(String text) {
		GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new GioviParser(tokens);
	}

	@Test
	void notReturningInFunctionWithReturn_shouldThrow() {
		GioviParser parser = setupParser("""
			someFunction() => Int {}
			someFunction()
			""");
		assertThrows(GioviException.class, () -> new GioviVisitor().visit(parser.begin()));
	}

	@Test
	void returningInFunctionWithNoReturn_shouldThrowException() {
		GioviParser parser = setupParser("""
			someFunction() {
				=> 69
			}
			someFunction()
			""");
		assertThrows(GioviException.class, () -> new GioviVisitor().visit(parser.begin()));
	}

	@Test
	void returningWrongType_shouldThrowException() {
		GioviParser parser = setupParser("""
			someFunction() => Text {
				=> 69
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

	@Test
	void testDeclaration_noParams_noReturn() {
		GioviParser parser = setupParser("""
			sayHelloWorld() {
				"hello world" > \\out
			}
			""");
		GioviVisitor visitor = new GioviVisitor();
		visitor.visit(parser.begin());
		GioviFunction actual = visitor.getCurrentScope().getDefinitionFunction("sayHelloWorld");
		GioviFunction expected = new GioviFunction(null, null, new LinkedHashMap<>());
		assertEquals(actual.getReturnType(), expected.getReturnType());
		assertEquals(actual.getParameters(), expected.getParameters());
	}

	@Test
	void nonDefaultParameterAfterDefaultParameter_shouldThrowException() {
		GioviParser parser = setupParser("""
			fun(x Int _ 69, y Int) {
			}
			""");
		assertThrows(GioviException.class, () -> new GioviVisitor().visit(parser.begin()));
	}

	@Test
	void correctDefaultParameters_shouldNotThrowException() {
		GioviParser parser = setupParser("""
			fun(x Int, y Int _ 69, z Int _ 420) {
			}
			""");
		assertDoesNotThrow(() -> new GioviVisitor().visit(parser.begin()));
	}

	@Disabled("Function overload not implemented yet")
	@Nested
	class TestOverload {

		@Test
		void differentParameterCount_shouldNotThrow() {
			GioviParser parser = setupParser("""
				fun(x Int, y Int) {}
				fun(x Int, y Int, z Int) {}
				""");
			assertDoesNotThrow(() -> new GioviVisitor().visit(parser.begin()));
		}

		@Test
		void sameSignature_shouldThrow() {
			GioviParser parser = setupParser("""
				fun(x Int, y Int) {}
				fun(x Int, y Int) {}
				""");
			assertThrows(GioviException.class, () -> new GioviVisitor().visit(parser.begin()));
		}
	}

	@Nested
	class TestNamedParametersCall {

		@Test
		void missingParameter_shouldThrow() {
			GioviParser parser = setupParser("""
				fun(a Int, b Int) {}
				fun(b: 0)
				""");
			assertThrows(GioviException.class, () -> new GioviVisitor().visit(parser.begin()));
		}

		@Test
		void duplicateNamedParameter_shouldThrow() {
			GioviParser parser = setupParser("""
				fun(a Int, b Int) {}
				fun(a: 0, a: 1)
				""");
			assertThrows(GioviException.class, () -> new GioviVisitor().visit(parser.begin()));
		}

		@Test
		void unknownNamedParameter_shouldThrow() {
			GioviParser parser = setupParser("""
				fun(a Int, b Int) {}
				fun(a: 0, b: 1, z: 69)
				""");
			assertThrows(GioviException.class, () -> new GioviVisitor().visit(parser.begin()));
		}

	}
}
