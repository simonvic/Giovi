package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

}
