package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import giovi.Range;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.util.GioviStructs;
import it.simonvic.giovi.util.GioviTuples;
import it.simonvic.giovi.util.Structs;

public class TestGioviStruct {


	//////////////////////////////////////////////////////////////////////////////////////////////// DEFINITION

	@Nested
	class Definition {

		@Test
		void testEmptyStruct() {
			assertThat(visit("""
				Foo <>
				""").getCurrentScope().getDefinitionStruct("Foo"))
				.isEqualTo(Structs.of("Foo"));
		}

		@Test
		void testDefinition() {
			assertThat(visit("""
				Foo <
					foo Int
					bar Bool
					foobar Text
				>
				""").getCurrentScope().getDefinitionStruct("Foo"))
				.isEqualTo(Structs.of(
					"Foo",
					"foo", new GioviInt(),
					"bar", new GioviBool(),
					"foobar", new GioviText()));
		}

		@Test
		void testDefinitionWithDefault() {
			assertThat(visit("""
				Foo <
					foo Int default 69
					bar Bool default true
					foobar Text default "hello"
				>
				""").getCurrentScope().getDefinitionStruct("Foo"))
				.isEqualTo(Structs.of(
					"Foo",
					"foo", new GioviInt(69, 69),
					"bar", new GioviBool(true, true),
					"foobar", new GioviText("hello", "hello")));
		}

		@Test
		void testDefinitionWithStructField() {
			assertThat(visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				Person <
					name Text
					favoriteColor RGB
				>
				""").getCurrentScope().getDefinitionStruct("Person"))
				.isEqualTo(Structs.of(
					"Person",
					"name", new GioviText(),
					"favoriteColor", GioviStructs.of(
						"RGB",
						"r", new GioviInt(),
						"g", new GioviInt(),
						"b", new GioviInt())));
		}


		@Test
		void testDefinitionWithStructFieldAndDefaults() {
			assertThat(visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				Person <
					name Text
					favoriteColor RGB default RGB.<
						r: 255
						g: 250
						b: 200
					>
				>
				""").getCurrentScope().getDefinitionStruct("Person"))
				.isEqualTo(Structs.of(
					"Person",
					"name", new GioviText(),
					"favoriteColor", GioviStructs.of(
						"RGB",
						"r", new GioviInt(255, 255),
						"g", new GioviInt(250, 250),
						"b", new GioviInt(200, 200))));
		}


		@Disabled("Not implemented yet")
		@Test
		void testFunctions() {

		}

		@Disabled("Not implemented yet")
		@Test
		void testNestedDefinition() {

		}

		@Test
		void testInheritance() {
			assertThat(visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB : RGBA <
					a Int
				>
				""").getCurrentScope().getDefinitionStruct("RGBA"))
				.isEqualTo(Structs.of(
					"RGBA",
					"r", new GioviInt(),
					"g", new GioviInt(),
					"b", new GioviInt(),
					"a", new GioviInt()));
		}

		@Test
		void testInheritanceWithDefaults() {
			assertThat(visit("""
				RGB <
					r Int default 255
					g Int default 255
					b Int default 255
				>
				RGB : RGBA <
					a Int default 69
				>
				""").getCurrentScope().getDefinitionStruct("RGBA"))
				.isEqualTo(Structs.of(
					"RGBA",
					"r", new GioviInt(255, 255),
					"g", new GioviInt(255, 255),
					"b", new GioviInt(255, 255),
					"a", new GioviInt(69, 69)));
		}

		@Test
		void testInheritedFieldsDefaultOverride() {
			var visitor = visit("""
				RGB <
					r Int default 255
					g Int default 255
					b Int default 255
				>
				RGB : RGBA <
					r Int default 250
					g Int default 200
					b Int default 150
					a Int
				>
				""");
			var rgb = visitor.getCurrentScope().getDefinitionStruct("RGB");
			var argb = visitor.getCurrentScope().getDefinitionStruct("RGBA");
			assertAll(
				() -> assertThat(rgb).isEqualTo(Structs.of(
					"RGB",
					"r", new GioviInt(255, 255),
					"g", new GioviInt(255, 255),
					"b", new GioviInt(255, 255))),
				() -> assertThat(argb).isEqualTo(Structs.of(
					"RGBA",
					"r", new GioviInt(250, 250),
					"g", new GioviInt(200, 200),
					"b", new GioviInt(150, 150),
					"a", new GioviInt())));
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////// CREATIONS

	@Nested
	class Creation {

		@Test
		void whenCreatingStruct_withNoFields_shouldCreateStructWithDefaultValues() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				""");
			assertThat(visitExpression(visitor, """
				RGB.<>
				""")).isEqualTo(GioviStructs.of(
				"RGB",
				"r", new GioviInt(),
				"g", new GioviInt(),
				"b", new GioviInt()));
		}

