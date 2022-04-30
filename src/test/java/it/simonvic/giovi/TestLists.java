package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestLists {

	private Object run(String text) {
		it.simonvic.giovi.antlr.GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		it.simonvic.giovi.antlr.GioviParser parser = new GioviParser(tokens);
		GioviVisitor visitor = new GioviVisitor();
		return visitor.visit(parser.expression());
	}

	private static Stream<Arguments> produceEqualLists() {
		return Stream.of(
			Arguments.of("[]", List.of()),
			Arguments.of("[1 2 3]", List.of(1, 2, 3)),
			Arguments.of("[1 2 3 4]", List.of(1, 2, 3, 4)),
			Arguments.of("['Simone' 'Francesco' 'Mario']", List.of("Simone", "Francesco", "Mario")),
			Arguments.of("[\"Simone\" \"Francesco\" \"Mario\"]", List.of("Simone", "Francesco", "Mario"))
		);

	}

	private static Stream<Arguments> produceNotEqualLists() {
		return Stream.of(
			Arguments.of("[1 2 3]", List.of(1, 2, 3, 4)),
			Arguments.of("[1 2 3 4]", List.of(1, 2, 3)),
			Arguments.of("[1 2 3]", List.of(1, 2, 69)),
			Arguments.of("['Simone' 'Francesco' 'Ma']", List.of("Simone", "Francesco", "Mario")),
			Arguments.of("['Simone' 'Francesco' 'Mario' 'Luigi']", List.of("Simone", "Francesco", "Mario")),
			Arguments.of("[\"Simone\" \"Francesco\" \"mario\"]", List.of("Simone", "Francesco", "Mario"))
		);
	}

	@ParameterizedTest
	@MethodSource("produceEqualLists")
	public void testListEquality(String code, List<Integer> expected) {
		assertEquals(expected, run(code));
	}

	@ParameterizedTest
	@MethodSource("produceNotEqualLists")
	public void testListNonEquality(String code, List<Integer> expected) {
		assertNotEquals(expected, run(code));
	}

}
