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

import giovi.*;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.RedirectionException;
import it.simonvic.giovi.lang.FormalParameter;
import it.simonvic.giovi.lang.FormalParameters;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.util.*;

public class TestGioviStruct {


	//////////////////////////////////////////////////////////////////////////////////////////////// DEFINITION

	@Nested
	class Definition {

		@Test
		void testEmptyStruct() {
			assertThat(
				visit("""
					Foo <>
					""")
					.getCurrentScope()
					.getStructType("Foo").orElseThrow()
					.signature()
			).isEqualTo(
				GioviStructs.signatureOf("Foo")
			);
		}

		@Test
		void testDefinition() {
			assertThat(
				visit("""
					Foo <
						foo:Int
						bar:Bool
						foobar:Text
					>
					""")
					.getCurrentScope()
					.getStructType("Foo").orElseThrow()
					.signature()
			).isEqualTo(
				GioviStructs.signatureOf(
					"Foo",
					"foo", GioviType.INT,
					"bar", GioviType.BOOL,
					"foobar", GioviType.TEXT
				)
			);
		}

		@Test
		void testDefinitionWithDefault() {
			assertThat(
				visit("""
					Foo <
						i:Int default 69
						b:Bool default true
						t:Text default "hello"
					>
					""")
					.getCurrentScope()
					.getStructType("Foo").orElseThrow()
					.signature()
			).isEqualTo(
				new StructSignature(
					"Foo",
					new RecordFieldSignature("i", GioviType.INT, new GioviInt(69)),
					new RecordFieldSignature("b", GioviType.BOOL, GioviBool.TRUE),
					new RecordFieldSignature("t", GioviType.TEXT, new GioviText("hello"))
				)
			);
		}

		@Test
		void testDefinitionWithNestedStructField() {
			assertThat(
				visit("""
					RGB <
						r:Int
						g:Int
						b:Int
					>
					Person <
						name:Text
						favoriteColor:RGB
					>
					""")
					.getCurrentScope()
					.getStructType("Person").orElseThrow()
					.signature()
			).isEqualTo(
				GioviStructs.signatureOf(
					"Person",
					"name", GioviType.TEXT,
					"favoriteColor", new GioviTypeStruct(
						GioviStructs.signatureOf(
							"RGB",
							"r", GioviType.INT,
							"g", GioviType.INT,
							"b", GioviType.INT
						)
					)
				)
			);
		}


		@Test
		void testDefinitionWithNestedStructFieldAndDefaults() {
			var rgbType = new GioviTypeStruct(
				GioviStructs.signatureOf(
					"RGB",
					"r", GioviType.INT,
					"g", GioviType.INT,
					"b", GioviType.INT
				)
			);
			assertThat(
				visit("""
					RGB <
						r:Int
						g:Int
						b:Int
					>
					Person <
						name:Text
						favoriteColor:RGB default RGB.<
							r=255
							g=250
							b=200
						>
					>
					""")
					.getCurrentScope()
					.getStructType("Person").orElseThrow()
					.signature()
			).isEqualTo(
				new StructSignature(
					"Person",
					new RecordFieldSignature("name", GioviType.TEXT),
					new RecordFieldSignature(
						"favoriteColor",
						rgbType,
						new GioviStruct(
							rgbType, GStructs.of(
								"RGB",
								"r", GFields.of(255, 0),
								"g", GFields.of(250, 0),
								"b", GFields.of(200, 0)
							)
						)
					)
				)
			);
		}

		@Test
		void testConstants() {
			assertThat(
				visit("""
					Foo <
						a:Int
						b:Int
						const BAR:Real <- 6.9
						const BAZ:[Int] <- [1 2 3]
					>
					""")
					.getCurrentScope()
					.getStructType("Foo").orElseThrow()
					.signature()
					.constants()
			).isEqualTo(
				Map.of(
					"BAR", new GioviReal(6.9),
					"BAZ", GioviLists.of(1, 2, 3)
				)
			);
		}

		@Disabled("Stackoverflow because of cyclic definition")
		@Test
		void testConstantsWithSameDeclaringType() {
			var expectedType = new GioviTypeStruct(
				GioviStructs.signatureOf(
					"Foo",
					"a", GioviType.INT,
					"b", GioviType.INT
				)
			);
			expectedType.signature().constants().put("FOO", expectedType.spawn());
			assertThat(
				visit("""
					Foo <
						a:Int
						b:Int
						const FOO Foo <- Foo.<>
					>
					""")
					.getCurrentScope()
					.getStructType("Foo").orElseThrow()
			).isEqualTo(expectedType);
		}

