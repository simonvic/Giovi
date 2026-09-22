package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import giovi.GioviParserVisitResult;
import giovi.Range;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviRecords;

public class TestGioviList {

	//////////////////////////////////////////////////////////////////////////////////////////////// CREATION

	@Nested
	class Creation {

		@Test
		void listOfInt_shouldBe_listOfGioviInt() {
			assertThat(
				visitExpression("""
					[ 1 2 3 4 ]
					""")
					.asGioviList()
					.type()
					.entryType()
					.value()
			).isEqualTo(GioviInt.class);
		}

		@Test
		void listOfReal_shouldBe_listOfGioviReal() {
			assertThat(
				visitExpression("""
					[ 1.0 2.0 3.0 4.0 ]
					""")
					.asGioviList()
					.type()
					.entryType()
					.value()
			).isEqualTo(GioviReal.class);
		}

		@Test
		void listOfText_shouldBe_listOfGioviText() {
			assertThat(
				visitExpression("""
					[ "A" "B" "C" ]
					""")
					.asGioviList()
					.type()
					.entryType()
					.value()
			).isEqualTo(GioviText.class);
		}

		@Test
		void listOfBool_shouldBe_listOfGioviBool() {
			assertThat(
				visitExpression("""
					[ true false true true ]
					""")
					.asGioviList()
					.type()
					.entryType().value()
			).isEqualTo(GioviBool.class);
		}

