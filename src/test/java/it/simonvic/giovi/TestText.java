package it.simonvic.giovi;

import static it.simonvic.giovi.Giovi.runExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import it.simonvic.giovi.lang.Range;
import it.simonvic.giovi.lang.values.GioviValue;
import it.simonvic.giovi.util.GioviLists;

public class TestText {

	@Test
	void testText() {
		String actual = runExpression("""
			"hello world"
			""").asGioviText().unbox();
		assertEquals("hello world", actual);
	}

	@Test
	void testExactText() {
		String actual = runExpression("""
			'hello world'
			""").asGioviText().unbox();
		assertEquals("hello world", actual);
	}

	@Nested
	class TextBlock {

		@Test
		void singleLineAndEmpty_shouldBeEmptyString() {
			String actual = runExpression("""
				"{}"
				""").asGioviText().unbox();
			assertEquals("", actual);
		}

		@Test
		void singleLineNotEmpty_shouldBeOneLineString() {
			String actual = runExpression("""
				"{hello world}"
				""").asGioviText().unbox();
			assertEquals("hello world", actual);
		}

		@Test
		void multiline_withEmptyDelimiterLines_shouldBeOneLineString() {
			String actual = runExpression("""
				"{
				hello world
				}"
				""").asGioviText().unbox();
			assertEquals("hello world\n", actual);
		}

		@Test
		void multiline_withEmptyDelimiterLines_shouldBeMultilineString() {
			String actual = runExpression("""
				"{
				hello
				world
				}"
				""").asGioviText().unbox();
			assertEquals("hello\nworld\n", actual);
		}

		@Test
		void multiline_withEmptyFirstDelimiterLine_shouldBeMultiLineString() {
			String actual = runExpression("""
				"{hello
				world
				}"
				""").asGioviText().unbox();
			assertEquals("hello\nworld\n", actual);
		}

		@Test
		void multiline_withEmptyLastDelimiterLine_shouldBeMultiLineString() {
			String actual = runExpression("""
				"{
				hello
				world}"
				""").asGioviText().unbox();
			assertEquals("hello\nworld", actual);
		}


		@Test
		void multiline_emptyLines_shouldBeMultiLineEmptyString() {
			String actual = runExpression("""
				"{


				}"
				""").asGioviText().unbox();
			assertEquals("\n\n", actual);
		}


		@Nested
		class TestIncidentalWhitespaces {

			@Test
			void firstLine_shouldNotMatter() {
				String actual = runExpression("""
					"{	hello
					world
					}"
					""").asGioviText().unbox();
				assertEquals("\thello\nworld\n", actual);
			}

			@Test
			void firstLine_shouldNotMatter2() {
				String actual = runExpression("""
					"{
						hello
						world
						}"
					""").asGioviText().unbox();
				assertEquals("hello\nworld\n", actual);
			}

			@Test
			void testIndentation_tabs() {
				String actual = runExpression("""
							"{
								hello
							world
									!!!
							}"
					""").asGioviText().unbox();
				assertEquals("\thello\nworld\n\t\t!!!\n", actual);
			}

			@Test
			void testIndentation_tabs2() {
				String actual = runExpression("""
							"{
								hello
								world
								!!!
							}"
					""").asGioviText().unbox();
				assertEquals("\thello\n\tworld\n\t!!!\n", actual);
			}

			@Test
			void testIndentation_spaces() {
				String actual = runExpression("""
					    "{
					        hello
					    world
					            !!!
					    }"
					""").asGioviText().unbox();
				assertEquals("    hello\nworld\n        !!!\n", actual);
			}

			@Test
			void testIndentation_spaces2() {
				String actual = runExpression("""
					    "{
					        hello
					        world
					        !!!
					    }"
					""").asGioviText().unbox();
				assertEquals("    hello\n    world\n    !!!\n", actual);
			}

