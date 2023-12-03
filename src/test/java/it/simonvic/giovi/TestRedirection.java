package it.simonvic.giovi;

import static it.simonvic.giovi.Giovi.visit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.exceptions.IncompatibleStructs;
import it.simonvic.giovi.util.GioviStructs;

public class TestRedirection {


	@Nested
	class TestReal {

		@Test
		void redirToDeclaration_shouldInitialize() {
			assertEquals(1d, visit("""
				1.0 -> x Real
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unbox());
		}

		@Test
		void redir_shouldChangeValue() {
			assertEquals(69d, visit("""
				x Real
				69 -> x
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unbox());
		}

		@Test
		void redirPlus_shouldIncrement() {
			assertEquals(4d, visit("""
				2 -> x Real
				2 ->+ x
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unbox());
		}

		@Test
		void redirMinus_shouldDecrement() {
			assertEquals(7d, visit("""
				10 -> x Real
				3 ->- x
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unbox());
		}

		@Test
		void redirStar_shouldMultiply() {
			assertEquals(10d, visit("""
				2 -> x Real
				5 ->* x
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unbox());
		}

		@Test
		void redirDiv_shouldDivide() {
			assertEquals(5d, visit("""
				10 -> x Real
				2 ->/ x
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unbox());
		}
	}

	@Nested
	class TestText {
		@Test
		void redir_shouldChangeValue() {
			assertEquals("foo", visit("""
				x Text
				"foo" -> x
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unbox());
		}

		@Test
		void redirPlus_shouldAppend() {
			assertEquals("foobar", visit("""
				"foo" -> x Text
				"bar" ->+ x
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unbox());
		}

		@Test
		void redirMinus_shouldRemove() {
			assertEquals("bar", visit("""
				"foobar" -> x Text
				"foo" ->- x
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unbox());
		}

		@Test
		void redirMinus_shouldRemoveAllOccurences() {
			assertEquals("bar", visit("""
				"foobarfoo" -> x Text
				"foo" ->- x
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unbox());
		}
	}

	@Nested
	class TestStruct {

		@Test
		void redir_shouldChangeFields() {
			assertEquals(
				GioviStructs.of(
					"Color",
					"r", 255,
					"g", 69,
					"b", 42),
				visit("""
					Color <r Int, g Int, b Int>
					x Color
					<255 69 42> -> x
					""")
					.getCurrentScope()
					.getVariableValue("x"));
		}

		@Test
		void redir_namedFields_shouldChangeFields() {
			assertEquals(
				GioviStructs.of(
					"Color",
					"r", 255,
					"g", 69,
					"b", 42),
				visit("""
					Color <r Int, g Int, b Int>
					x Color
					<b:42 r:255 g:69> -> x
					""")
					.getCurrentScope()
					.getVariableValue("x"));
		}

		@Test
		void redir_wrongNamedFields_shouldThrow() {
			assertThrows(IncompatibleStructs.class, () ->
				visit("""
				Color <r Int, g Int, b Int>
				x Color
				<red:255 b:42 g:69> -> x
				"""));
		}

		@Disabled("Should wrong explicit struct fail?")
		@Test
		void redir_wrongExplicitStruct_shouldFail() {
			assertEquals(GioviStructs.of("r", 0, "g", 0, "b", 0), visit("""
				Foo <r Int, g Int, b Int>
				Color <r Int, g Int, b Int>
				x Color
				Foo.<r:255 b:42 g:69> -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void redir_toField_shouldChangeValue() {
			assertEquals(
				GioviStructs.of(
					"Color",
					"r", 255,
					"g", 69,
					"b", 42),
				visit("""
					Color <r Int, g Int, b Int>
					x Color
					255 -> x.r
					69 -> x.g
					42 -> x.b
					""")
					.getCurrentScope()
					.getVariableValue("x"));
		}
	}

}
