package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

public class TestGioviList {

	//////////////////////////////////////////////////////////////////////////////////////////////// CREATION

	@Nested
	class Creation {

		@Test
		void listOfInt_shouldBe_listOfGioviInt() {
			assertThat(visitExpression("""
				[ 1 2 3 4 ]
				""").asGioviList()
				.getEntryType().unbox().getClazz())
					.isEqualTo(GioviInt.class);
		}

		@Test
		void listOfReal_shouldBe_listOfGioviReal() {
			assertThat(visitExpression("""
				[ 1.0 2.0 3.0 4.0 ]
				""").asGioviList()
				.getEntryType().unbox().getClazz())
					.isEqualTo(GioviReal.class);
		}

		@Test
		void listOfText_shouldBe_listOfGioviText() {
			assertThat(visitExpression("""
				[ "A" "B" "C" ]
				""").asGioviList()
				.getEntryType().unbox().getClazz())
					.isEqualTo(GioviText.class);
		}

		@Test
		void listOfBool_shouldBe_listOfGioviBool() {
			assertThat(visitExpression("""
				[ true false true true ]
				""").asGioviList()
				.getEntryType().unbox().getClazz())
					.isEqualTo(GioviBool.class);
		}

		@Test
		void listOfMixedTypes_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visitExpression("""
				[ 1 2 3.0 4 "hello" 5 ]
				"""));
		}

		@Test
		void listOfMixedButCompatibleTypes_shouldInferTypeOfHighestOrder() {
			assertThat(visitExpression("""
				[1n 1i 1r]
				""").asGioviList()
				.getEntryType().unbox().getClazz())
					.isEqualTo(GioviReal.class);
		}

		@Test
		void listOfMixedButCompatibleTypes_shouldCastAllEntriesToTypeOfHighestOrder() {
			assertThat(visitExpression("""
				[1n 1i 1r]
				""").asGioviList().unbox())
				.allSatisfy(entry -> assertThat(entry
					.getGioviType())
						.isEqualTo(GioviTypes.of(GioviReal.class)));
		}

		@Test
		void listOfMixedButCompatibleTypes_shouldHaveAllValuesCastedToHighestOrderType() {
			assertThat(visitExpression("""
				[1n 1i 1r]
				""").asGioviList().unbox())
				.allSatisfy(entry -> assertThat(entry
					.getGioviType())
						.isEqualTo(GioviTypes.of(GioviReal.class)));
		}

		@Test
		void listOfListOfPrimitive_shouldHaveGioviTypeOfListOfPrimitive() {
			assertThat(visitExpression("""
				[ [1 2 3] [1 2 3] ]
				""").asGioviList()
				.getEntryType())
					.isEqualTo(new GioviTypeList(GioviTypes.of(GioviInt.class)));
		}

		@Test
		void listOfMixedButCompatibleListOfPrimitive_shouldHaveGioviTypeOfListOfHighestType() {
			assertThat(visitExpression("""
				[ [1n 2n 3n] [1i 2i 3i] [1r 2r 3r] ]
				""").asGioviList()
				.getEntryType())
					.isEqualTo(new GioviTypeList(GioviTypes.of(GioviReal.class)));
		}

		@Test
		// TODO: should it? Clarify requirements
		// when accessing a Real list, we know all entries are Real
		// or at least "subtypes" of Real (Int, Natural)
		void listOfMixedButCompatibleListOfPrimitive_shouldCastAllEntriesToHighestType() {
			assertThat(visitExpression("""
				[ [1n 2n 3n] [1i 2i 3i] [1r 2r 3r] ]
				""").asGioviList().unbox())
				.allSatisfy(entry -> assertThat(entry
					.getGioviType())
						.isEqualTo(new GioviTypeList(GioviTypes.of(GioviReal.class))));
		}

		@Test
		void listOfMixedList_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> visitExpression("""
					[ ["hello" "world"] [1 2 3] ]
					"""));
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	@Nested
	class Operations {

		//////////////////////////////////////////////////////////////////////////////////////////// redirection

		@Nested
		class Redirection {

			@Test
			void redir_shouldChangeValue() {
				assertThat(visit("""
					x [Int]
					[1 2 3] -> x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirPlusValue_shouldInsert() {
				assertThat(visit("""
					[1 2] -> x [Int]
					3 ->+ x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirMinusValue_shouldRemove() {
				assertThat(visit("""
					[1 2 3 4] -> x [Int]
					4 ->- x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirMinusValue_shouldRemoveFirstOccurence() {
				assertThat(visit("""
					[1 2 4 3 4] -> x [Int]
					4 ->- x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviLists.of(1, 2, 3, 4));
			}

			@Test
			void redirPlusList_shouldInsertAll() {
				assertThat(visit("""
					[1 2 3] -> x [Int]
					x +<- [4 5 6]
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviLists.of(1, 2, 3, 4, 5, 6));
			}

			@Test
			void redirMinusList_shouldRemoveAll() {
				assertThat(visit("""
					[1 2 3 4 5 6] -> x [Int]
					x -<- [4 5 6]
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirMinusList_shouldRemoveAllOccurences() {
				assertThat(visit("""
					[4 5 1 6 5 2 3 4 5 6] -> x [Int]
					[4 5 6] ->- x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirToListAccess_shouldChangeValueAtPosition() {
				assertThat(visit("""
					[1 2 3] -> x [Int]
					69 -> x@1
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviLists.of(1, 69, 3));
			}

			@Test
			void redirToOutOfBoundListAccess_shouldThrow() {
				assertThatExceptionOfType(IndexOutOfBoundsException.class)
					.isThrownBy(() -> visit("""
						[1 2 3] -> x [Int]
						69 -> x@420
						"""));
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// arithmetic
		@Nested
		@Disabled("Candidate not implemented yet")
		class Arithmetic {

			@Test
			@Disabled("Candidate not implemented")
			void plus_shouldConcatenate() {
				assertThat(visitExpression("""
					[1 2] + [2 3]
					"""))
					.isEqualTo(GioviLists.of(1, 2, 2, 3));
			}

			@Test
			@Disabled("Candidate not implemented")
			void minus_shouldIntersecate() {
				assertThat(visitExpression("""
					[1 2 3] - [3 4]
					"""))
					.isEqualTo(GioviLists.of(1, 2));
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////// boolean
		//////////////////////////////////////////////////////////////////////////////////////////// relational

		@Nested
		class Relational {

			@Test
			void checkingIfContainsEqualEntry_shouldReturnTrue() {
				assertThat(visitExpression("""
					["hello" "world"] =~ "world"
					""").asGioviBool().unbox())
					.isTrue();
			}

			@Test
			void checkingIfContainsEqualEntryWithDifferentType_shouldReturnTrue() {
				assertThat(visitExpression("""
					[1i 2i 3i] =~ 1n
					""").asGioviBool().unbox())
					.isTrue();
			}

		}

		@Nested
		class Equality {

			@Test
			void comparingLists_withEqualValues_shouldBeTrue() {
				assertThat(visitExpression("""
					["hello" "world"] = ["hello" "world"]
					""").asGioviBool().unbox())
					.isTrue();
			}

			@Test
			void comparingLists_withEqualValues_butDifferentSize_shouldBeFalse() {
				assertThat(visitExpression("""
					["hello" "world"] = ["hello"]
					""").asGioviBool().unbox())
					.isFalse();
			}

			@Test
			void comparingLists_withDifferentValueTypes_shouldBeFalse() {
				assertThat(visitExpression("""
					[1 2] = ["hello" "world"]
					""").asGioviBool().unbox())
					.isFalse();
			}

			@Test
			void comparingLists_withDifferentButCompatibleValueTypes_shouldBeTrue() {
				assertThat(visitExpression("""
					[1n 2n] = [1r 2r]
					""").asGioviBool().unbox())
					.isTrue();
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// access

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
				assertEquals(expected, visitExpression(expression).asGioviInt().unbox());
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
				assertEquals(expected, visitExpression(expression).asGioviInt().unbox());
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
				assertEquals(expected, visitExpression(expression));
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
				assertEquals(expected, visitExpression(expression));
			}


			@Test
			void accessWithLengthOperator_shouldReturnEntriesCount() {
				assertThat(visitExpression("""
					[0 1 2 3 4]@%
					""").asGioviInt().unbox())
					.isEqualTo(5);
			}

			@Test
			void accessWithIndexesOperator_shouldReturnRange() {
				assertThat(visitExpression("""
					[0 1 2 3 4]@@
					"""))
					.isEqualTo(Range.of(0, 4, 1));
			}

		}

		@Nested
		class Casts {

			@Test
			void upcastingListToCompatibleList_shouldCastAllEntries() {
				assertThat(visitExpression("""
					[1n 2i 3r] as [Real]
					""").asGioviList().unbox())
					.allSatisfy(entry -> assertThat(entry
						.getGioviType())
							.isEqualTo(GioviTypes.of(GioviReal.class)));

			}

		}

	}

}
