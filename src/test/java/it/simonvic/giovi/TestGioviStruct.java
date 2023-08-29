package it.simonvic.giovi;

import static it.simonvic.giovi.Giovi.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.IncompatibleStructs;
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
	void structsWithDifferentAttributes_shouldNotBeEquals() {
		assertNotEquals(
			GioviStructs.of(
				"foo", 69,
				"bar", true),
			GioviStructs.of(
				"foo", 69));
	}

	@Test
	void structsWithDifferentAttributesType_shouldNotBeEquals() {
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
		@Disabled
		void testEmptyStruct() {

		}

		@Test
		void testSimpleStruct() {
			var visitor = visit("""
				Foo (
					foo Int
					bar Bool
					foobar Text
				)
				""");
			assertTrue(visitor.getCurrentScope().isDefinedStruct("Foo"));
		}

		@Test
		void testSimpleStructDefaultAttributes() {
			var visitor = visit("""
				Foo (
					foo Int
					bar Bool
					foobar Text
				)
				""");
			assertEquals(
				GioviStructs.of(
					"foo", 0,
					"bar", false,
					"foobar", ""),
				visitor.getCurrentScope().getDefinitionStruct("Foo"));
		}

		@Test
		void testAttributesDefaultOverride() {
			var visitor = visit("""
				Foo (
					foo Int _ 69
					bar Bool _ true
					foobar Text _ "foobar"
				)
				""");
			assertEquals(
				GioviStructs.of(
					"foo", 69,
					"bar", true,
					"foobar", "foobar"),
				visitor.getCurrentScope().getDefinitionStruct("Foo"));
		}

		@Test
		void testNestedStruct() {
			var visitor = visit("""
				RGB (
					r Int
					g Int
					b Int
				)
				Person (
					name Text
					favoriteColor RGB
				)
				""");
			assertEquals(
				GioviStructs.of(
					"name", "",
					"favoriteColor", Structs.of(
						"r", 0,
						"g", 0,
						"b", 0)),
				visitor.getCurrentScope().getDefinitionStruct("Person"));
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
				RGB (
					r Int
					g Int
					b Int
				)
				RGB : ARGB (
					a Int
				)
				""");
			assertEquals(
				GioviStructs.of(
					"r", 0,
					"g", 0,
					"b", 0,
					"a", 0),
				visitor.getCurrentScope().getDefinitionStruct("ARGB"));
		}

		@Test
		void testInheritedAttributesDefaultOverride() {
			var visitor = visit("""
				RGB (
					r Int
					g Int
					b Int
				)
				RGB : SomeColor (
					r Int _ 255
					g Int _ 255
					b Int _ 255
					a Int _ 255
				)
				""");
			assertEquals(
				GioviStructs.of(
					"r", 255,
					"g", 255,
					"b", 255,
					"a", 255),
				visitor.getCurrentScope().getDefinitionStruct("SomeColor"));
		}

		@Test
		void whenOverridingInheritedAttributesDefault_shouldNotChangeDefaultsInInheritedClass() {
			var visitor = visit("""
				RGB (
					r Int
					g Int
					b Int
				)
				RGB : SomeColor (
					r Int _ 255
					g Int _ 255
					b Int _ 255
					a Int _ 255
				)
				""");
			assertEquals(
				GioviStructs.of(
					"r", 0,
					"g", 0,
					"b", 0),
				visitor.getCurrentScope().getDefinitionStruct("RGB"));
		}
	}

	@Nested
	class Creation {

		@Test
		void anonStructWithUnnamedAttributes() {
			assertEquals(
				GioviStructs.of(List.of(
					69, true, "foo")),
				runExpression("""
					(69 true "foo")
					"""));
		}

		@Test
		void anonStructWithNamedAttributes() {
			assertEquals(
				GioviStructs.of(
					"foo", 69,
					"bar", true,
					"foobar", "hello"),
				runExpression("""
					(
						foo: 69
						bar: true
						foobar: "hello"
					)
					"""));
		}


		@Test
		void structWithInheritance() {
			var visitor = visit("""
				RGB (
					r Int
					g Int
					b Int
				)
				RGB : ARGB (
					a Int
				)
				color ARGB
				""");
			assertEquals(
				GioviStructs.of(
					"r", 0,
					"g", 0,
					"b", 0,
					"a", 0),
				visitor.getCurrentScope().getVariableValue("color"));
		}

		@Test
		void typedStructWithUnnamedAttributes() {
			var visitor = visit("""
				RGB (
					r Int
					g Int
					b Int
				)
				""");
			assertEquals(
				GioviStructs.of(
					"r", 1,
					"g", 2,
					"b", 3),
				runExpression(visitor, """
					RGB.(1 2 3)
					"""));
		}

		@Test
		void typedStructWithNamedAttributes() {
			var visitor = visit("""
				RGB (
					r Int
					g Int
					b Int
				)
				""");
			assertEquals(
				GioviStructs.of(
					"r", 1,
					"g", 2,
					"b", 3),
				runExpression(visitor, """
					RGB.(
						r: 1
						g: 2
						b: 3
					)
					"""));
		}

		@Test
		void givenUndefinedType_whenCreatingTypedStruct_shouldThrow() {
			assertThrows(GioviException.class, () -> runExpression("""
				Foo.(69 420 "lol")
				"""));
		}

		@Test
		void givenDefinedType_whenCreatingTypedStruct_shouldNotThrow() {
			assertDoesNotThrow(() -> run("""
				RGB (
					r Int
					g Int
					b Int
				)
				RGB.(69 42 255)
				"""));
		}

		@Test
		void whenCreatingTypedStructWithWrongAttributesCount_shouldThrow() {
			assertThrows(IncompatibleStructs.class, () -> run("""
				RGB (
					r Int
					g Int
					b Int
				)
				RGB.(1 2 3 4)
				"""));
		}

		@Test
		void whenCreatingTypedStructWithWrongAttributesName_shouldThrow() {
			assertThrows(IncompatibleStructs.class, () -> run("""
				RGB (
					r Int
					g Int
					b Int
				)
				RGB.(red:1 g:2 b:3)
				"""));
		}

		@Test
		void whenCreatingTypedStructWithSubsetOfAttributes_shouldNotThrow() {
			assertDoesNotThrow(() -> run("""
				RGB (
					r Int
					g Int
					b Int
				)
				RGB.(r:1 g:2)
				"""));
		}

		@Test
		void whenCreatingTypedStructWithSubsetOfAttributes_shouldOnlyInitTheSubset() {
			var visitor = visit("""
				RGB (
					r Int
					g Int
					b Int
				)
				""");
			assertEquals(
				GioviStructs.of(
					"r", 69,
					"g", 0,
					"b", 420),
				runExpression(visitor, """
					RGB.(r:69 b:420)
					"""));
		}


	}

	@Nested
	class Operators {
	}
}
