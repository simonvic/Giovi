package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.values.GioviList;
import it.simonvic.giovi.util.GioviCollections;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestLists {

	private GioviObject run(String text) {
		GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		GioviParser parser = new GioviParser(tokens);
		GioviVisitor visitor = new GioviVisitor();
		return visitor.visit(parser.expression());
	}

	@Test
	void valueListsShouldBeEqual() {
		assertEquals(GioviCollections.of(1, 2, 3), GioviCollections.of(1, 2, 3));
		assertEquals(GioviCollections.of("Simone", "Francesco", "Mario"), GioviCollections.of("Simone", "Francesco", "Mario"));
	}

	private static Stream<Arguments> produceEqualLists() {
		return Stream.of(
			Arguments.of("[]", GioviCollections.of()),
			Arguments.of("[1 2 3]", GioviCollections.of(1, 2, 3)),
			Arguments.of("[1 2 3 4]", GioviCollections.of(1, 2, 3, 4)),
			Arguments.of("['Simone' 'Francesco' 'Mario']", GioviCollections.of("Simone", "Francesco", "Mario"))
		);

	}

	private static Stream<Arguments> produceNotEqualLists() {
		return Stream.of(
			Arguments.of("[1 2 3]", GioviCollections.of(1, 2, 3, 4)),
			Arguments.of("[1 2 3 4]", GioviCollections.of(1, 2, 3)),
			Arguments.of("[1 2 3]", GioviCollections.of(1, 2, 69)),
			Arguments.of("['Simone' 'Francesco' 'Ma']", GioviCollections.of("Simone", "Francesco", "Mario")),
			Arguments.of("['Simone' 'Francesco' 'Mario' 'Luigi']", GioviCollections.of("Simone", "Francesco", "Mario"))
		);
	}

	@ParameterizedTest
	@MethodSource("produceEqualLists")
	void testListEquality(String code, GioviList<?> expected) {
		assertEquals(expected, run(code));
	}

	@ParameterizedTest
	@MethodSource("produceNotEqualLists")
	void testListNonEquality(String code, GioviList<?> expected) {
		assertNotEquals(expected, run(code));
	}


}
