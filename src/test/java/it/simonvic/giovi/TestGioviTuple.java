package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import giovi.Range;
import giovi.lang.GioviTuple;
import giovi.lang.GioviValue;
import it.simonvic.giovi.util.GioviTuples;

public class TestGioviTuple {

	//////////////////////////////////////////////////////////////////////////////////////////////// CREATIONS

	@Nested
	class Creation {

		@Test
		void testCreation_emptyTuple() {
			assertThat(visitExpression("""
				<>
				""")).isEqualTo(GioviTuples.of(List.of()));
		}

		@Test
		void testCreation_tupleWithMultipleFields() {
			assertThat(visitExpression("""
				<69 true "foo">
				""")).isEqualTo(GioviTuples.of(List.of(
				69, true, "foo")));
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	@Nested
	class Operations {

		//////////////////////////////////////////////////////////////////////////////////////////// redirection

		@Disabled("Candidate not implemented yet")
		@Nested
		class Redirection {

			@Test
			void redir() {
				visit("""
					<1 true "hello"> -> tuple Tuple
					""");
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// arithmetic
		//////////////////////////////////////////////////////////////////////////////////////////// boolean
		//////////////////////////////////////////////////////////////////////////////////////////// relational

		@Nested
		class Equality {

			@Test
			void tuples_withEqualValues_shouldBeTrue() {
				assertThat(visitNativeBool("""
					<1 true "hello"> = <1 true "hello">
					"""))
					.isTrue();
			}

			@Test
			void tuples_withEqualValuesOfDifferentButCompatibleType_shouldBeTrue() {
				assertThat(visitNativeBool("""
					<1n true "hello"> = <1r true "hello">
					"""))
					.isTrue();
			}

			@Test
			void tuples_withAtLeastOneDifferentValue_shouldBeFalse() {
				assertThat(visitNativeBool("""
					<1 true "hello"> = <1 true "HELLO">
					"""))
					.isFalse();
			}

			@Test
			void tuples_withDifferentValueTypes_shouldBeFalse() {
				assertThat(visitNativeBool("""
					<1 true "hello"> = <1 true 69>
					"""))
					.isFalse();
			}

			@Test
			void tuples_withEqualValuesButInDifferentOrder_shouldBeFalse() {
				assertThat(visitNativeBool("""
					<1 true "hello"> = <true 1 "hello">
					"""))
					.isFalse();
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// access

		@Nested
		class Access {

			@ParameterizedTest
			@CsvSource(textBlock = """
				<0 1 2 3>@0, 0
				<0 1 2 3>@1, 1
				<0 1 2 3>@2, 2
				<0 1 2 3>@3, 3
				""")
			void access_withInt_shouldReturnFieldAtIndex(String code, Integer expected) {
				assertThat(visitNativeValue(code)).isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				<0 1 2 3>@{-1}, 3
				<0 1 2 3>@{-2}, 2
				<0 1 2 3>@{-3}, 1
				<0 1 2 3>@{-4}, 0
				""")
			void access_withNegativeInt_shouldReturnFieldAtRearIndex(
				String code,
				Integer expected) {
				assertThat(visitValue(code)).isEqualTo(GioviValue.of(expected));
			}


			static Stream<Arguments> produceArgs_access_withRange() {
				return Stream.of(
					Arguments.of("<0 1 2 3 4>@0..4", GioviTuples.of(0, 1, 2, 3, 4)),
					Arguments.of("<0 1 2 3 4>@0..3", GioviTuples.of(0, 1, 2, 3)),
					Arguments.of("<0 1 2 3 4>@0..2", GioviTuples.of(0, 1, 2)),
					Arguments.of("<0 1 2 3 4>@0..1", GioviTuples.of(0, 1)),
					Arguments.of("<0 1 2 3 4>@0..0", GioviTuples.of(0)),
					Arguments.of("<0 1 2 3 4>@1..4", GioviTuples.of(1, 2, 3, 4)),
					Arguments.of("<0 1 2 3 4>@2..4", GioviTuples.of(2, 3, 4)),
					Arguments.of("<0 1 2 3 4>@3..4", GioviTuples.of(3, 4)),
					Arguments.of("<0 1 2 3 4>@4..4", GioviTuples.of(4)),
					Arguments.of("<0 1 2 3 4>@1..3", GioviTuples.of(1, 2, 3)),
					Arguments.of("<0 1 2 3 4>@2..4", GioviTuples.of(2, 3, 4)));
			}

			@ParameterizedTest
			@MethodSource("produceArgs_access_withRange")
			void access_withRange_shouldReturnSubset(String code, GioviTuple expected) {
				assertThat(visitValue(code)).isEqualTo(expected);
			}

			static Stream<Arguments> produceArgs_access_withInverseRange() {
				return Stream.of(
					Arguments.of("<0 1 2 3 4>@4..0", GioviTuples.of(4, 3, 2, 1, 0)),
					Arguments.of("<0 1 2 3 4>@3..0", GioviTuples.of(3, 2, 1, 0)),
					Arguments.of("<0 1 2 3 4>@2..0", GioviTuples.of(2, 1, 0)),
					Arguments.of("<0 1 2 3 4>@1..0", GioviTuples.of(1, 0)),
					Arguments.of("<0 1 2 3 4>@4..1", GioviTuples.of(4, 3, 2, 1)),
					Arguments.of("<0 1 2 3 4>@4..2", GioviTuples.of(4, 3, 2)),
					Arguments.of("<0 1 2 3 4>@4..3", GioviTuples.of(4, 3)),
					Arguments.of("<0 1 2 3 4>@3..1", GioviTuples.of(3, 2, 1)),
					Arguments.of("<0 1 2 3 4>@4..2", GioviTuples.of(4, 3, 2)));
			}

			@ParameterizedTest
			@MethodSource("produceArgs_access_withInverseRange")
			void access_withInverseRange_shouldReturnSubsetReversed(
				String code,
				GioviTuple expected) {
				assertThat(visitValue(code)).isEqualTo(expected);
			}

			static Stream<Arguments> produceArgs_access_withStepRange() {
				return Stream.of(
					Arguments.of("<0 1 2 3 4>@0..4..2", GioviTuples.of(0, 2, 4)),
					Arguments.of("<0 1 2 3 4>@0..3..2", GioviTuples.of(0, 2)),
					Arguments.of("<0 1 2 3 4>@0..2..2", GioviTuples.of(0, 2)),
					Arguments.of("<0 1 2 3 4>@0..1..2", GioviTuples.of(0)),
					Arguments.of("<0 1 2 3 4>@0..0..2", GioviTuples.of(0)),
					Arguments.of("<0 1 2 3 4>@1..4..2", GioviTuples.of(1, 3)),
					Arguments.of("<0 1 2 3 4>@2..4..2", GioviTuples.of(2, 4)),
					Arguments.of("<0 1 2 3 4>@3..4..2", GioviTuples.of(3)),
					Arguments.of("<0 1 2 3 4>@4..4..2", GioviTuples.of(4)),
					Arguments.of("<0 1 2 3 4>@1..3..2", GioviTuples.of(1, 3)),
					Arguments.of("<0 1 2 3 4>@2..4..2", GioviTuples.of(2, 4)));
			}

			@ParameterizedTest
			@MethodSource("produceArgs_access_withStepRange")
			void access_withStepRange_shouldReturnSubset(String code, GioviTuple expected) {
				assertThat(visitValue(code)).isEqualTo(expected);
			}


			static Stream<Arguments> produceArgs_inverseStepRange() {
				return Stream.of(
					Arguments.of("<0 1 2 3 4>@4..0..2", GioviTuples.of(4, 2, 0)),
					Arguments.of("<0 1 2 3 4>@3..0..2", GioviTuples.of(3, 1)),
					Arguments.of("<0 1 2 3 4>@2..0..2", GioviTuples.of(2, 0)),
					Arguments.of("<0 1 2 3 4>@4..1..2", GioviTuples.of(4, 2)),
					Arguments.of("<0 1 2 3 4>@4..2..2", GioviTuples.of(4, 2)),
					Arguments.of("<0 1 2 3 4>@4..3..2", GioviTuples.of(4)),
					Arguments.of("<0 1 2 3 4>@3..1..2", GioviTuples.of(3, 1)),
					Arguments.of("<0 1 2 3 4>@4..2..2", GioviTuples.of(4, 2)));
			}

			@ParameterizedTest
			@MethodSource("produceArgs_inverseStepRange")
			void access_withInverseStepRange_shouldReturnSubsetReversed(
				String code,
				GioviTuple expected) {
				assertThat(visitValue(code)).isEqualTo(expected);
			}

			@Test
			void access_withLengthOperator_shouldReturnFieldsCount() {
				assertThat(visitNativeValue("""
					<0 1 2 3 4>@%
					"""))
					.isEqualTo(5);
			}

			@Test
			void accessWithIndexesOperator_shouldReturnRange() {
				assertThat(visitExpression("""
					<0 1 2 3 4>@@
					"""))
					.isEqualTo(Range.of(0, 5, 1));
			}


		}
	}
}
