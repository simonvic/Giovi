package it.simonvic.giovi;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static it.simonvic.giovi.Giovi.runExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