		@Disabled("Find a way to test functions equality (different bodyctx)")
		@Test
		void testFunctions() {
			assertThat(
				visit("""
					Foo <
						foo() {}
						bar(a:Real, b [Int] default [1 2 3]) => Text {}
					>
					""")
					.getCurrentScope()
					.getStructType("Foo").orElseThrow()
					.signature()
					.functions()
			).isEqualTo(
				Map.of(
					"foo", new GioviFunction(
						new GFunction(
							new FunctionSignature(
								"foo",
								new FormalParameters(),
								GioviType.VOID
							)
						)
					),
					"bar", new GioviFunction(
						new GFunction(
							new FunctionSignature(
								"bar",
								new FormalParameters(
									new FormalParameter("a", GioviType.REAL),
									new FormalParameter("b", GioviLists.of(1, 2, 3))
								),
								GioviType.TEXT
							)
						)
					)
				)
			);
		}

		@Disabled("Not implemented yet")
		@Test
		void testNestedDefinition() {

		}

		@Test
		void testInheritance() {
			assertThat(
				visit("""
					RGB <
						r:Int
						g:Int
						b:Int
					>
					RGB + RGBA <
						a:Int
					>
					""")
					.getCurrentScope()
					.getStructType("RGBA").orElseThrow()
					.signature()
			).isEqualTo(
				GioviStructs.signatureOf(
					"RGBA",
					"r", GioviType.INT,
					"g", GioviType.INT,
					"b", GioviType.INT,
					"a", GioviType.INT
				)
			);
		}

		@Test
		void testInheritanceWithDefaults() {
			assertThat(
				visit("""
					RGB <
						r:Int default 255
						g:Int default 255
						b:Int default 255
					>
					RGB + RGBA <
						a:Int default 69
					>
					""")
					.getCurrentScope()
					.getStructType("RGBA").orElseThrow()
					.signature()
			).isEqualTo(
				new StructSignature(
					"RGBA",
					new RecordFieldSignature("r", GioviType.INT, new GioviInt(255)),
					new RecordFieldSignature("g", GioviType.INT, new GioviInt(255)),
					new RecordFieldSignature("b", GioviType.INT, new GioviInt(255)),
					new RecordFieldSignature("a", GioviType.INT, new GioviInt(69))
				)
			);
		}

		@Test
		void testInheritedFieldsDefaultOverride() {
			var visitor = visit("""
				RGB <
					r:Int default 255
					g:Int default 255
					b:Int default 255
				>
				RGB + RGBA <
					r:Int default 250
					g:Int default 200
					b:Int default 150
					a:Int
				>
				""");
			var rgb = visitor.getCurrentScope().getStructType("RGB").orElseThrow();
			var argb = visitor.getCurrentScope().getStructType("RGBA").orElseThrow();
			assertAll(
				() -> assertThat(rgb).isEqualTo(
					new GioviTypeStruct(
						new StructSignature(
							"RGB",
							new RecordFieldSignature("r", GioviType.INT, new GioviInt(255)),
							new RecordFieldSignature("g", GioviType.INT, new GioviInt(255)),
							new RecordFieldSignature("b", GioviType.INT, new GioviInt(255))
						)
					)
				),
				() -> assertThat(argb).isEqualTo(
					new GioviTypeStruct(
						new StructSignature(
							"RGBA",
							new RecordFieldSignature("r", GioviType.INT, new GioviInt(250)),
							new RecordFieldSignature("g", GioviType.INT, new GioviInt(200)),
							new RecordFieldSignature("b", GioviType.INT, new GioviInt(150)),
							new RecordFieldSignature("a", GioviType.INT, new GioviInt(0))
						)
					)
				)
			);
		}

		@Nested
		class Recursive {

			@Test
			void test_instantiation_default() {
				visit("""
					Node <data:Text, next:self>
					Node.<> -> _out
					""");
			}

			@Test
			void test_instantiation_withFields() {
				visit("""
					Node <data:Text, next:self>
					Node.<data="root"> -> _out
					""");
			}

