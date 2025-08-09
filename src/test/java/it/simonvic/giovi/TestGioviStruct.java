package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import giovi.RecordFieldSignature;
import giovi.StructSignature;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.RedirectionException;
import it.simonvic.giovi.lang.FormalParameter;
import it.simonvic.giovi.lang.FormalParameters;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviRecords;
import it.simonvic.giovi.util.GioviStructs;
import it.simonvic.giovi.util.Structs;

public class TestGioviStruct {


	//////////////////////////////////////////////////////////////////////////////////////////////// DEFINITION

	@Nested
	class Definition {

		@Test
		void testEmptyStruct() {
			assertThat(visit("""
				Foo <>
				""").getCurrentScope()
				.getStructType("Foo").orElseThrow()
				.signature())
				.isEqualTo(GioviStructs.signatureOf("Foo"));
		}

		@Test
		void testDefinition() {
			assertThat(visit("""
				Foo <
					foo Int
					bar Bool
					foobar Text
				>
				""").getCurrentScope()
				.getStructType("Foo").orElseThrow()
				.signature())
				.isEqualTo(GioviStructs.signatureOf(
					"Foo",
					"foo", GioviInt.TYPE,
					"bar", GioviBool.TYPE,
					"foobar", GioviText.TYPE));
		}

		@Test
		void testDefinitionWithDefault() {
			assertThat(visit("""
				Foo <
					i Int default 69
					b Bool default true
					t Text default "hello"
				>
				""").getCurrentScope()
				.getStructType("Foo").orElseThrow()
				.signature())
				.isEqualTo(new StructSignature(
					"Foo",
					new RecordFieldSignature("i", GioviInt.TYPE, new GioviInt(69)),
					new RecordFieldSignature("b", GioviBool.TYPE, new GioviBool(true)),
					new RecordFieldSignature("t", GioviText.TYPE, new GioviText("hello"))));
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
				.getStructType("Person").orElseThrow()
				.signature())
				.isEqualTo(GioviStructs.signatureOf(
					"Person",
					"name", GioviText.TYPE,
					"favoriteColor", new GioviTypeStruct(GioviStructs.signatureOf(
						"RGB",
						"r", GioviInt.TYPE,
						"g", GioviInt.TYPE,
						"b", GioviInt.TYPE))));
		}


