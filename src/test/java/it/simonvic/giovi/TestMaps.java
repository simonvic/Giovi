package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.values.IntValue;
import it.simonvic.giovi.lang.values.MapValue;
import it.simonvic.giovi.lang.values.TextValue;
import it.simonvic.giovi.lang.values.Value;
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
		it.simonvic.giovi.antlr.GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		it.simonvic.giovi.antlr.GioviParser parser = new GioviParser(tokens);
		GioviVisitor visitor = new GioviVisitor();
		return visitor.visit(parser.expression());
	}

	@Test
	public void mapsShouldBeEqual() {
		MapValue<IntValue, TextValue> map1 = new MapValue<>();
		map1.put(new IntValue(0), new TextValue("a"));
		map1.put(new IntValue(1), new TextValue("b"));
		map1.put(new IntValue(2), new TextValue("c"));

		MapValue<IntValue, TextValue> map2 = new MapValue<>();
		map2.put(new IntValue(0), new TextValue("a"));
		map2.put(new IntValue(1), new TextValue("b"));
		map2.put(new IntValue(2), new TextValue("c"));

		assertEquals(map1, map2);
	}

	@Test
	void awafwa(){
		assertEquals(new TextValue("awf"), new TextValue("awf"));
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
	public void testMapsEquality(String code, MapValue<Value<?>, Value<?>> expected) {
		assertEquals(expected, run(code));
	}


}