			@Test
			void test_instantiation_withSelfField() {
				visit("""
					Node <data:Text, next:self>
					Node.<"root", Node.<>> -> _out
					Node.<data="root", next=Node.<>> -> _out
					Node.<data="root", next=<data="child">> -> _out
					""");
			}

			@Test
			void test_redirection_default() {
				visit("""
					Node <data:Text, next:self>
					root:Node <- Node.<>
					""");
			}

			@Test
			void test_redirection_withField() {
				visit("""
					Node <data:Text, next:self>
					root:Node <- Node.<data="root">
					""");
			}

			@Test
			void test_redirection_withSelfField() {
				visit("""
					Node <data:Text, next:self>
					root:Node <- <
						data="root",
						next=<
							data="child1"
							next=<
								data="child2"
							>
						>
					>
					""");
			}

			@Test
			void recursiveStructs() {
				visit("""

					Node <
						label:Text
						next:self

						self toText() => Text {
							text:Text
							self.label ->+ text
							if self.next.label != Node.label do {
								"->" ->+ text
								self.next.toText() ->+ text
							}
							=> text
						}

						self toTextLazy() => Text {
							text:Text
							self.label ->+ text
							zelf:Node <- self
							if zelf.next.label != Node.label do {
								"->" ->+ text
								zelf.next.toTextLazy() ->+ text
							}
							=> text
						}
					>

					Node -> _out
					"lazy" -> _out
					root:Node <- Node.<label="root">
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
					root <- Node.<label="root">
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
					i:Int
					t:Text
					b:Bool
				>
				""");
			assertThat(
				visitExpression(visitor, """
					Foo.<>
					""")
			).isEqualTo(
				GioviStructs.of(
					"Foo",
					"i", GFields.of(0),
					"t", GFields.of(""),
					"b", GFields.of(false)
				)
			);
		}

		@Test
		void whenCreatingStruct_withPositionalFields_shouldCreateStructWithSpecifiedValues() {
			var visitor = visit("""
				Foo <
					i:Int
					t:Text
					b:Bool
				>
				""");
			assertThat(
				visitExpression(visitor, """
					Foo.<
						69
						"hello"
						true
					>
					""")
			).isEqualTo(
				GioviStructs.of(
					"Foo",
					"i", GFields.of(69),
					"t", GFields.of("hello"),
					"b", GFields.of(true)
				)
			);
		}

		@Test
		void whenCreatingStruct_withNominalFields_shouldCreateStructWithSpecifiedValues() {
			var visitor = visit("""
				Foo <
					i:Int
					t:Text
					b:Bool
				>
				""");
			assertThat(
				visitExpression(visitor, """
					Foo.<
						i=69
						t="hello"
						b=true
					>
					""")
			).isEqualTo(
				GioviStructs.of(
					"Foo",
					"i", GFields.of(69),
					"t", GFields.of("hello"),
					"b", GFields.of(true)
				)
			);
		}

		@Test
		void whenCreatingStruct_withUnorderedNominalFields_shouldCreateStructWithSpecifiedValues() {
			var visitor = visit("""
				Foo <
					i:Int
					t:Text
					b:Bool
				>
				""");
			assertThat(
				visitExpression(visitor, """
					Foo.<
						b=true
						i=69
						t="hello"
					>
					""")
			).isEqualTo(
				GioviStructs.of(
					"Foo",
					"i", GFields.of(69),
					"t", GFields.of("hello"),
					"b", GFields.of(true)
				)
			);
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
					i:Int
					t:Text
					b:Bool
				>
				Foo.<69 "hello" true 420> -> foo:Foo
				"""));
		}

		@Test
		void whenCreatingStruct_withWrongFieldsName_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				Foo <
					i:Int
					t:Text
					b:Bool
				>
				Foo.<x=1 y="hello" z=true> -> foo:Foo
				"""));
		}

