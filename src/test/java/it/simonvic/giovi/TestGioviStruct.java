package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import giovi.GFunction;
import giovi.Range;
import giovi.Struct;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.RedirectionException;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.lang.FunctionSignatureParam;
import it.simonvic.giovi.util.GioviLists;
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
				""").getCurrentScope().getDefinitionStruct("Foo").get())
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
				""").getCurrentScope().getDefinitionStruct("Foo").get())
				.isEqualTo(Structs.of(
					"Foo",
					"foo", VarD.of(0),
					"bar", VarD.of(false),
					"foobar", VarD.of("")));
		}

		@Test
		void testDefinitionWithDefault() {
			assertThat(visit("""
				Foo <
					i Int default 69
					b Bool default true
					t Text default "hello"
				>
				""").getCurrentScope().getDefinitionStruct("Foo").get())
				.isEqualTo(Structs.of(
					"Foo",
					"i", VarD.of(69, 69),
					"b", VarD.of(true, true),
					"t", VarD.of("hello", "hello")));
		}

		@Test
		void testDefinitionWithNestedStructField() {
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
				""")
				.getCurrentScope()
				.getDefinitionStruct("Person").get())
				.isEqualTo(Structs.of(
					"Person",
					"name", VarD.of(""),
					"favoriteColor", VarD.of(Structs.of(
						"RGB",
						"r", VarD.of(0),
						"g", VarD.of(0),
						"b", VarD.of(0)))));
		}


		@Test
		void testDefinitionWithNestedStructFieldAndDefaults() {
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
				""")
				.getCurrentScope()
				.getDefinitionStruct("Person").get())
				.isEqualTo(Structs.of(
					"Person",
					"name", VarD.of(""),
					"favoriteColor", VarD.of(
						Structs.of(
							"RGB",
							"r", VarD.of(255, 0),
							"g", VarD.of(250, 0),
							"b", VarD.of(200, 0)),
						Structs.of(
							"RGB",
							"r", VarD.of(255, 0),
							"g", VarD.of(250, 0),
							"b", VarD.of(200, 0)))));
		}

		@Test
		void testConstants() {
			assertThat(visit("""
				Foo <
					a Int
					b Int
					constant BAR Real <- 6.9
					constant BAZ [Int] <- [1 2 3]
				>
				""")
				.getCurrentScope()
				.getDefinitionStruct("Foo").get()
				.getConstants())
				.isEqualTo(Map.of(
					"BAR", new GioviReal(6.9),
					"BAZ", GioviLists.of(1, 2, 3)));
		}

		@Disabled("Fix constants definition getting lost")
		@Test
		void testConstantsWithSameDeclaringType() {
			var expectedDefinition = new Struct(
				"Foo", new LinkedHashMap<>(Map.of(
					"a", VarD.of(0),
					"b", VarD.of(0))),
				new LinkedHashMap<>(),
				new LinkedHashMap<>());
			expectedDefinition.getConstants().put("FOO", new GioviStruct(expectedDefinition));
			assertThat(visit("""
				Foo <
					a Int
					b Int
					constant FOO Foo <- Foo.<69 420>
				>
				""")
				.getCurrentScope()
				.getDefinitionStruct("Foo").get())
				.isEqualTo(expectedDefinition);
		}

		@Disabled("Find a way to test functions equality (different bodyctx)")
		@Test
		void testFunctions() {
			assertThat(visit("""
				Foo <
					foo() {}
					bar(a Real, b [Int] default [1 2 3]) => Text {}
				>
				""")
				.getCurrentScope()
				.getDefinitionStruct("Foo")
				.get()
				.getFunctions())
				.isEqualTo(Map.of(
					"foo", new GioviFunction(new GFunction(new FunctionSignature(
						"foo",
						new LinkedHashMap<>(),
						new GioviTypeVoid()))),
					"bar", new GioviFunction(new GFunction(new FunctionSignature(
						"bar",
						new LinkedHashMap<>(Map.of(
							"a", new FunctionSignatureParam("a", new GioviTypeReal()),
							"b", new FunctionSignatureParam("b", GioviLists.of(1, 2, 3)))),
						new GioviTypeText())))));
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
				""").getCurrentScope().getDefinitionStruct("RGBA").get())
				.isEqualTo(Structs.of(
					"RGBA",
					"r", VarD.of(0),
					"g", VarD.of(0),
					"b", VarD.of(0),
					"a", VarD.of(0)));
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
				""").getCurrentScope().getDefinitionStruct("RGBA").get())
				.isEqualTo(Structs.of(
					"RGBA",
					"r", VarD.of(255, 255),
					"g", VarD.of(255, 255),
					"b", VarD.of(255, 255),
					"a", VarD.of(69, 69)));
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
			var rgb = visitor.getCurrentScope().getDefinitionStruct("RGB").get();
			var argb = visitor.getCurrentScope().getDefinitionStruct("RGBA").get();
			assertAll(
				() -> assertThat(rgb).isEqualTo(Structs.of(
					"RGB",
					"r", VarD.of(255, 255),
					"g", VarD.of(255, 255),
					"b", VarD.of(255, 255))),
				() -> assertThat(argb).isEqualTo(Structs.of(
					"RGBA",
					"r", VarD.of(250, 250),
					"g", VarD.of(200, 200),
					"b", VarD.of(150, 150),
					"a", VarD.of(0))));
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////// CREATIONS

	@Nested
	class Creation {

		@Test
		void whenCreatingStruct_withNoFields_shouldCreateStructWithDefaultValues() {
			var visitor = visit("""
				Foo <
					i Int
					t Text
					b Bool
				>
				""");
			assertThat(visitExpression(visitor, """
				Foo.<>
				""")).isEqualTo(GioviStructs.of(
				"Foo",
				"i", VarD.of(0),
				"t", VarD.of(""),
				"b", VarD.of(false)));
		}

		@Test
		void whenCreatingStruct_withPositionalFields_shouldCreateStructWithSpecifiedValues() {
			var visitor = visit("""
				Foo <
					i Int
					t Text
					b Bool
				>
				""");
			assertThat(visitExpression(visitor, """
				Foo.<
					69
					"hello"
					true
				>
				""")).isEqualTo(GioviStructs.of(
				"Foo",
				"i", VarD.of(69),
				"t", VarD.of("hello"),
				"b", VarD.of(true)));
		}

		@Test
		void whenCreatingStruct_withNamedFields_shouldCreateStructWithSpecifiedValues() {
			var visitor = visit("""
				Foo <
					i Int
					t Text
					b Bool
				>
				""");
			assertThat(visitExpression(visitor, """
				Foo.<
					i: 69
					t: "hello"
					b: true
				>
				""")).isEqualTo(GioviStructs.of(
				"Foo",
				"i", VarD.of(69),
				"t", VarD.of("hello"),
				"b", VarD.of(true)));
		}

		@Test
		void whenCreatingStruct_withUnorderedNamedFields_shouldCreateStructWithSpecifiedValues() {
			var visitor = visit("""
				Foo <
					i Int
					t Text
					b Bool
				>
				""");
			assertThat(visitExpression(visitor, """
				Foo.<
					b: true
					i: 69
					t: "hello"
				>
				""")).isEqualTo(GioviStructs.of(
				"Foo",
				"i", VarD.of(69),
				"t", VarD.of("hello"),
				"b", VarD.of(true)));
		}

		@Test
		void whenCreatingStructOfUndefinedType_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visitExpression("""
				Foo.<69 "hello" true>
				"""));
		}

		@Test
		void whenCreatingStruct_withWrongFieldsCount_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				Foo <
					i Int
					t Text
					b Bool
				>
				Foo.<69 "hello" true 420> -> foo Foo
				"""));
		}

		@Test
		void whenCreatingStruct_withWrongFieldsName_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				Foo <
					i Int
					t Text
					b Bool
				>
				Foo.<x:1 y:"hello" z:true> -> foo Foo
				"""));
		}

		@Test
		void whenCreatingStruct_withSubsetOfFields_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> visit("""
				Foo <
					i Int
					t Text
					b Bool
				>
				Foo.<i:69 b:true> -> foo Foo
				"""));
		}

		@Test
		void whenCreatingStruct_withSubsetOfFields_shouldOnlyChangeSpecifiedFields() {
			var visitor = visit("""
				Foo <
					i Int
					t Text
					b Bool
				>
				""");
			assertThat(visitExpression(visitor, """
				Foo.<
					i: 69
					b: true
				>
				""")).isEqualTo(GioviStructs.of(
				"Foo",
				"i", VarD.of(69),
				"t", VarD.of(""),
				"b", VarD.of(true)));
		}

		@Test
		void whenCreatingStruct_withDefault_shouldUseDefaultValue() {
			var visitor = visit("""
				Bar <
					r Real default 4.20
				>
				Foo <
					i Int default 69
					t Text default "foo"
					s Bar default <6.9>
				>
				""");
			assertThat(visitExpression(visitor, """
				Foo.<
					i: 420
					t: default
					s: default
				>
				""")).isEqualTo(GioviStructs.of(
				"Foo",
				"i", VarD.of(420, 69),
				"t", VarD.of("foo", "foo"),
				"s", VarD.of(
					Structs.of("Bar", "r", VarD.of(6.9, 4.20)),
					Structs.of("Bar", "r", VarD.of(6.9, 4.20)))));
		}

		@Disabled("Not implemented yet")
		@Test
		void whenCreatingStruct_withPlaceholder_shouldUseDefaultValue() {
			var visitor = visit("""
				Bar <
					r Real default 4.20
				>
				Foo <
					i Int default 69
					t Text default "foo"
					s Bar default <6.9>
				>
				""");
			assertThat(visitExpression(visitor, """
				Foo.<
					i: 420
					t: _
					s: _
				>
				""")).isEqualTo(GioviStructs.of(
				"Foo",
				"i", VarD.of(420, 69),
				"t", VarD.of("foo", "foo"),
				"s", VarD.of(
					Structs.of("Bar", "r", VarD.of(6.9, 4.20)),
					Structs.of("Bar", "r", VarD.of(6.9, 4.20)))));
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
						"r", VarD.of(1),
						"g", VarD.of(2),
						"b", VarD.of(3)));
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
						"r", VarD.of(1),
						"g", VarD.of(2),
						"b", VarD.of(3)));
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
						"r", VarD.of(1),
						"g", VarD.of(2),
						"b", VarD.of(3),
						"a", VarD.of(0)));
			}

			@Test
			void whenCastingToSubsetType_shouldThrow() {
				assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
					RGB <r Int, g Int, b Int>
					RGBA <r Int, g Int, b Int, a Int>
					RGBA.<> as RGB -> rgb RGB
					"""));
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// redirection

		@Nested
		class Redirection {

			@Test
			void redir_compatibleTuple_shouldChangeField() {
				assertThat(visit("""
					Foo <i Int, t Text, b Bool>
					x Foo
					x <- <69 "hello" true>
					""").getCurrentScope()
					.getVariable("x")
					.get()
					.value())
					.isEqualTo(GioviStructs.of(
						"Foo",
						"i", VarD.of(69),
						"t", VarD.of("hello"),
						"b", VarD.of(true)));
			}

			@Test
			void redir_tupleWithLessFields_shouldThrow() {
				assertThatExceptionOfType(RedirectionException.class).isThrownBy(() -> visit("""
					Foo <i Int, t Text, b Bool>
					x Foo
					x <- <69 "hello">
					"""));
			}

			@Test
			void redir_tupleWithMoreFields_shouldThrow() {
				assertThatExceptionOfType(RedirectionException.class).isThrownBy(() -> visit("""
					Foo <i Int, t Text, b Bool>
					x Foo
					x <- <69 "hello" false 420>
					"""));
			}

			@Test
			void redir_compatibleRecord_shouldChangeFields() {
				assertThat(visit("""
					Foo <i Int, t Text, b Bool>
					x Foo
					x <- <i:69 t:"hello" b:true>
					""").getCurrentScope()
					.getVariable("x")
					.get()
					.value())
					.isEqualTo(GioviStructs.of(
						"Foo",
						"i", VarD.of(69),
						"t", VarD.of("hello"),
						"b", VarD.of(true)));
			}

			@Test
			@Disabled("Candidate not implemented")
			void redirPlus_narrowerRecord_shouldChangeFields() {
				assertThat(visit("""
					Foo <i Int, t Text, b Bool>
					x Foo
					x +<- <i:69 t:true>
					""").getCurrentScope()
					.getVariable("x")
					.get()
					.value())
					.isEqualTo(GioviStructs.of(
						"Foo",
						"i", VarD.of(69),
						"t", VarD.of(""),
						"b", VarD.of(true)));
			}

			@Test
			void redir_record_withDifferentNames_shouldThrow() {
				assertThatExceptionOfType(NoSuchField.class).isThrownBy(() -> visit("""
					Foo <i Int, t Text, b Bool>
					x Foo
					x <- <foo:69 t:"hello" b:true>
					"""));
			}

			@Test
			void redir_toField_shouldChangeValue() {
				assertThat(visit("""
					Foo <i Int, t Text, b Bool>
					x Foo
					x.i <- 69
					x.t <- "hello"
					x.b <- true
					""").getCurrentScope()
					.getVariable("x")
					.get()
					.value())
					.isEqualTo(GioviStructs.of(
						"Foo",
						"i", VarD.of(69),
						"t", VarD.of("hello"),
						"b", VarD.of(true)));
			}

			@Test
			void redir_struct_shouldChangeFields() {
				assertThat(visit("""
					Foo <i Int, t Text, b Bool>
					x Foo
					x <- Foo.<i:69 t:"hello" b:true>
					""").getCurrentScope()
					.getVariable("x")
					.get()
					.value())
					.isEqualTo(GioviStructs.of(
						"Foo",
						"i", VarD.of(69),
						"t", VarD.of("hello"),
						"b", VarD.of(true)));
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
				assertThat(visitExpression(visitor, code)
					.asVar()
					.value()
					.value())
					.isEqualTo(expected);
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
				assertThat(visitExpression(visitor, code)
					.asVar()
					.value()
					.value())
					.isEqualTo(expected);
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
				assertThat(visitExpression(visitor, """
					Foobar.<>@%
					""")
					.asGioviValue()
					.value())
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
