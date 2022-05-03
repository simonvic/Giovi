package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRedirection {

	GioviVisitor visitor = new GioviVisitor();

	private GioviParser parserOf(String text) {
		GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new GioviParser(tokens);
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		1.0 > varDouble Real, 1
		1 >+  varDouble, 2
		2 >+  varDouble, 4
		2 >-  varDouble, 2
		10 >* varDouble, 20
		10 >/ varDouble, 2
		"""
	)
	public void testDouble(String code, Double expected) {
		visitor.visit(parserOf(code).begin());
		assertEquals(expected, GioviVisitor.getCurrentScope().getVariableValue("varDouble").get());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		"hello" > varString Text, hello
		"1" >+ varString, hello1
		"2" >+ varString, hello12
		"2" >+ varString, hello122
		"2" >- varString, hello1
		"l" >- varString, heo1
		"eo" >- varString, h1
		"""
	)
	public void testString(String code, String expected) {
		visitor.visit(parserOf(code).begin());
		assertEquals(expected, GioviVisitor.getCurrentScope().getVariableValue("varString").get());
	}


	private static Stream<Arguments> produceLists() {
		return Stream.of(
			Arguments.of("[1 2 3] > intList List[Int]", GioviLists.of(1, 2, 3, 4))
		);
	}

	@Disabled("Not implemented yet")
	@ParameterizedTest
	@MethodSource("produceLists")
	public void testList(String code, String expected) {
		visitor.visit(parserOf(code).begin());
		assertEquals(expected, GioviVisitor.getCurrentScope().getVariableValue("varString").get());
	}
}
