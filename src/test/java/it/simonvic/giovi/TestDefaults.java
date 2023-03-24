package it.simonvic.giovi;

import it.simonvic.giovi.lang.type.GioviType;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.util.GioviLists;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import it.simonvic.giovi.lang.type.Natural;

import java.util.*;

import static it.simonvic.giovi.Giovi.visit;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDefaults {

	@Nested
	class TestDefaultOverride {

		@Test
		void testNatural() {
			assertEquals(new Natural(69), visit("x Natural _ 69")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue());
		}

		@Test
		void testInt() {
			assertEquals(69, visit("x Int _ 69")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue());
		}

		@Test
		void testReal() {
			assertEquals(6.9, visit("x Real _ 6.9")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue());
		}

		@Test
		void testBool() {
			assertEquals(true, visit("x Bool _ true")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue());
		}

		@Test
		void testText() {
			assertEquals("hello", visit("x Text _ 'hello'")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue());
		}

		@Test
		void testList() {
			var expected = List.of(
				GioviValue.of("a"),
				GioviValue.of("b"),
				GioviValue.of("c")
			);
			var actual = visit("x [Text] _ ['a' 'b' 'c']")
				.getCurrentScope()
				.getVariableValue("x")
				.getDefaultValue();
			assertEquals(expected, actual);
		}

		@Disabled("needs to manually implement the equals")
		@Test
		void testMaps() {
			// the maps are different because GioviValues references are different
			var expected = new LinkedHashMap<GioviInt, GioviText>();
			expected.put(new GioviInt(0), new GioviText("a"));
			expected.put(new GioviInt(1), new GioviText("b"));
			expected.put(new GioviInt(2), new GioviText("c"));
			var actual = visit("x [Int:Text] _ [0:'a' 1:'b' 2:'c']")
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
				x Natural _ 69
				420 > x
				_ > x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void testInt() {
			assertEquals(new GioviInt(69), visit("""
				x Int _ 69
				420 > x
				_ > x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void testReal() {
			assertEquals(new GioviReal(6.9), visit("""
				x Real _ 6.9
				4.20 > x
				_ > x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void testBool() {
			assertEquals(new GioviBool(true), visit("""
				x Bool _ true
				false > x
				_ > x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void testText() {
			assertEquals(new GioviText("foo"), visit("""
				x Text _ "foo"
				"bar" > x
				_ > x
				""")
				.getCurrentScope()
				.getVariableValue("x"));
		}

		@Test
		void testList() {
			var expected = GioviLists.of("a", "b", "c");
			var actual = visit("""
				x [Text] _ ['a' 'b' 'c']
				['z'] > x
				_ > x
				""")
				.getCurrentScope()
				.getVariableValue("x");
			assertEquals(expected, actual);
		}

		@Test
		void testMaps() {
			var expected = new GioviMap(new GioviType(GioviInt.class), new GioviType(GioviText.class));
			expected.put(new GioviInt(0), new GioviText("a"));
			expected.put(new GioviInt(1), new GioviText("b"));
			expected.put(new GioviInt(2), new GioviText("c"));
			var actual = visit("""
				x [Int:Text] _ [0:'a' 1:'b' 2:'c']
				[69:'z'] > x
				_ > x
				""")
				.getCurrentScope()
				.getVariableValue("x");
			assertEquals(expected, actual);
		}

	}

}
