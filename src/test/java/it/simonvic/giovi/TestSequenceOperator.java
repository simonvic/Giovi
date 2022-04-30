package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSequenceOperator {

	private Object run(String text) {
		it.simonvic.giovi.antlr.GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		it.simonvic.giovi.antlr.GioviParser parser = new GioviParser(tokens);
		GioviVisitor visitor = new GioviVisitor();
		return visitor.visit(parser.expression());
	}

	private static Stream<Arguments> produceEqualListsInteger() {
		return Stream.of(
			Arguments.of("[0..5]", List.of(0, 1, 2, 3, 4, 5)),
			Arguments.of("[0..0]", List.of(0)),
			Arguments.of("[0..10..2]", List.of(0, 2, 4, 6, 8, 10)),
			Arguments.of("[0..100..10]", List.of(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100)),
			Arguments.of("[0..10..10]", List.of(0, 10))
		);

	}

	@ParameterizedTest
	@MethodSource("produceEqualListsInteger")
	public void testSequence_Integer(String code, List<Integer> expected) {
		assertEquals(expected, run(code));
	}

	private static Stream<Arguments> produceEqualListsDouble() {
		return Stream.of(
			Arguments.of("[0.0..5.0]", List.of(0d, 1d, 2d, 3d, 4d, 5d)),
			Arguments.of("[0.0..0.0]", List.of(0d)),
			Arguments.of("[0.0..10.0..2.0]", List.of(0d, 2d, 4d, 6d, 8d, 10d)),
			Arguments.of("[0.0..100.0..10.0]", List.of(0d, 10d, 20d, 30d, 40d, 50d, 60d, 70d, 80d, 90d, 100d)),
			Arguments.of("[0.0..10.0..10.0]", List.of(0d, 10d))
		);

	}

	@ParameterizedTest
	@MethodSource("produceEqualListsDouble")
	public void testSequence_Double(String code, List<Double> expected) {
		assertEquals(expected, run(code));
	}



	private static Stream<Arguments> produceEqualListsLetters() {
		return Stream.of(
			Arguments.of("""
				["a".."e"]
				""", List.of("a","b","c","d","e")),
			Arguments.of("""
				["a".."e"..2]
				""", List.of("a","c","e"))
		);

	}

	@Disabled("Not implemented yet")
	@ParameterizedTest
	@MethodSource("produceEqualListsLetters")
	public void testSequence_Letters(String code, List<Double> expected) {
		assertEquals(expected, run(code));
	}

}