		@Test
		void whenCreatingStruct_withSubsetOfFields_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> visit("""
				Foo <
					i:Int
					t:Text
					b:Bool
				>
				Foo.<i=69 b=true> -> foo:Foo
				"""));
		}

		@Test
		void whenCreatingStruct_withSubsetOfFields_shouldOnlyChangeSpecifiedFields() {
			var visitor = visit("""
				Foo <
					i:Int
					t:Text
					b:Bool
				>
				""");
			assertThat(
				visitExpression(visitor, """
					Foo.<
						i=69
						b=true
					>
					""")
			).isEqualTo(
				GioviStructs.of(
					"Foo",
					"i", GFields.of(69),
					"t", GFields.of(""),
					"b", GFields.of(true)
				)
			);
		}

		@Test
		void whenCreatingStruct_withDefault_shouldUseDefaultValue() {
			var visitor = visit("""
				Bar <
					r:Real default 4.20
				>
				Foo <
					i:Int default 69
					t:Text default "foo"
					s:Bar default <6.9>
				>
				""");
			assertThat(
				visitExpression(visitor, """
					Foo.<
						i=420
						t=default
						s=default
					>
					""")
			).isEqualTo(
				GioviStructs.of(
					"Foo",
					"i", GFields.of(420, 69),
					"t", GFields.of("foo", "foo"),
					"s", GFields.of(
						GStructs.of("Bar", "r", GFields.of(6.9, 4.20)),
						GStructs.of("Bar", "r", GFields.of(6.9, 4.20))
					)
				)
			);
		}

		@Disabled("Not implemented yet")
		@Test
		void whenCreatingStruct_withPlaceholder_shouldUseDefaultValue() {
			var visitor = visit("""
				Bar <
					r:Real default 4.20
				>
				Foo <
					i:Int default 69
					t:Text default "foo"
					s:Bar default <6.9>
				>
				""");
			assertThat(
				visitExpression(visitor, """
					Foo.<
						i=420
						t=_
						s=_
					>
					""")
			).isEqualTo(
				GioviStructs.of(
					"Foo",
					"i", GFields.of(420, 69),
					"t", GFields.of("foo", "foo"),
					"s", GFields.of(
						GStructs.of("Bar", "r", GFields.of(6.9, 4.20)),
						GStructs.of("Bar", "r", GFields.of(6.9, 4.20))
					)
				)
			);
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	@Nested
	class Operations {

		//////////////////////////////////////////////////////////////////////////////////////////// casting

		@Nested
		class Casting {

			@Test
			void casting_toTuple_withEqualTypes() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Natural, b:Text, c:Bool>
							"""),
						"""
						Foo.<a=69n b="hello" c=true> as tuple<Natural, Text, Bool>
						"""
					)
				).isEqualTo(
					GioviTuples.of(new Natural(69), "hello", true)
				);
			}

			@Test
			void casting_toTuple_withWiderTypes() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Natural, b:Text, c:Int>
							"""),
						"""
						Foo.<a=69n b="hello" c=420i> as tuple<Int, Text, Real>
						"""
					)
				).isEqualTo(
					GioviTuples.of(69, "hello", 420d)
				);
			}

			@Test
			void casting_toTuple_withWiderSignature() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Natural, b:Int>
							"""),
						"""
						Foo.<a=69n b=420i> as tuple<Real, Real, Real>
						"""
					)
				).isEqualTo(
					GioviTuples.of(69d, 420d, 0d)
				);
			}

			@Test
			void casting_toTuple_withNarrowerTypes() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Int, b:Real>
							"""),
						"""
						Foo.<a=1i b=2r> as tuple<Natural, Int>
						"""
					)
				).isEqualTo(
					GioviTuples.of(new Natural(1), 2)
				);
			}

			@Test
			void casting_toTuple_withNarrowerSignature() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Real, b:Real, c:Real>
							"""),
						"""
						Foo.<a=1r b=2r c=3r> as tuple<Natural, Int>
						"""
					)
				).isEqualTo(
					GioviTuples.of(new Natural(1), 2)
				);
			}

			@Test
			void casting_toRecord_withEqualSignature() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Natural, b:Text, c:Bool>
							"""),
						"""
						Foo.<a=69n b="hello" c=true> as record<a:Natural, b:Text, c:Bool>
						"""
					)
				).isEqualTo(
					GioviRecords.of(
						"a", new Natural(69),
						"b", "hello",
						"c", true
					)
				);
			}

			@Test
			void casting_toRecord_withWiderSignature() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Natural, b:Int>
							"""),
						"""
						Foo.<a=69n b=420i> as record<a:Int, b:Real, c:Real>
						"""
					)
				).isEqualTo(
					GioviRecords.of(
						"a", 69,
						"b", 420d,
						"c", 0d
					)
				);
			}

			@Test
			void casting_toRecord_withNarrowerSignature() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Natural, b:Int, c:Real>
							"""),
						"""
						Foo.<a=69n b=420i c=6.9> as record<b:Natural, c:Int>
						"""
					)
				).isEqualTo(
					GioviRecords.of(
						"b", new Natural(420),
						"c", 6
					)
				);
			}

			@Test
			void casting_toStruct_sameType() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Int, b:Int, c:Int>
							"""),
						"""
						Foo.<1 2 3> as Foo
						"""
					)
				).isEqualTo(
					GioviStructs.ofNative(
						"Foo",
						"a", 1,
						"b", 2,
						"c", 3
					)
				);
			}

			@Test
			void casting_toStruct_withEqualSignature_butDifferentTypename() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Int, b:Int, c:Int>
							struct Bar <a:Int, b:Int, c:Int>
							"""),
						"""
						Foo.<1 2 3> as Bar
						"""
					)
				).isEqualTo(
					GioviStructs.ofNative(
						"Bar",
						"a", 1,
						"b", 2,
						"c", 3
					)
				);
			}

			@Test
			void casting_toStruct_withWiderSignature() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Natural, b:Int>
							struct Bar <a:Int, b:Real, c:Text>
							"""),
						"""
						Foo.<1n 2> as Bar
						"""
					)
				).isEqualTo(
					GioviStructs.ofNative(
						"Bar",
						"a", 1,
						"b", 2d,
						"c", ""
					)
				);
			}

			@Test
			void casting_toStruct_withNarrowerSignature() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a:Real, b:Int, c:Natural>
							struct Bar <a:Int, b:Natural>
							"""),
						"""
						Foo.<a=1r b=2i c=3n> as Bar
						"""
					)
				).isEqualTo(
					GioviStructs.ofNative(
						"Bar",
						"a", 1,
						"b", new Natural(2)
					)
				);
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// redirection

		@Nested
		class Redirection {

			@Test
			void redir_compatibleTuple_shouldChangeField() {
				assertThat(
					visit("""
						Foo <i:Int, t:Text, b:Bool>
						x:Foo
						x <- <69 "hello" true>
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(
					GioviStructs.of(
						"Foo",
						"i", GFields.of(69),
						"t", GFields.of("hello"),
						"b", GFields.of(true)
					)
				);
			}

			@Test
			void redir_tupleWithLessFields_shouldThrow() {
				assertThatExceptionOfType(RedirectionException.class).isThrownBy(() -> visit("""
					Foo <i:Int, t:Text, b:Bool>
					x:Foo
					x <- <69 "hello">
					"""));
			}

			@Test
			void redir_tupleWithMoreFields_shouldThrow() {
				assertThatExceptionOfType(RedirectionException.class).isThrownBy(() -> visit("""
					Foo <i:Int, t:Text, b:Bool>
					x:Foo
					x <- <69 "hello" false 420>
					"""));
			}

			@Test
			void redir_compatibleRecord_shouldChangeFields() {
				assertThat(
					visit("""
						Foo <i:Int, t:Text, b:Bool>
						x:Foo
						x <- <i=69 t="hello" b=true>
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(
					GioviStructs.of(
						"Foo",
						"i", GFields.of(69),
						"t", GFields.of("hello"),
						"b", GFields.of(true)
					)
				);
			}

			@Test
			@Disabled("Candidate not implemented")
			void redirPlus_narrowerRecord_shouldChangeFields() {
				assertThat(
					visit("""
						Foo <i:Int, t:Text, b:Bool>
						x:Foo
						x +<- <i=69 t=true>
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(
					GioviStructs.of(
						"Foo",
						"i", GFields.of(69),
						"t", GFields.of(""),
						"b", GFields.of(true)
					)
				);
			}

			@Test
			void redir_record_withDifferentNames_shouldThrow() {
				assertThatExceptionOfType(NoSuchField.class).isThrownBy(() -> visit("""
					Foo <i:Int, t:Text, b:Bool>
					x:Foo
					x <- <foo=69 t="hello" b=true>
					"""));
			}

			@Test
			void redir_toField_shouldChangeValue() {
				assertThat(
					visit("""
						Foo <i:Int, t:Text, b:Bool>
						x:Foo
						x.i <- 69
						x.t <- "hello"
						x.b <- true
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(
					GioviStructs.of(
						"Foo",
						"i", GFields.of(69),
						"t", GFields.of("hello"),
						"b", GFields.of(true)
					)
				);
			}

			@Test
			void redir_struct_shouldChangeFields() {
				assertThat(
					visit("""
						Foo <i:Int, t:Text, b:Bool>
						x:Foo
						x <- Foo.<i=69 t="hello" b=true>
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(
					GioviStructs.of(
						"Foo",
						"i", GFields.of(69),
						"t", GFields.of("hello"),
						"b", GFields.of(true)
					)
				);
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
				Foobar.<0 1 2 3>.0, 0
				Foobar.<0 1 2 3>.1, 1
				Foobar.<0 1 2 3>.2, 2
				Foobar.<0 1 2 3>.3, 3
				""")
			void access_withInt_shouldReturnFieldAtIndex(String code, Integer expected) {
				var visitor = visit("""
					Foobar <a:Int, b:Int, c:Int, d:Int>
					""");
				assertThat(
					visitExpression(visitor, code)
						.unwrapAsGioviValue()
						.value()
				).isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				Foobar.<0 1 2 3>.{-1}, 3
				Foobar.<0 1 2 3>.{-2}, 2
				Foobar.<0 1 2 3>.{-3}, 1
				Foobar.<0 1 2 3>.{-4}, 0
				""")
			void access_withNegativeInt_shouldReturnFieldAtRearIndex(
				String code,
				Integer expected
			) {
				var visitor = visit("""
					Foobar <a:Int, b:Int, c:Int, d:Int>
					""");
				assertThat(
					visitExpression(visitor, code)
						.unwrapAsGioviValue()
						.value()
				).isEqualTo(expected);
			}

			static Stream<Arguments> produceArgs_access_withRange() {
				return Stream.of(
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.0..4",
						GioviRecords.of("a", 0, "b", 1, "c", 2, "d", 3, "e", 4)
					),
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.0..3",
						GioviRecords.of("a", 0, "b", 1, "c", 2, "d", 3)
					),
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.0..2",
						GioviRecords.of("a", 0, "b", 1, "c", 2)
					),
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.0..1",
						GioviRecords.of("a", 0, "b", 1)
					),
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.0..0",
						GioviRecords.of("a", 0)
					),
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.1..4",
						GioviRecords.of("b", 1, "c", 2, "d", 3, "e", 4)
					),
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.2..4",
						GioviRecords.of("c", 2, "d", 3, "e", 4)
					),
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.3..4",
						GioviRecords.of("d", 3, "e", 4)
					),
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.4..4",
						GioviRecords.of("e", 4)
					),
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.1..3",
						GioviRecords.of("b", 1, "c", 2, "d", 3)
					),
					Arguments.of(
						"Foo.<a=0 b=1 c=2 d=3 e=4>.2..4",
						GioviRecords.of("c", 2, "d", 3, "e", 4)
					)
				);
			}

			@ParameterizedTest
			@MethodSource("produceArgs_access_withRange")
			void access_withRange_shouldReturnSubset(String code, GioviRecord expected) {
				var visitor = visit("""
					Foo <a:Int, b:Int, c:Int, d:Int, e:Int>
					""");
				assertThat(visitValue(visitor, code)).isEqualTo(expected);
			}

			@Test
			void access_withLengthOperator_shouldReturnFieldsCount() {
				var visitor = visit("""
					Foobar <a:Int, b:Int, c:Int, d:Int, e:Int>
					""");
				assertThat(
					visitExpression(visitor, """
						Foobar.<>.%
						""")
						.asGioviValue()
						.value()
				).isEqualTo(5);
			}

			@Test
			void accessWithIndexesOperator_shouldReturnRange() {
				var visitor = visit("""
					Foobar <a:Int, b:Int, c:Int, d:Int, e:Int>
					""");
				assertThat(visitExpression(visitor, """
					Foobar.<>.@
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
				data:Text
				next:self
			>
			n:Node <- <data="root">
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
				label:Text
				next:self
				self foo() => self {
					self -> _out
					self.next.foo -> _out
					=> self
				}
			>
			root:Node
			root.foo() -> _out
			""");
		}

	}

}