		@Test
		void testDefinitionWithNestedStructFieldAndDefaults() {
			var rgbType = new GioviTypeStruct(GioviStructs.signatureOf(
				"RGB",
				"r", GioviInt.TYPE,
				"g", GioviInt.TYPE,
				"b", GioviInt.TYPE));
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
				.getStructType("Person").orElseThrow()
				.signature())
				.isEqualTo(new StructSignature(
					"Person",
					new RecordFieldSignature("name", GioviText.TYPE),
					new RecordFieldSignature(
						"favoriteColor",
						rgbType,
						new GioviStruct(rgbType, Structs.of(
							"RGB",
							"r", VarD.of(255, 0),
							"g", VarD.of(250, 0),
							"b", VarD.of(200, 0))))));
		}

		@Test
		void testConstants() {
			assertThat(visit("""
				Foo <
					a Int
					b Int
					const BAR Real <- 6.9
					const BAZ [Int] <- [1 2 3]
				>
				""")
				.getCurrentScope()
				.getStructType("Foo").orElseThrow()
				.signature()
				.constants())
				.isEqualTo(Map.of(
					"BAR", new GioviReal(6.9),
					"BAZ", GioviLists.of(1, 2, 3)));
		}

		@Disabled("Stackoverflow because of cyclic definition")
		@Test
		void testConstantsWithSameDeclaringType() {
			var expectedType = new GioviTypeStruct(GioviStructs.signatureOf(
				"Foo",
				"a", GioviInt.TYPE,
				"b", GioviInt.TYPE));
			expectedType.signature()
				.constants().put("FOO", expectedType.spawn());
			assertThat(visit("""
				Foo <
					a Int
					b Int
					const FOO Foo <- Foo.<>
				>
				""")
				.getCurrentScope()
				.getStructType("Foo").orElseThrow())
				.isEqualTo(expectedType);
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
				.getStructType("Foo").orElseThrow()
				.signature()
				.functions())
				.isEqualTo(Map.of(
					"foo", new GioviFunction(new GFunction(new FunctionSignature(
						"foo",
						new FormalParameters(),
						new GioviTypeVoid()))),
					"bar", new GioviFunction(new GFunction(new FunctionSignature(
						"bar",
						new FormalParameters(
							new FormalParameter("a", GioviReal.TYPE),
							new FormalParameter("b", GioviLists.of(1, 2, 3))),
						GioviText.TYPE)))));
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
				""").getCurrentScope()
				.getStructType("RGBA").orElseThrow()
				.signature())
				.isEqualTo(GioviStructs.signatureOf(
					"RGBA",
					"r", GioviInt.TYPE,
					"g", GioviInt.TYPE,
					"b", GioviInt.TYPE,
					"a", GioviInt.TYPE));
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
				""").getCurrentScope()
				.getStructType("RGBA").orElseThrow()
				.signature())
				.isEqualTo(new StructSignature(
					"RGBA",
					new RecordFieldSignature("r", GioviInt.TYPE, new GioviInt(255)),
					new RecordFieldSignature("g", GioviInt.TYPE, new GioviInt(255)),
					new RecordFieldSignature("b", GioviInt.TYPE, new GioviInt(255)),
					new RecordFieldSignature("a", GioviInt.TYPE, new GioviInt(69))));
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
			var rgb = visitor.getCurrentScope().getStructType("RGB").orElseThrow();
			var argb = visitor.getCurrentScope().getStructType("RGBA").orElseThrow();
			assertAll(
				() -> assertThat(rgb).isEqualTo(new GioviTypeStruct(new StructSignature(
					"RGB",
					new RecordFieldSignature("r", GioviInt.TYPE, new GioviInt(255)),
					new RecordFieldSignature("g", GioviInt.TYPE, new GioviInt(255)),
					new RecordFieldSignature("b", GioviInt.TYPE, new GioviInt(255))))),
				() -> assertThat(argb).isEqualTo(new GioviTypeStruct(new StructSignature(
					"RGBA",
					new RecordFieldSignature("r", GioviInt.TYPE, new GioviInt(250)),
					new RecordFieldSignature("g", GioviInt.TYPE, new GioviInt(200)),
					new RecordFieldSignature("b", GioviInt.TYPE, new GioviInt(150)),
					new RecordFieldSignature("a", GioviInt.TYPE, new GioviInt(0))))));
		}

		@Nested
		class Recursive {

			@Test
			void recursiveStructs() {
				visit("""

					Node <
						label Text
						next self

						self toText() => Text {
							text Text
							self.label ->+ text
							if self.next.label != Node.label do {
								"->" ->+ text
								self.next.toText() ->+ text
							}
							=> text
						}

						self toTextLazy() => Text {
							text Text
							self.label ->+ text
							zelf Node <- self
							if zelf.next.label != Node.label do {
								"->" ->+ text
								zelf.next.toTextLazy() ->+ text
							}
							=> text
						}
					>

					Node -> _out
					"lazy" -> _out
					root Node <- Node.<label: "root">
					root.next.next.next.next.label <- "foo"
					root.toTextLazy() -> _out
					root.next.label <- "a"
					root.toTextLazy() -> _out
					root.next.next.label <- "b"
					root.toTextLazy() -> _out
					root.next.next.next.label <- "c"
					root.toTextLazy() -> _out
					"-------" -> _out

					"eager" -> _out
					root <- Node.<label: "root">
					root.next.next.next.next.label <- "foo"
					root.toText() -> _out
					root.next.label <- "a"
					root.toText() -> _out
					root.next.next.label <- "b"
					root.toText() -> _out
					root.next.next.next.label <- "c"
					root.toText() -> _out
					"-------" -> _out

					""");
			}


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
			@Disabled("Not implemented yet")
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
				Integer expected
			) {
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
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@0..4",
						GioviRecords.of("a", 0, "b", 1, "c", 2, "d", 3, "e", 4)),
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@0..3",
						GioviRecords.of("a", 0, "b", 1, "c", 2, "d", 3)),
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@0..2",
						GioviRecords.of("a", 0, "b", 1, "c", 2)),
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@0..1",
						GioviRecords.of("a", 0, "b", 1)),
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@0..0",
						GioviRecords.of("a", 0)),
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@1..4",
						GioviRecords.of("b", 1, "c", 2, "d", 3, "e", 4)),
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@2..4",
						GioviRecords.of("c", 2, "d", 3, "e", 4)),
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@3..4",
						GioviRecords.of("d", 3, "e", 4)),
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@4..4",
						GioviRecords.of("e", 4)),
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@1..3",
						GioviRecords.of("b", 1, "c", 2, "d", 3)),
					Arguments.of(
						"Foo.<a:0 b:1 c:2 d:3 e:4>@2..4",
						GioviRecords.of("c", 2, "d", 3, "e", 4)));
			}

			@ParameterizedTest
			@MethodSource("produceArgs_access_withRange")
			void access_withRange_shouldReturnSubset(String code, GioviRecord expected) {
				var visitor = visit("""
					Foo <a Int, b Int, c Int, d Int, e Int>
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

	@Nested
	class Recursion {


		@Test
		void foo() {
			visit("""
			Node <
				data Text
				next self
			>
			n Node <- <data: "root">
			n.next.data <- "1"
			n.next.next.data <- "2"
			n.next.next.next.data <- "3"
			n -> _out
			repeat if n.data != default {
				n.data -> _out
				n <- n.next
			}
			""");
		}

		@Test
		void bar() {
			visit("""
			Node <
				label Text
				next self
				self foo() => self {
					self -> _out
					self.next.foo -> _out
					=> self
				}
			>
			root Node
			root.foo() -> _out
			""");
		}

	}

}
