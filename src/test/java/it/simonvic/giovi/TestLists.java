package it.simonvic.giovi;

import static it.simonvic.giovi.Giovi.runExpression;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.Range;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviTypes;

public class TestLists {

	@Test
	void test_GioviListsOf() {
		assertEquals(GioviLists.of(1, 2, 3), GioviLists.of(1, 2, 3));
		assertEquals(GioviLists.of("Simone", "Francesco", "Mario"),
			GioviLists.of("Simone", "Francesco", "Mario"));
	}

	@Nested
	class TestTypeInference {

		@Test
		void listOfInt_shouldBe_listOfGioviInt() {
			String code = """
				[ 1 2 3 4 ]
				""";
			assertEquals(GioviInt.class,
				runExpression(code).asGioviList().getEntryType().unbox().getClazz());
		}

		@Test
		void listOfReal_shouldBe_listOfGioviReal() {
			String code = """
				[ 1.0 2.0 3.0 4.0 ]
				""";
			assertEquals(GioviReal.class,
				runExpression(code).asGioviList().getEntryType().unbox().getClazz());
		}

		@Test
		void listOfText_shouldBe_listOfGioviText() {
			String code = """
				[ "A" "B" "C" ]
				""";
			assertEquals(GioviText.class,
				runExpression(code).asGioviList().getEntryType().unbox().getClazz());
		}

		@Test
		void listOfBool_shouldBe_listOfGioviBool() {
			String code = """
				[ true false true true ]
				""";
			assertEquals(GioviBool.class,
				runExpression(code).asGioviList().getEntryType().unbox().getClazz());
		}

		@Test
		void listOfIntAndReal_shouldBe_listOfGioviReal() {
			String code = """
				[ 1 2 3.0 4 ]
				""";
			assertEquals(GioviReal.class,
				runExpression(code).asGioviList().getEntryType().unbox().getClazz());
		}

		@Test
		void listOfMixedTypes_shouldThrow() {
			String code = """
				[ 1 2 3.0 4 "hello" 5 ]
				""";
			assertThrows(GioviException.class, () -> runExpression(code));
		}

		@Test
		void listOfList() {
			String code = """
				[ [1 2 3] [1 2 3] ]
				""";
			var expected = new GioviTypeList(GioviTypes.of(GioviInt.class));
			assertEquals(expected, runExpression(code).asGioviList().getEntryType());
		}

		@Test
		void listOfList_shouldInferCorrectType() {
			String code = """
				[ [1 2 3] [1 2.0 3] ]
				""";
			var expected = new GioviTypeList(GioviTypes.of(GioviReal.class));
			var entryType = runExpression(code).asGioviList().getEntryType();
			assertEquals(expected, entryType);
		}

		@Test
		void listOfList_mixedTypes_shouldThrow() {
			String code = """
				[ [1 2 3] ["hello" "world"] ]
				""";
			assertThrows(GioviException.class, () -> runExpression(code));
		}

	}

	private static Stream<Arguments> produceEqualLists() {
		return Stream.of(
			Arguments.of("[1 2 3]", GioviLists.of(1, 2, 3)),
			Arguments.of("[1 2 3 4]", GioviLists.of(1, 2, 3, 4)),
			Arguments.of("['Simone' 'Francesco' 'Mario']",
				GioviLists.of("Simone", "Francesco", "Mario")));

	}

	@ParameterizedTest
	@MethodSource("produceEqualLists")
	void testListEquality(String code, GioviList expected) {
		assertEquals(expected, runExpression(code));
	}


	private static Stream<Arguments> produceNotEqualLists() {
		return Stream.of(
			Arguments.of("[1 2 3]", GioviLists.of(1, 2, 3, 4)),
			Arguments.of("[1 2 3 4]", GioviLists.of(1, 2, 3)),
			Arguments.of("[1 2 3]", GioviLists.of(1, 2, 69)),
			Arguments.of("['Simone' 'Francesco' 'Ma']",
				GioviLists.of("Simone", "Francesco", "Mario")),
			Arguments.of("['Simone' 'Francesco' 'Mario' 'Luigi']",
				GioviLists.of("Simone", "Francesco", "Mario")));
	}

	@ParameterizedTest
	@MethodSource("produceNotEqualLists")
	void testListNonEquality(String code, GioviList expected) {
		assertNotEquals(expected, runExpression(code));
	}

	@Nested
	class Operations {

		@Test
		void equal() {
			assertEquals(
				GioviValue.of(
					true),
				runExpression("""
					["hello" "world"] = ["hello" "world"]
					"""));
		}

		@Test
		void contains() {
			assertEquals(
				GioviValue.of(
					true),
				runExpression("""
					["hello" "world"] =~ "world"
					"""));
		}

