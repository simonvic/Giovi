package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitor.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.IncompatibleStructs;
import it.simonvic.giovi.lang.GioviStructDefinition;
import it.simonvic.giovi.util.GioviStructs;
import it.simonvic.giovi.util.Structs;

public class TestGioviStruct {

	@Test
	void shouldBeEquals() {
		assertEquals(
			GioviStructs.of(
				"foo", 69,
				"bar", true,
				"foobar", "hello"),
			GioviStructs.of(
				"foo", 69,
				"bar", true,
				"foobar", "hello"));
	}

	@Test
	void structsWithDifferentFields_shouldNotBeEquals() {
		assertNotEquals(
			GioviStructs.of(
				"foo", 69,
				"bar", true),
			GioviStructs.of(
				"foo", 69));
	}

	@Test
	void structsWithDifferentFieldsType_shouldNotBeEquals() {
		assertNotEquals(
			GioviStructs.of(
				"foo", 69,
				"bar", true),
			GioviStructs.of(
				"foo", 69,
				"bar", 420));
	}

	@Nested
	class Definition {

		@Test
		void testDefinition() {
			var visitor = visit("""
				Foo <
					foo Int
					bar Bool
					foobar Text
				>
				""");
			assertTrue(visitor.getCurrentScope().isDefinedStruct("Foo"));
		}

		@Test
		void testEmptyStruct() {
			var visitor = visit("""
				Foo <>
				""");
			assertEquals(new GioviStructDefinition(
				"Foo", Structs.of()),
				visitor.getCurrentScope().getDefinitionStruct("Foo"));
		}

		@Test
		void testSimpleStructDefaultFields() {
			var visitor = visit("""
				Foo <
					foo Int
					bar Bool
					foobar Text
				>
				""");
			assertEquals(new GioviStructDefinition(
				"Foo", Structs.of(
					"foo", 0,
					"bar", false,
					"foobar", "")),
				visitor.getCurrentScope().getDefinitionStruct("Foo"));
		}

		@Test
		void testFieldsDefaultOverride() {
			var visitor = visit("""
				Foo <
					foo Int default 69
					bar Bool default true
					foobar Text default "foobar"
				>
				""");
			assertEquals(new GioviStructDefinition(
				"Foo", Structs.of(
					"foo", 69,
					"bar", true,
					"foobar", "foobar")),
				visitor.getCurrentScope().getDefinitionStruct("Foo"));
		}

		@Test
		void testNestedStruct() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				Person <
					name Text
					favoriteColor RGB
				>
				""");
			var expected = new GioviStructDefinition(
				"Person", Structs.of(
					"name", "",
					"favoriteColor", new GioviStructDefinition("RGB", Structs.of(
						"r", 0,
						"g", 0,
						"b", 0))));
			var actual = visitor.getCurrentScope().getDefinitionStruct("Person");
			assertEquals(expected, actual);
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
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB : ARGB <
					a Int
				>
				""");
			assertEquals(new GioviStructDefinition(
				"ARGB", Structs.of(
					"r", 0,
					"g", 0,
					"b", 0,
					"a", 0)),
				visitor.getCurrentScope().getDefinitionStruct("ARGB"));
		}

		@Test
		void testInheritedFieldsDefaultOverride() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB : SomeColor <
					r Int default 255
					g Int default 255
					b Int default 255
					a Int default 255
				>
				""");
			assertEquals(new GioviStructDefinition(
				"SomeColor", Structs.of(
					"r", 255,
					"g", 255,
					"b", 255,
					"a", 255)),
				visitor.getCurrentScope().getDefinitionStruct("SomeColor"));
		}

		@Test
		void whenOverridingInheritedFieldsDefault_shouldNotChangeDefaultsInInheritedClass() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB : SomeColor <
					r Int default 255
					g Int default 255
					b Int default 255
					a Int default 255
				>
				""");
			assertEquals(new GioviStructDefinition(
				"RGB", Structs.of(
					"r", 0,
					"g", 0,
					"b", 0)),
				visitor.getCurrentScope().getDefinitionStruct("RGB"));
		}
	}

	@Nested
	class Creation {

		@Test
		void anonStructWithUnnamedFields() {
			assertEquals(
				GioviStructs.of(List.of(
					69, true, "foo")),
				runExpression("""
					<69 true "foo">
					"""));
		}

		@Test
		void anonStructWithNamedFields() {
			assertEquals(
				GioviStructs.of(
					"foo", 69,
					"bar", true,
					"foobar", "hello"),
				runExpression("""
					<
						foo: 69
						bar: true
						foobar: "hello"
					>
					"""));
		}


		@Test
		void structWithInheritance() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB : ARGB <
					a Int
				>
				color ARGB
				""");
			assertEquals(
				GioviStructs.of(
					"ARGB",
					"r", 0,
					"g", 0,
					"b", 0,
					"a", 0),
				visitor.getCurrentScope().getVariableValue("color"));
		}

		@Test
		void typedStructWithUnnamedFields() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				""");
			assertEquals(
				GioviStructs.of(
					"RGB",
					"r", 1,
					"g", 2,
					"b", 3),
				runExpression(visitor, """
					RGB.<1 2 3>
					"""));
		}

		@Test
		void typedStructWithNamedFields() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				""");
			assertEquals(
				GioviStructs.of(
					"RGB",
					"r", 1,
					"g", 2,
					"b", 3),
				runExpression(visitor, """
					RGB.<
						r: 1
						g: 2
						b: 3
					>
					"""));
		}

		@Test
		void givenUndefinedType_whenCreatingTypedStruct_shouldThrow() {
			assertThrows(GioviException.class, () -> runExpression("""
				Foo.<69 420 "lol">
				"""));
		}

		@Test
		void givenDefinedType_whenCreatingTypedStruct_shouldNotThrow() {
			assertDoesNotThrow(() -> run("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB.<69 42 255>
				"""));
		}

		@Test
		void whenCreatingTypedStructWithWrongFieldsCount_shouldThrow() {
			assertThrows(IncompatibleStructs.class, () -> run("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB.<1 2 3 4>
				"""));
		}

		@Test
		void whenCreatingTypedStructWithWrongFieldsName_shouldThrow() {
			assertThrows(IncompatibleStructs.class, () -> run("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB.<red:1 g:2 b:3>
				"""));
		}

		@Test
		void whenCreatingTypedStructWithSubsetOfFields_shouldNotThrow() {
			assertDoesNotThrow(() -> run("""
				RGB <
					r Int
					g Int
					b Int
				>
				RGB.<r:1 g:2>
				"""));
		}

		@Test
		void whenCreatingTypedStructWithSubsetOfFields_shouldOnlyInitTheSubset() {
			var visitor = visit("""
				RGB <
					r Int
					g Int
					b Int
				>
				""");
			assertEquals(
				GioviStructs.of(
					"RGB",
					"r", 69,
					"g", 0,
					"b", 420),
				runExpression(visitor, """
					RGB.<r:69 b:420>
					"""));
		}


	}

	@Nested
	class Operators {
	}
}
