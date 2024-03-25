package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import giovi.Natural;
import giovi.lang.*;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviTypes;

public class TestDefaults {

	@Nested
	class TestDefaultOverride {

		@Test
		void testNatural() {
			assertEquals(new Natural(69), visit("x Natural default 69")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue());
		}

		@Test
		void testInt() {
			assertEquals(69, visit("x Int default 69")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue());
		}

		@Test
		void testReal() {
			assertEquals(6.9, visit("x Real default 6.9")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue());
		}

		@Test
		void testBool() {
			assertEquals(true, visit("x Bool default true")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue());
		}

		@Test
		void testText() {
			assertEquals("hello", visit("x Text default 'hello'")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue());
		}

		@Test
		void testList() {
			var expected = List.of(
				GioviValue.of("a"),
				GioviValue.of("b"),
				GioviValue.of("c"));
			var actual = visit("x [Text] default ['a' 'b' 'c']")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue();
			assertEquals(expected, actual);
		}

		@Test
		void testMaps() {
			var expected = new LinkedHashMap<GioviInt, GioviText>();
			expected.put(new GioviInt(0), new GioviText("a"));
			expected.put(new GioviInt(1), new GioviText("b"));
			expected.put(new GioviInt(2), new GioviText("c"));
			var actual = visit("x [Int:Text] default [0:'a' 1:'b' 2:'c']")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue();
			assertEquals(expected, actual);
		}
	}

	@Nested
	class TestDefaultReset {

		@Test
		void testNatural() {
			assertEquals(new GioviNatural(new Natural(69)), visit("""
				x Natural default 69
				420 -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void testInt() {
			assertEquals(new GioviInt(69), visit("""
				x Int default 69
				420 -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void testReal() {
			assertEquals(new GioviReal(6.9), visit("""
				x Real default 6.9
				4.20 -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void testBool() {
			assertEquals(new GioviBool(true), visit("""
				x Bool default true
				false -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void testText() {
			assertEquals(new GioviText("foo"), visit("""
				x Text default "foo"
				"bar" -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void testList() {
			var expected = GioviLists.of("a", "b", "c");
			var actual = visit("""
				x [Text] default ['a' 'b' 'c']
				['z'] -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x");
			assertEquals(expected, actual);
		}

		@Test
		void testMaps() {
			var expected = new GioviMap(
				GioviTypes.of(GioviInt.class),
				GioviTypes.of(GioviText.class));
			expected.put(new GioviInt(0), new GioviText("a"));
			expected.put(new GioviInt(1), new GioviText("b"));
			expected.put(new GioviInt(2), new GioviText("c"));
			var actual = visit("""
				x [Int:Text] default [0:'a' 1:'b' 2:'c']
				[69:'z'] -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x");
			assertEquals(expected, actual);
		}

	}

}