		@Test
		void listOfMixedTypes_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visitExpression("""
				[ 1 2 3.0 4 "hello" 5 ]
				"""));
		}

		@Test
		void listOfMixedButCompatibleTypes_shouldInferWidestType() {
			assertThat(
				visitExpression("""
					[1n 1i 1r]
					""")
					.asGioviList()
					.type()
					.entryType()
					.value()
			).isEqualTo(GioviReal.class);
		}

		@Test
		void listOfMixedButCompatibleTypes_shouldHaveAllValuesWidened() {
			assertThat(
				visitExpression("""
					[1n 1i 1r]
					""")
					.asGioviList()
					.value()
			).allSatisfy(
				entry -> assertThat(entry.getGioviType())
					.isEqualTo(GioviType.REAL)
			);
		}

		@Test
		void listOfListOfPrimitive_shouldHaveGioviTypeOfListOfPrimitive() {
			assertThat(
				visitExpression("""
					[ [1 2 3] [1 2 3] ]
					""")
					.asGioviList()
					.type()
					.entryType()
			).isEqualTo(GioviType.LIST_OF_INT);
		}

		@Test
		void listOfMixedButCompatibleListOfPrimitive_shouldHaveGioviTypeOfListOfWidestType() {
			assertThat(
				visitExpression("""
					[ [1n 2n 3n] [1i 2i 3i] [1r 2r 3r] ]
					""")
					.asGioviList()
					.type()
					.entryType()
			).isEqualTo(GioviType.LIST_OF_REAL);
		}

		@Test
		// TODO: should it? Clarify requirements
		// When accessing a Real list, we know all entries are Real
		// or at least can be widened to Real (Int, Natural)
		// When is it wiser to widen the entry? What about a very deep tuple?
		void listOfMixedButCompatibleListOfPrimitive_shouldCastAllEntriesToWidestType() {
			assertThat(
				visitExpression("""
					[ [1n 2n 3n] [1i 2i 3i] [1r 2r 3r] ]
					""")
					.asGioviList()
					.value()
			).allSatisfy(
				entry -> assertThat(entry.getGioviType())
					.isEqualTo(GioviType.LIST_OF_REAL)
			);
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
				assertThat(
					visit("""
						x [Int]
						[1 2 3] -> x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redir_wideneableList_shouldWidenEntries() {
				assertThat(
					visit("""
						x [Real]
						[1 2 3] -> x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1d, 2d, 3d));
			}

			@Test
			void redir_nonWideneableList_shouldThrow() {
				assertThatExceptionOfType(OperatorNotSupported.class)
					.isThrownBy(() -> visit("""
						x [Int]
						[1r 2r 3r] -> x
						"""));
			}

			@Test
			void redirPlusValue_shouldInsert() {
				assertThat(
					visit("""
						[1 2] -> x [Int]
						3 ->+ x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirPlus_wideneableValue_shouldWiden() {
				assertThat(
					visit("""
						[1r 2r] -> x [Real]
						3 ->+ x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1d, 2d, 3d));
			}

			@Test
			void redirPlus_nonWideneableValue_shouldThrow() {
				assertThatExceptionOfType(OperatorNotSupported.class)
					.isThrownBy(() -> visit("""
						x [Int]
						1r ->+ x
						"""));
			}

			@Test
			void redirMinusValue_shouldRemove() {
				assertThat(
					visit("""
						[1 2 3 4] -> x [Int]
						4 ->- x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirMinusValue_shouldRemoveFirstOccurence() {
				assertThat(
					visit("""
						[1 2 4 3 4] -> x [Int]
						4 ->- x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1, 2, 3, 4));
			}

			@Test
			void redirMinus_narrowerValue_shouldRemove() {
				assertThat(
					visit("""
						[1 2 3 4] -> x [Int]
						4n ->- x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirPlusList_shouldInsertAll() {
				assertThat(
					visit("""
						[1 2 3] -> x [Int]
						x +<- [4 5 6]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1, 2, 3, 4, 5, 6));
			}

			@Test
			void redirMinusList_shouldRemoveAll() {
				assertThat(
					visit("""
						[1 2 3 4 5 6] -> x [Int]
						x -<- [4 5 6]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirMinus_narrowerList_shouldRemove() {
				assertThat(
					visit("""
						[1 2 3 4] -> x [Int]
						[4n] ->- x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirMinusList_shouldRemoveAllOccurences() {
				assertThat(
					visit("""
						[4 5 1 6 5 2 3 4 5 6] -> x [Int]
						[4 5 6] ->- x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void redirMinusValueOfMatrix_shouldRemoveFirstOccurence() {
				assertThat(
					visit("""
						x [[Int]] <- [
							[1 0 0]
							[1 1 1]
							[0 1 0]
							[0 0 1]
						]
						x -<- [1 1 1]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(
					GioviLists.of(
						List.of(1, 0, 0),
						List.of(0, 1, 0),
						List.of(0, 0, 1)
					)
				);
			}

			@Test
			void redirMinusMatrix_shouldRemoveAllOccurences() {
				assertThat(
					visit("""
						x [[Int]] <- [
							[1 0 0]
							[1 1 1]
							[0 1 0]
							[2 2 2]
							[0 0 1]
						]
						x -<- [[1 1 1] [2 2 2]]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(
					GioviLists.of(
						List.of(1, 0, 0),
						List.of(0, 1, 0),
						List.of(0, 0, 1)
					)
				);
			}

			@Test
			void redirToListAccess_shouldChangeValueAtPosition() {
				assertThat(
					visit("""
						[1 2 3] -> x [Int]
						69 -> x.1
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviLists.of(1, 69, 3));
			}

			@Test
			void redirToOutOfBoundListAccess_shouldThrow() {
				assertThatExceptionOfType(IndexOutOfBoundsException.class)
					.isThrownBy(() -> visit("""
						[1 2 3] -> x [Int]
						69 -> x.420
						"""));
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// arithmetic

		@Nested
		class Arithmetic {

			@Test
			void plus_list_shouldConcatenate() {
				assertThat(
					visitExpression("""
						[1 2] + [2 3]
						""")
				).isEqualTo(
					GioviLists.of(1, 2, 2, 3)
				);
			}

			@Test
			void plus_narrowerList_shouldConcatenate() {
				assertThat(
					visitExpression("""
						[1r 2r] + [2 3]
						""")
				).isEqualTo(
					GioviLists.of(1d, 2d, 2d, 3d)
				);
			}

			@Disabled("Clarify requirements")
			@Test
			void plus_widerList_shouldConcatenate() {
				assertThat(
					visitExpression("""
						[1 2] + [2r 3r]
						""")
				).isEqualTo(
					GioviLists.of(1d, 2d, 2d, 3d)
				);
			}

			@Test
			void plus_entry_shouldAdd() {
				assertThat(
					visitExpression("""
						[1 2] + 3
						""")
				).isEqualTo(
					GioviLists.of(1, 2, 3)
				);
			}

			@Test
			void plus_narrowerEntry_shouldAdd() {
				assertThat(
					visitExpression("""
						[1r 2r] + 3
						""")
				).isEqualTo(
					GioviLists.of(1d, 2d, 3d)
				);
			}

			@Disabled("Clarify requirements")
			@Test
			void plus_widerEntry_shouldAdd() {
				assertThat(
					visitExpression("""
						[1 2] + 3r
						""")
				).isEqualTo(
					GioviLists.of(1d, 2d, 3d)
				);
			}

			@Test
			void minus_list_shouldIntersecate() {
				assertThat(
					visitExpression("""
						[1 2 3 2] - [2 4]
						""")
				).isEqualTo(
					GioviLists.of(1, 3)
				);
			}

			@Test
			void minus_narrowerList_shouldIntersecate() {
				assertThat(
					visitExpression("""
						[1 2 3 2] - [2n 4n]
						""")
				).isEqualTo(
					GioviLists.of(1, 3)
				);
			}

			@Disabled("Clarify requirements")
			@Test
			void minus_widerList_shouldIntersecate() {
				assertThat(
					visitExpression("""
						[1 2 3 2] - [2r 4r]
						""")
				).isEqualTo(
					GioviLists.of(1d, 3d)
				);
			}

			// TODO(requirements): clarify

			@Test
			void minus_entry_shouldRemoveFirstEntry() {
				assertThat(
					visitExpression("""
						[1 3 2 3] - 3
						""")
				).isEqualTo(
					GioviLists.of(1, 2, 3)
				);
			}

			@Test
			void minus_narrowerEntry_shouldRemoveFirstEntry() {
				assertThat(
					visitExpression("""
					[1 3 2 3] - 3n
					""")
				).isEqualTo(
					GioviLists.of(1, 2, 3)
				);
			}

			@Disabled("Clarify requirements")
			@Test
			void minus_widerEntry_shouldRemoveFirstEntry() {
				assertThat(
					visitExpression("""
						[1 3 2 3] - 3r
						""")
				).isEqualTo(
					GioviLists.of(1d, 2d, 3d)
				);
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
					""").asGioviBool().value())
					.isTrue();
			}

			@Test
			void checkingIfContainsEqualEntryWithDifferentType_shouldReturnTrue() {
				assertThat(visitExpression("""
					[1i 2i 3i] =~ 1n
					""").asGioviBool().value())
					.isTrue();
			}

			@Test
			void checkingIfContainsEqualEntries_shouldReturnTrue() {
				assertThat(visitExpression("""
					[1 2 3] =~ [1 3]
					""").asGioviBool().value())
					.isTrue();
			}

			@Test
			void checkingIf_listOfList_contains_equalEntry_shouldReturnTrue() {
				assertThat(visitNativeBool("""
					[[1 1 1] [2 2 2] [3 3 3]] =~ [2 2 2]
					"""))
					.isTrue();
			}

			@Test
			void checkingIf_listOfList_contains_multipleEqualEntries_shouldReturnTrue() {
				assertThat(visitNativeBool("""
					[[1 1 1] [2 2 2] [3 3 3]] =~ [[1 1 1] [3 3 3]]
					"""))
					.isTrue();
			}

			@Test
			void checkingIf_listOfList_contains_notEqualEntry_shouldReturnFalse() {
				assertThat(visitNativeBool("""
					[[1 1 1] [2 2 2] [3 3 3]] =~ [1 69 1]
					"""))
					.isFalse();
			}

			@Test
			void checkingIf_listOfList_contains_multipleNotEqualEntries_shouldReturnFalse() {
				assertThat(visitNativeBool("""
					[[1 1 1] [2 2 2] [3 3 3]] =~ [[1 1 1] [2 69 2] [3 3 3]]
					"""))
					.isFalse();
			}

			@Test
			@Disabled("Clarify requirements")
			void checkingIf_listOfList_contains_narrowerButEqualEntry_shouldReturnTrue() {
				assertThat(visitNativeBool("""
					[[1 1 1] [2 2 2] [3 3 3]] =~ [1n 1n 1n]
					"""))
					.isTrue();
			}

			@Test
			@Disabled("Clarify requirements")
			void checkingIf_listOfList_contains_multipleNarrowerButEqualEntries_shouldReturnTrue() {
				assertThat(visitNativeBool("""
					[[1 1 1] [2 2 2] [3 3 3]] =~ [[1n 1n 1n] [3n 3n 3n]]
					"""))
					.isTrue();
			}

		}

		@Nested
		class Equality {

			@Test
			void comparingLists_withEqualValues_shouldBeTrue() {
				assertThat(visitExpression("""
					["hello" "world"] = ["hello" "world"]
					""").asGioviBool().value())
					.isTrue();
			}

			@Test
			void comparingLists_withEqualValues_butDifferentSize_shouldBeFalse() {
				assertThat(visitExpression("""
					["hello" "world"] = ["hello"]
					""").asGioviBool().value())
					.isFalse();
			}

			@Test
			void comparingLists_withDifferentValueTypes_shouldBeFalse() {
				assertThat(visitExpression("""
					[1 2] = ["hello" "world"]
					""").asGioviBool().value())
					.isFalse();
			}

			@Test
			void comparingLists_withDifferentButCompatibleValueTypes_shouldBeTrue() {
				assertThat(visitExpression("""
					[1n 2n] = [1r 2r]
					""").asGioviBool().value())
					.isTrue();
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// access

		@Nested
		class Access {

			@ParameterizedTest
			@CsvSource(textBlock = """
				[0 1 2 3 4].0, 0
				[0 1 2 3 4].1, 1
				[0 1 2 3 4].2, 2
				[0 1 2 3 4].3, 3
				[0 1 2 3 4].4, 4
				""")
			void accessWithInt_shouldReturnElementAtIndex(String expression, Integer expected) {
				assertThat(visitExpression(expression).unwrapAsGioviValue().value())
					.isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				[0 1 2 3 4].{-1}, 4
				[0 1 2 3 4].{-2}, 3
				[0 1 2 3 4].{-3}, 2
				[0 1 2 3 4].{-4}, 1
				[0 1 2 3 4].{-5}, 0
				""")
			void accessWithNegativeInt_shouldReturnElementAtRearIndex(
				String expression,
				Integer expected
			) {
				assertThat(visitExpression(expression).unwrapAsGioviValue().value())
					.isEqualTo(expected);
			}

			static Stream<Arguments> produceEqualGioviLists_range() {
				return Stream.of(
					Arguments.of("[0 1 2 3 4].0..4", GioviLists.of(0, 1, 2, 3, 4)),
					Arguments.of("[0 1 2 3 4].0..3", GioviLists.of(0, 1, 2, 3)),
					Arguments.of("[0 1 2 3 4].0..2", GioviLists.of(0, 1, 2)),
					Arguments.of("[0 1 2 3 4].0..1", GioviLists.of(0, 1)),
					Arguments.of("[0 1 2 3 4].0..0", GioviLists.of(0)),
					Arguments.of("[0 1 2 3 4].0..4", GioviLists.of(0, 1, 2, 3, 4)),
					Arguments.of("[0 1 2 3 4].1..4", GioviLists.of(1, 2, 3, 4)),
					Arguments.of("[0 1 2 3 4].2..4", GioviLists.of(2, 3, 4)),
					Arguments.of("[0 1 2 3 4].3..4", GioviLists.of(3, 4)),
					Arguments.of("[0 1 2 3 4].4..4", GioviLists.of(4)),
					Arguments.of("[0 1 2 3 4].1..3", GioviLists.of(1, 2, 3)),
					Arguments.of("[0 1 2 3 4].2..4", GioviLists.of(2, 3, 4))
				);
			}

			@ParameterizedTest
			@MethodSource("produceEqualGioviLists_range")
			void accessWithRange_shouldReturnSublist(String expression, GioviList expected) {
				assertEquals(expected, visitExpression(expression));
			}

			static Stream<Arguments> produceEqualGioviLists_inverseRange() {
				return Stream.of(
					Arguments.of("[0 1 2 3 4].4..0", GioviLists.of(4, 3, 2, 1, 0)),
					Arguments.of("[0 1 2 3 4].3..0", GioviLists.of(3, 2, 1, 0)),
					Arguments.of("[0 1 2 3 4].2..0", GioviLists.of(2, 1, 0)),
					Arguments.of("[0 1 2 3 4].1..0", GioviLists.of(1, 0)),
					Arguments.of("[0 1 2 3 4].4..1", GioviLists.of(4, 3, 2, 1)),
					Arguments.of("[0 1 2 3 4].4..2", GioviLists.of(4, 3, 2)),
					Arguments.of("[0 1 2 3 4].4..3", GioviLists.of(4, 3)),
					Arguments.of("[0 1 2 3 4].3..1", GioviLists.of(3, 2, 1)),
					Arguments.of("[0 1 2 3 4].4..2", GioviLists.of(4, 3, 2))
				);
			}

			@ParameterizedTest
			@MethodSource("produceEqualGioviLists_inverseRange")
			void accessWithInverseRange_shouldReturnSublistReversed(
				String expression,
				GioviList expected
			) {
				assertEquals(expected, visitExpression(expression));
			}


			@Test
			void accessWithLengthOperator_shouldReturnEntriesCount() {
				assertThat(visitExpression("""
					[0 1 2 3 4]._count
					""").asGioviInt().value())
					.isEqualTo(5);
			}

			@Test
			void accessWithIndexesOperator_shouldReturnRange() {
				assertThat(visitExpression("""
					[0 1 2 3 4]._range
					"""))
					.isEqualTo(Range.of(0, 4, 1));
			}

		}

		@Nested
		class Casts {

			@Test
			void testUpcasting() {
				assertThat(
					visitExpression("""
						[1 2 3] as [Real]
						""")
						.asGioviList()
						.value()
				).allSatisfy(
					entry -> assertThat(entry.getGioviType())
						.isEqualTo(GioviType.REAL)
				);
			}

			@Test
			void testDowncasting() {
				assertThat(
					visitExpression("""
					[1.69 2.69 3.69] as [Int]
					""")
						.asGioviList()
						.value()
				).allSatisfy(
					entry -> assertThat(entry.getGioviType())
						.isEqualTo(GioviType.INT)
				);
			}

			@Test
			void downcasting_shouldDowncastAllValues() {
				assertThat(visitExpression("""
					[1.69 2.69 3.69] as [Int]
					""")).isEqualTo(
					GioviLists.of(
						1, 2, 3
					)
				);

			}

		}

	}

	@Nested
	class NativeBindings {


		@Test
		void test_selfMutatingFunction_shouldMutateSameInstance() {
			// TODO: clarify requirements
			var visitor = visit("""
				l [Int]
				""");
			var l = visitor.lookup("l").orElseThrow();
			visit(visitor, """
				l.reverse()
				""");
			assertThat(visitor.lookup("l"))
				.containsSame(l);
		}

		@ParameterizedTest
		@CsvSource(textBlock = """
			[].count(), 0
			[1].count(), 1
			[1 2 3 4].count(), 4
			"""
		)
		void test_count(String code, int expected) {
			assertThat(visitExpression(code))
				.isEqualTo(new GioviInt(expected));
		}

		@ParameterizedTest
		@CsvSource(textBlock = """
			[].isEmpty(), true
			[1].isEmpty(), false
			"""
		)
		void test_isEmpty(String code, boolean expected) {
			assertThat(visitExpression(code))
				.isEqualTo(GioviBool.of(expected));
		}

		@Test
		void testThat_add_returnsSameInstance() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				""");
			var l = visitor.lookup("l").orElseThrow().unwrapAsGioviValue();
			assertThat(visitExpression(visitor, """
				l.add(69)
				"""))
				.isSameAs(l);
		}

		@Test
		void testThat_add_shouldAdd_atEnd() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				l.add(69)
				""");
			assertThat(visitor.lookup("l"))
				.<GioviValue<?>>map(GioviParserVisitResult::unwrapAsGioviValue)
				.contains(GioviLists.of(0, 1, 2, 3, 4, 69));
		}

		@Test
		void testThat_add_shouldAdd_atSpecifiedIndex() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				l.add(69, 2)
				l.add(420, -2)
				""");
			assertThat(visitor.lookup("l"))
				.<GioviValue<?>>map(GioviParserVisitResult::unwrapAsGioviValue)
				.contains(GioviLists.of(0, 1, 69, 2, 3, 420, 4));
		}

		@Test
		void test_replace() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				l.replace(index:1, element:69)
				""");
			assertThat(visitor.lookup("l"))
				.<GioviValue<?>>map(GioviParserVisitResult::unwrapAsGioviValue)
				.contains(GioviLists.of(0, 69, 2, 3, 4));
		}

		@Test
		void test_remove() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				l.remove(1)
				""");
			assertThat(visitor.lookup("l"))
				.<GioviValue<?>>map(GioviParserVisitResult::unwrapAsGioviValue)
				.contains(GioviLists.of(0, 2, 3, 4));
		}

		@Test
		void test_reverse() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				l.reverse()
				""");
			assertThat(visitor.lookup("l"))
				.<GioviValue<?>>map(GioviParserVisitResult::unwrapAsGioviValue)
				.contains(GioviLists.of(4, 3, 2, 1, 0));
		}

		@Test
		void test_reversed() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				""");
			var l = visitor.lookup("l").orElseThrow().unwrapAsGioviValue();
			assertThat(visitExpression(visitor, """
				l.reversed()
				"""))
				.isNotSameAs(l)
				.isEqualTo(GioviLists.of(4, 3, 2, 1, 0));
		}

		@Test
		void test_find() {
			var visitor = visit("""
				l [Real] <- [0r 1r 2r 3r 4r]
				""");
			assertThat(visitExpression(visitor, """
				l.find(2n)
				"""))
				.isEqualTo(
					GioviRecords.of(
						"found", true,
						"index", 2
					)
				);
		}

		@Test
		void testThat_find_notEqualElement_returnsFalse() {
			var visitor = visit("""
				l [Real] <- [0r 1r 2r 3r 4r]
				""");
			assertThat(visitExpression(visitor, """
				l.find(69)
				"""))
				.isEqualTo(
					GioviRecords.of(
						"found", false,
						"index", 0
					)
				);
		}

	}

}
