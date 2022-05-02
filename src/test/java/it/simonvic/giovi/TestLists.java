package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.values.IntValue;
import it.simonvic.giovi.lang.values.Value;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestLists {

	private GioviObject run(String text) {
		it.simonvic.giovi.antlr.GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		it.simonvic.giovi.antlr.GioviParser parser = new GioviParser(tokens);
		GioviVisitor visitor = new GioviVisitor();
		return visitor.visit(parser.expression());
	}

	private static Stream<Arguments> produceEqualLists() {
		return Stream.of(
			Arguments.of("[]", new ArrayList<Value<?>>()),
			Arguments.of("[1 2 3]", GioviList.of(1, 2, 3)),
			Arguments.of("[1 2 3 4]", GioviList.of(1, 2, 3, 4)),
			Arguments.of("['Simone' 'Francesco' 'Mario']", GioviList.of("Simone", "Francesco", "Mario")),
			Arguments.of("[\"Simone\" \"Francesco\" \"Mario\"]", GioviList.of("Simone", "Francesco", "Mario"))
		);

	}

	private static Stream<Arguments> produceNotEqualLists() {
		return Stream.of(
			Arguments.of("[1 2 3]", GioviList.of(1, 2, 3, 4)),
			Arguments.of("[1 2 3 4]", GioviList.of(1, 2, 3)),
			Arguments.of("[1 2 3]", GioviList.of(1, 2, 69)),
			Arguments.of("['Simone' 'Francesco' 'Ma']", GioviList.of("Simone", "Francesco", "Mario")),
			Arguments.of("['Simone' 'Francesco' 'Mario' 'Luigi']", GioviList.of("Simone", "Francesco", "Mario")),
			Arguments.of("[\"Simone\" \"Francesco\" \"mario\"]", GioviList.of("Simone", "Francesco", "Mario"))
		);
	}

	@ParameterizedTest
	@MethodSource("produceEqualLists")
	public void testListEquality(String code, List<Value<?>> expected) {
		assertEquals(expected, run(code).asList());
	}

	@ParameterizedTest
	@MethodSource("produceNotEqualLists")
	public void testListNonEquality(String code, List<Value<?>> expected) {
		assertNotEquals(expected, run(code).asList());
	}

}
