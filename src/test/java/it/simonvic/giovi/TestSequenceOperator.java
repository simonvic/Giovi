package it.simonvic.giovi;

import it.simonvic.giovi.lang.values.GioviList;
import it.simonvic.giovi.util.GioviCollections;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static it.simonvic.giovi.Giovi.runExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSequenceOperator {

	private static Stream<Arguments> produceEqualListsInteger() {
		return Stream.of(
			Arguments.of("[0..5]", GioviCollections.of(0, 1, 2, 3, 4, 5)),
			Arguments.of("[0..0]", GioviCollections.of(0)),
			Arguments.of("[0..10..2]", GioviCollections.of(0, 2, 4, 6, 8, 10)),
			Arguments.of("[0..100..10]", GioviCollections.of(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100)),
			Arguments.of("[0..10..10]", GioviCollections.of(0, 10)),

			Arguments.of("[5..0]", GioviCollections.of(5, 4, 3, 2, 1, 0)),
			Arguments.of("[10..0..2]", GioviCollections.of(10, 8, 6, 4, 2, 0)),
			Arguments.of("[100..10..10]", GioviCollections.of(100, 90, 80, 70, 60, 50, 40, 30, 20, 10)),
			Arguments.of("[10..0..10]", GioviCollections.of(10, 0))
		);
	}

	@ParameterizedTest
	@MethodSource("produceEqualListsInteger")
	void testSequence_Integer(String code, GioviList expected) {
		assertEquals(expected, runExpression(code));
	}

	private static Stream<Arguments> produceEqualListsDouble() {
		return Stream.of(
			Arguments.of("[0.0 .. 5.0]", GioviCollections.of(0d, 1d, 2d, 3d, 4d, 5d)),
			Arguments.of("[0.0 .. 0.0]", GioviCollections.of(0d)),
			Arguments.of("[0.0 .. 10.0 .. 2.0]", GioviCollections.of(0d, 2d, 4d, 6d, 8d, 10d)),
			Arguments.of("[0.0 .. 100.0 .. 10.0]", GioviCollections.of(0d, 10d, 20d, 30d, 40d, 50d, 60d, 70d, 80d, 90d, 100d)),
			Arguments.of("[0.0 .. 10.0 .. 10.0]", GioviCollections.of(0d, 10d)),

			Arguments.of("[5.0 .. 0.0]", GioviCollections.of(5d, 4d, 3d, 2d, 1d, 0d)),
			Arguments.of("[10.0 .. 0.0 .. 2.0]", GioviCollections.of(10d, 8d, 6d, 4d, 2d, 0d)),
			Arguments.of("[100.0 .. 10.0 .. 10.0]", GioviCollections.of(100d, 90d, 80d, 70d, 60d, 50d, 40d, 30d, 20d, 10d)),
			Arguments.of("[10.0 .. 0.0 .. 10.0]", GioviCollections.of(10d, 0d))
		);

	}

	@ParameterizedTest
	@MethodSource("produceEqualListsDouble")
	void testSequence_Double(String code, GioviList expected) {
		assertEquals(expected, runExpression(code));
	}


	private static Stream<Arguments> produceEqualListsLetters() {
		return Stream.of(
			Arguments.of("""
				["a".."e"]
				""", GioviCollections.of("a", "b", "c", "d", "e")),
			Arguments.of("""
				["a".."e"..2]
				""", GioviCollections.of("a", "c", "e"))
		);

	}

	@ParameterizedTest
	@MethodSource("produceEqualListsLetters")
	void testSequence_Letters(String code, GioviList expected) {
		assertEquals(expected, runExpression(code));
	}

}
