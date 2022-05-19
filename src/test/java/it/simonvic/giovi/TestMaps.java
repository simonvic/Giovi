package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.values.GioviInt;
import it.simonvic.giovi.lang.values.GioviMap;
import it.simonvic.giovi.lang.values.GioviText;
import it.simonvic.giovi.lang.values.GioviValue;
import it.simonvic.giovi.util.GioviCollections;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMaps {

	private GioviObject run(String text) {
		GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		GioviParser parser = new GioviParser(tokens);
		GioviVisitor visitor = new GioviVisitor();
		return visitor.visit(parser.expression());
	}

	@Test
	void mapsShouldBeEqual() {
		GioviMap<GioviInt, GioviText> map1 = new GioviMap<>();
		map1.put(new GioviInt(0), new GioviText("a"));
		map1.put(new GioviInt(1), new GioviText("b"));
		map1.put(new GioviInt(2), new GioviText("c"));

		GioviMap<GioviInt, GioviText> map2 = new GioviMap<>();
		map2.put(new GioviInt(0), new GioviText("a"));
		map2.put(new GioviInt(1), new GioviText("b"));
		map2.put(new GioviInt(2), new GioviText("c"));

		assertEquals(map1, map2);
	}

	private static Stream<Arguments> produceEqualMaps() {
		return Stream.of(
			Arguments.of("""
				[ 1:"a" 2:"b" 3:"c" ]
				""", GioviCollections.of(Map.of(1, "a", 2, "b", 3, "c"))
			)
		);
	}

	@ParameterizedTest
	@MethodSource("produceEqualMaps")
	void testMapsEquality(String code, GioviMap<GioviValue<?>, GioviValue<?>> expected) {
		assertEquals(expected, run(code));
	}

}
