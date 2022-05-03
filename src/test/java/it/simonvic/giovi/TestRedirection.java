package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.values.GioviList;
import it.simonvic.giovi.lang.values.GioviMap;
import it.simonvic.giovi.lang.values.GioviValue;
import it.simonvic.giovi.util.GioviCollections;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
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
			Arguments.of("[69 420] > list [Int]", GioviCollections.of(69, 420)),
			Arguments.of("[1 2 3] > list", GioviCollections.of(1, 2, 3)),
			Arguments.of("[1 2 3] > list", GioviCollections.of(1, 2, 3)),
			Arguments.of("4 >+ list", GioviCollections.of(1, 2, 3, 4)),
			Arguments.of("4 >- list", GioviCollections.of(1, 2, 3)),
			Arguments.of("[4 5 6] >+ list", GioviCollections.of(1, 2, 3, 4, 5, 6)),
			Arguments.of("[4 5 6] >- list", GioviCollections.of(1, 2, 3))
		);
	}

	@ParameterizedTest
	@MethodSource("produceLists")
	public void testList(String code, GioviList<GioviValue<?>> expected) {
		visitor.visit(parserOf(code).begin());
		assertEquals(expected, GioviVisitor.getCurrentScope().getVariableValue("list"));
	}


	private static Stream<Arguments> produceMaps() {
		return Stream.of(
			Arguments.of("""
				[69 : "lol"] > map [Int : Text]
				""", GioviCollections.of(Map.of(69, "lol"))
			),
			Arguments.of("""
				[1:"a" 2:"b" 3:"c"] > map
				""", GioviCollections.of(Map.of(1, "a", 2, "b", 3, "c"))
			),
			Arguments.of("""
				[4:"d"] >+ map
				""", GioviCollections.of(Map.of(1, "a", 2, "b", 3, "c", 4, "d"))
			),
			Arguments.of("""
				[5:"e" 6:"f"] >+ map
				""", GioviCollections.of(Map.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e", 6, "f"))
			),
			Arguments.of("""
				6 >- map
				""", GioviCollections.of(Map.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e"))
			),
			Arguments.of("""
				[5 : "non existing element"] >- map
				""", GioviCollections.of(Map.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e"))
			),
			Arguments.of("""
				[5 : "e"] >- map
				""", GioviCollections.of(Map.of(1, "a", 2, "b", 3, "c", 4, "d"))
			),
			Arguments.of("""
				[3:"c" 4:"d"] >- map
				""", GioviCollections.of(Map.of(1, "a", 2, "b"))
			),
			Arguments.of("""
				[1 2] >- map
				""", GioviCollections.of(Map.of())
			)
		);
	}

	@ParameterizedTest
	@MethodSource("produceMaps")
	public void testMaps(String code, GioviMap<GioviValue<?>, GioviValue<?>> expected) {
		visitor.visit(parserOf(code).begin());
		assertEquals(expected, GioviVisitor.getCurrentScope().getVariableValue("map"));
	}
}