		@Test
		void whenCreatingStruct_withUnnamedFields_shouldCreateStructWithSpecifiedValues() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				""");
			assertThat(visitExpression(visitor, """
				RGB.<
					1
					2
					3
				>
				""")).isEqualTo(GioviStructs.of(
				"RGB",
				"r", new GioviInt(1),
				"g", new GioviInt(2),
				"b", new GioviInt(3)));
		}

		@Test
		void whenCreatingStruct_withNamedFields_shouldCreateStructWithSpecifiedValues() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				""");
			assertThat(visitExpression(visitor, """
				RGB.<
					r: 1
					g: 2
					b: 3
				>
				""")).isEqualTo(GioviStructs.of(
				"RGB",
				"r", new GioviInt(1),
				"g", new GioviInt(2),
				"b", new GioviInt(3)));
		}

		@Test
		void whenCreatingStruct_withUnorderedNamedFields_shouldCreateStructWithSpecifiedValues() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				""");
			assertThat(visitExpression(visitor, """
				RGB.<
					g: 2
					b: 3
					r: 1
				>
				""")).isEqualTo(GioviStructs.of(
				"RGB",
				"r", new GioviInt(1),
				"g", new GioviInt(2),
				"b", new GioviInt(3)));
		}

		@Test
		void whenCreatingStructOfUndefinedType_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visitExpression("""
				Foo.<69 420 "lol">
				"""));
		}

		@Test
		void whenCreatingStruct_withWrongFieldsCount_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB.<1 2 3 4>
				"""));
		}

		@Test
		void whenCreatingStruct_withWrongFieldsName_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB.<red:1 g:2 b:3>
				"""));
		}

		@Test
		void whenCreatingStruct_withSubsetOfFields_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB.<r:1 g:2>
				"""));
		}

		@Test
		void whenCreatingStruct_withSubsetOfFields_shouldOnlyChangeSpecifiedFields() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				""");
			assertThat(visitExpression(visitor, """
				RGB.<
					r: 1
					b: 3
				>
				""")).isEqualTo(GioviStructs.of(
				"RGB",
				"r", new GioviInt(1),
				"g", new GioviInt(),
				"b", new GioviInt(3)));
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	@Nested
	class Operations {

		//////////////////////////////////////////////////////////////////////////////////////////// casting

		@Nested
		class Casting {

			@Test
			void testCastingToSameType() {
				var visitor = visit("""
					Color <r Int, g Int, b Int>
					""");
				assertThat(visitExpression(visitor, """
					Color.<1 2 3> as Color
					"""))
					.isEqualTo(GioviStructs.of(
						"Color",
						"r", new GioviInt(1),
						"g", new GioviInt(2),
						"b", new GioviInt(3)));
			}

			@Test
			@Disabled("Needs requirements clarification")
			void testCastingToCompatibleType() {
				var visitor = visit("""
					Color <r Int, g Int, b Int>
					RGB <r Int, g Int, b Int>
					""");
				assertThat(visitExpression(visitor, """
					Color.<1 2 3> as RGB
					"""))
					.isEqualTo(GioviStructs.of(
						"RGB",
						"r", new GioviInt(1),
						"g", new GioviInt(2),
						"b", new GioviInt(3)));
			}

			@Test
			@Disabled("Needs requirements clarification")
			void testCastingToSupersetType() {
				var visitor = visit("""
					RGB <r Int, g Int, b Int>
					RGBA <r Int, g Int, b Int, a Int>
					""");
				assertThat(visitExpression(visitor, """
					RGB.<1 2 3> as RGBA
					"""))
					.isEqualTo(GioviStructs.of(
						"RGBA",
						"r", new GioviInt(1),
						"g", new GioviInt(2),
						"b", new GioviInt(3),
						"a", new GioviInt()));
			}

			@Test
			void whenCastingToSubsetType_shouldThrow() {
				assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
					RGB <r Int, g Int, b Int>
					RGBA <r Int, g Int, b Int, a Int>
					RGBA.<> as RGB
					"""));
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// redirection

		@Nested
		class Redirection {

			@Test
			void whenRedirectingCompatibleTuple_shouldChangeField() {
				assertThat(visit("""
					Color <r Int, g Int, b Int>
					x Color
					<255 69 42> -> x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviStructs.of(
							"Color",
							"r", new GioviInt(255),
							"g", new GioviInt(69),
							"b", new GioviInt(42)));
			}

			@Test
			void whenRedirectingSubsetTuple_shouldThrow() {
				assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
					Color <r Int, g Int, b Int>
					x Color
					<69 42> -> x
					"""));
			}

			@Test
			void whenRedirectingCompatibleNamedTuple_shouldChangeFields() {
				assertThat(visit("""
					Color <r Int, g Int, b Int>
					x Color
					<b:42 r:255 g:69> -> x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviStructs.of(
							"Color",
							"r", new GioviInt(255),
							"g", new GioviInt(69),
							"b", new GioviInt(42)));
			}

			@Test
			@Disabled("Candidate not implemented")
			void redirPlusSubsetNamedTuple_shouldChangeOnlySpecifiedFields() {
				assertThat(visit("""
					Color <r Int, g Int, b Int>
					x Color
					<b:42 g:69> ->+ x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviStructs.of(
							"Color",
							"r", new GioviInt(),
							"g", new GioviInt(69),
							"b", new GioviInt(42)));
			}

			@Test
			void whenRedirectingIncompatibleNamedTuple_shouldThrow() {
				assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
					Color <r Int, g Int, b Int>
					x Color
					<red:255 b:42 g:69> -> x
					"""));
			}

			@Test
			void redir_toField_shouldChangeValue() {
				assertThat(visit("""
					Color <r Int, g Int, b Int>
					x Color
					x.r <- 255
					x.g <- 69
					x.b <- 42
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviStructs.of(
							"Color",
							"r", new GioviInt(255),
							"g", new GioviInt(69),
							"b", new GioviInt(42)));
			}


		}

		//////////////////////////////////////////////////////////////////////////////////////////// arithmetic
		//////////////////////////////////////////////////////////////////////////////////////////// boolean
		//////////////////////////////////////////////////////////////////////////////////////////// relational

		@Nested
		class Equality {

		}

		//////////////////////////////////////////////////////////////////////////////////////////// access

		@Nested
		class Access {

			@ParameterizedTest
			@CsvSource(textBlock = """
				Foobar.<0 1 2 3>@0, 0
				Foobar.<0 1 2 3>@1, 1
				Foobar.<0 1 2 3>@2, 2
				Foobar.<0 1 2 3>@3, 3
				""")
			void access_withInt_shouldReturnFieldAtIndex(String code, Integer expected) {
				var visitor = visit("""
					Foobar <a Int, b Int, c Int, d Int>
					""");
				assertThat(visitNativeValue(visitor, code)).isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				Foobar.<0 1 2 3>@{-1}, 3
				Foobar.<0 1 2 3>@{-2}, 2
				Foobar.<0 1 2 3>@{-3}, 1
				Foobar.<0 1 2 3>@{-4}, 0
				""")
			void access_withNegativeInt_shouldReturnFieldAtRearIndex(
				String code,
				Integer expected) {
				var visitor = visit("""
					Foobar <a Int, b Int, c Int, d Int>
					""");
				assertThat(visitNativeValue(visitor, code)).isEqualTo(expected);
			}


			static Stream<Arguments> produceArgs_access_withRange() {
				return Stream.of(
					Arguments.of("Foobar.<0 1 2 3 4>@0..4", GioviTuples.of(0, 1, 2, 3, 4)),
					Arguments.of("Foobar.<0 1 2 3 4>@0..3", GioviTuples.of(0, 1, 2, 3)),
					Arguments.of("Foobar.<0 1 2 3 4>@0..2", GioviTuples.of(0, 1, 2)),
					Arguments.of("Foobar.<0 1 2 3 4>@0..1", GioviTuples.of(0, 1)),
					Arguments.of("Foobar.<0 1 2 3 4>@0..0", GioviTuples.of(0)),
					Arguments.of("Foobar.<0 1 2 3 4>@1..4", GioviTuples.of(1, 2, 3, 4)),
					Arguments.of("Foobar.<0 1 2 3 4>@2..4", GioviTuples.of(2, 3, 4)),
					Arguments.of("Foobar.<0 1 2 3 4>@3..4", GioviTuples.of(3, 4)),
					Arguments.of("Foobar.<0 1 2 3 4>@4..4", GioviTuples.of(4)),
					Arguments.of("Foobar.<0 1 2 3 4>@1..3", GioviTuples.of(1, 2, 3)),
					Arguments.of("Foobar.<0 1 2 3 4>@2..4", GioviTuples.of(2, 3, 4)));
			}

			@ParameterizedTest
			@MethodSource("produceArgs_access_withRange")
			void access_withRange_shouldReturnSubset(String code, GioviTuple expected) {
				var visitor = visit("""
					Foobar <a Int, b Int, c Int, d Int, e Int>
					""");
				assertThat(visitValue(visitor, code)).isEqualTo(expected);
			}

			static Stream<Arguments> produceArgs_access_withInverseRange() {
				return Stream.of(
					Arguments.of("Foobar.<0 1 2 3 4>@4..0", GioviTuples.of(4, 3, 2, 1, 0)),
					Arguments.of("Foobar.<0 1 2 3 4>@3..0", GioviTuples.of(3, 2, 1, 0)),
					Arguments.of("Foobar.<0 1 2 3 4>@2..0", GioviTuples.of(2, 1, 0)),
					Arguments.of("Foobar.<0 1 2 3 4>@1..0", GioviTuples.of(1, 0)),
					Arguments.of("Foobar.<0 1 2 3 4>@4..1", GioviTuples.of(4, 3, 2, 1)),
					Arguments.of("Foobar.<0 1 2 3 4>@4..2", GioviTuples.of(4, 3, 2)),
					Arguments.of("Foobar.<0 1 2 3 4>@4..3", GioviTuples.of(4, 3)),
					Arguments.of("Foobar.<0 1 2 3 4>@3..1", GioviTuples.of(3, 2, 1)),
					Arguments.of("Foobar.<0 1 2 3 4>@4..2", GioviTuples.of(4, 3, 2)));
			}

			@ParameterizedTest
			@MethodSource("produceArgs_access_withInverseRange")
			void access_withInverseRange_shouldReturnSubsetReversed(
				String code,
				GioviTuple expected) {
				var visitor = visit("""
					Foobar <a Int, b Int, c Int, d Int, e Int>
					""");
				assertThat(visitValue(visitor, code)).isEqualTo(expected);
			}

			static Stream<Arguments> produceArgs_access_withStepRange() {
				return Stream.of(
					Arguments.of("Foobar.<0 1 2 3 4>@0..4..2", GioviTuples.of(0, 2, 4)),
					Arguments.of("Foobar.<0 1 2 3 4>@0..3..2", GioviTuples.of(0, 2)),
					Arguments.of("Foobar.<0 1 2 3 4>@0..2..2", GioviTuples.of(0, 2)),
					Arguments.of("Foobar.<0 1 2 3 4>@0..1..2", GioviTuples.of(0)),
					Arguments.of("Foobar.<0 1 2 3 4>@0..0..2", GioviTuples.of(0)),
					Arguments.of("Foobar.<0 1 2 3 4>@1..4..2", GioviTuples.of(1, 3)),
					Arguments.of("Foobar.<0 1 2 3 4>@2..4..2", GioviTuples.of(2, 4)),
					Arguments.of("Foobar.<0 1 2 3 4>@3..4..2", GioviTuples.of(3)),
					Arguments.of("Foobar.<0 1 2 3 4>@4..4..2", GioviTuples.of(4)),
					Arguments.of("Foobar.<0 1 2 3 4>@1..3..2", GioviTuples.of(1, 3)),
					Arguments.of("Foobar.<0 1 2 3 4>@2..4..2", GioviTuples.of(2, 4)));
			}

			@ParameterizedTest
			@MethodSource("produceArgs_access_withStepRange")
			void access_withStepRange_shouldReturnSubset(String code, GioviTuple expected) {
				var visitor = visit("""
					Foobar <a Int, b Int, c Int, d Int, e Int>
					""");
				assertThat(visitValue(visitor, code)).isEqualTo(expected);
			}


			static Stream<Arguments> produceArgs_inverseStepRange() {
				return Stream.of(
					Arguments.of("Foobar.<0 1 2 3 4>@4..0..2", GioviTuples.of(4, 2, 0)),
					Arguments.of("Foobar.<0 1 2 3 4>@3..0..2", GioviTuples.of(3, 1)),
					Arguments.of("Foobar.<0 1 2 3 4>@2..0..2", GioviTuples.of(2, 0)),
					Arguments.of("Foobar.<0 1 2 3 4>@4..1..2", GioviTuples.of(4, 2)),
					Arguments.of("Foobar.<0 1 2 3 4>@4..2..2", GioviTuples.of(4, 2)),
					Arguments.of("Foobar.<0 1 2 3 4>@4..3..2", GioviTuples.of(4)),
					Arguments.of("Foobar.<0 1 2 3 4>@3..1..2", GioviTuples.of(3, 1)),
					Arguments.of("Foobar.<0 1 2 3 4>@4..2..2", GioviTuples.of(4, 2)));
			}

			@ParameterizedTest
			@MethodSource("produceArgs_inverseStepRange")
			void access_withInverseStepRange_shouldReturnSubsetReversed(
				String code,
				GioviTuple expected) {
				var visitor = visit("""
					Foobar <a Int, b Int, c Int, d Int, e Int>
					""");
				assertThat(visitValue(visitor, code)).isEqualTo(expected);
			}

			@Test
			void access_withLengthOperator_shouldReturnFieldsCount() {
				var visitor = visit("""
					Foobar <a Int, b Int, c Int, d Int, e Int>
					""");
				assertThat(visitNativeValue(visitor, """
					Foobar.<>@%
					"""))
					.isEqualTo(5);
			}

			@Test
			void accessWithIndexesOperator_shouldReturnRange() {
				var visitor = visit("""
					Foobar <a Int, b Int, c Int, d Int, e Int>
					""");
				assertThat(visitExpression(visitor, """
					Foobar.<>@@
					"""))
					.isEqualTo(Range.of(0, 4, 1));
			}


		}

	}

}