			@Test
			void testIndentation_mixed_leaderTab() {
				String actual = runExpression("""
							    "{
							    hello
							    world
							    !!!
							    }"
					""").asGioviText().unbox();
				assertEquals("    hello\n    world\n    !!!\n    ", actual);
			}

			@Test
			void testIndentation_mixed_leaderTab2() {
				String actual = runExpression("""
							"{
								hello
							  world
							    !!!
							  }"
					""").asGioviText().unbox();
				assertEquals("\thello\n  world\n    !!!\n  ", actual);
			}

			@Test
			void testIndentation_mixed_leaderSpace() {
				String actual = runExpression("""
					    "{
					    	hello
					    	world
					    	!!!
					    	}"
					""").asGioviText().unbox();
				assertEquals("\thello\n\tworld\n\t!!!\n\t", actual);
			}

		}

	}

	@Nested
	class Operations {

		@Test
		void plus_shouldConcatenate() {
			assertEquals(
				GioviValue.of(
					"hello world"),
				runExpression("""
					"hello" + " world"
					"""));
		}

		@Test
		void minus_shouldRemoveAllSubstrings() {
			assertEquals(
				GioviValue.of(
					"hell wrld"),
				runExpression("""
					"hello world" - "o"
					"""));
		}

		@Test
		void divide_shouldSplitString() {
			assertEquals(
				GioviLists.of(
					"hello", "beatiful", "world"),
				runExpression("""
					"hello beatiful world" / " "
					"""));
		}

	}

	@Nested
	class Access {

		@ParameterizedTest
		@CsvSource(textBlock = """
			"01234"@0, 0
			"01234"@1, 1
			"01234"@2, 2
			"01234"@3, 3
			"01234"@4, 4
			""")
		void accessWithInt_shouldReturnCharAtIndex(String expression, String expected) {
			assertEquals(expected, runExpression(expression).asGioviText().unbox());
		}

		@ParameterizedTest
		@CsvSource(textBlock = """
			"01234"@{-1}, 4
			"01234"@{-2}, 3
			"01234"@{-3}, 2
			"01234"@{-4}, 1
			"01234"@{-5}, 0
			""")
		void accessWithNegativeInt_shouldReturnCharAtRearIndex(String expression, String expected) {
			assertEquals(expected, runExpression(expression).asGioviText().unbox());
		}

		@ParameterizedTest
		@CsvSource(textBlock = """
			"01234"@0..4, 01234
			"01234"@0..3, 0123
			"01234"@0..2, 012
			"01234"@0..1, 01
			"01234"@0..0, 0
			"01234"@0..4, 01234
			"01234"@1..4, 1234
			"01234"@2..4, 234
			"01234"@3..4, 34
			"01234"@4..4, 4
			"01234"@1..3, 123
			"01234"@2..4, 234
			""")
		void accessWithRange_shouldReturnSubstring(String expression, String expected) {
			assertEquals(expected, runExpression(expression).asGioviText().unbox());
		}

		@ParameterizedTest
		@CsvSource(textBlock = """
			"01234"@4..0, 43210
			"01234"@3..0, 3210
			"01234"@2..0, 210
			"01234"@1..0, 10
			"01234"@4..1, 4321
			"01234"@4..2, 432
			"01234"@4..3, 43
			"01234"@3..1, 321
			"01234"@4..2, 432
			""")
		void accessWithInverseRange_shouldReturnSubstringReversed(String expression,
			String expected) {
			assertEquals(expected, runExpression(expression).asGioviText().unbox());
		}

		@Test
		void testLength() {
			assertEquals(5, runExpression("""
				"01234"@%
				""").asGioviInt().unbox());
		}

		@Test
		void testElements() {
			assertEquals(
				GioviLists.of(
					"0", "1", "2", "3", "4"),
				runExpression("""
					"01234"@*
					"""));
		}

		@Test
		void testIndexes() {
			assertEquals(
				Range.of(
					0, 4, 1),
				runExpression("""
					"01234"@@
					"""));
		}
	}
}