		@Test
		@Disabled("Candidate not implemented")
		void plus_shouldConcatenate() {
			assertEquals(
				GioviLists.of(
					1, 2, 2, 3),
				runExpression("""
					[1 2] + [2 3]
					"""));
		}

		@Test
		@Disabled("Candidate not implemented")
		void minus_shouldIntersecate() {
			assertEquals(
				GioviLists.of(
					1, 2),
				runExpression("""
					[1 2 3] - [3 4]
					"""));
		}


		@Nested
		class Access {

			@ParameterizedTest
			@CsvSource(textBlock = """
				[0 1 2 3 4]@0, 0
				[0 1 2 3 4]@1, 1
				[0 1 2 3 4]@2, 2
				[0 1 2 3 4]@3, 3
				[0 1 2 3 4]@4, 4
				""")
			void accessWithInt_shouldReturnElementAtIndex(String expression, Integer expected) {
				assertEquals(expected, runExpression(expression).asGioviInt().unbox());
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				[0 1 2 3 4]@{-1}, 4
				[0 1 2 3 4]@{-2}, 3
				[0 1 2 3 4]@{-3}, 2
				[0 1 2 3 4]@{-4}, 1
				[0 1 2 3 4]@{-5}, 0
				""")
			void accessWithNegativeInt_shouldReturnElementAtRearIndex(
				String expression,
				Integer expected) {
				assertEquals(expected, runExpression(expression).asGioviInt().unbox());
			}

			static Stream<Arguments> produceEqualGioviLists_range() {
				return Stream.of(
					Arguments.of("[0 1 2 3 4]@0..4", GioviLists.of(0, 1, 2, 3, 4)),
					Arguments.of("[0 1 2 3 4]@0..3", GioviLists.of(0, 1, 2, 3)),
					Arguments.of("[0 1 2 3 4]@0..2", GioviLists.of(0, 1, 2)),
					Arguments.of("[0 1 2 3 4]@0..1", GioviLists.of(0, 1)),
					Arguments.of("[0 1 2 3 4]@0..0", GioviLists.of(0)),
					Arguments.of("[0 1 2 3 4]@0..4", GioviLists.of(0, 1, 2, 3, 4)),
					Arguments.of("[0 1 2 3 4]@1..4", GioviLists.of(1, 2, 3, 4)),
					Arguments.of("[0 1 2 3 4]@2..4", GioviLists.of(2, 3, 4)),
					Arguments.of("[0 1 2 3 4]@3..4", GioviLists.of(3, 4)),
					Arguments.of("[0 1 2 3 4]@4..4", GioviLists.of(4)),
					Arguments.of("[0 1 2 3 4]@1..3", GioviLists.of(1, 2, 3)),
					Arguments.of("[0 1 2 3 4]@2..4", GioviLists.of(2, 3, 4)));
			}

			@ParameterizedTest
			@MethodSource("produceEqualGioviLists_range")
			void accessWithRange_shouldReturnSublist(String expression, GioviList expected) {
				assertEquals(expected, runExpression(expression));
			}

			static Stream<Arguments> produceEqualGioviLists_inverseRange() {
				return Stream.of(
					Arguments.of("[0 1 2 3 4]@4..0", GioviLists.of(4, 3, 2, 1, 0)),
					Arguments.of("[0 1 2 3 4]@3..0", GioviLists.of(3, 2, 1, 0)),
					Arguments.of("[0 1 2 3 4]@2..0", GioviLists.of(2, 1, 0)),
					Arguments.of("[0 1 2 3 4]@1..0", GioviLists.of(1, 0)),
					Arguments.of("[0 1 2 3 4]@4..1", GioviLists.of(4, 3, 2, 1)),
					Arguments.of("[0 1 2 3 4]@4..2", GioviLists.of(4, 3, 2)),
					Arguments.of("[0 1 2 3 4]@4..3", GioviLists.of(4, 3)),
					Arguments.of("[0 1 2 3 4]@3..1", GioviLists.of(3, 2, 1)),
					Arguments.of("[0 1 2 3 4]@4..2", GioviLists.of(4, 3, 2)));
			}

			@ParameterizedTest
			@MethodSource("produceEqualGioviLists_inverseRange")
			void accessWithInverseRange_shouldReturnSublistReversed(String expression,
				GioviList expected) {
				assertEquals(expected, runExpression(expression));
			}


			@Test
			void testLength() {
				assertEquals(5, runExpression("""
					"01234"@%
					""").asGioviInt().unbox());
			}

			@Test
			void testElements() {
				assertEquals(
					GioviLists.of(
						"0", "1", "2", "3", "4"),
					runExpression("""
						"01234"@*
						"""));
			}

			@Test
			void testIndexes() {
				assertEquals(
					Range.of(
						0, 4, 1),
					runExpression("""
						"01234"@@
						"""));
			}

		}

	}

}
