package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestText {

	private GioviObject run(String code) {
		GioviLexer lexer = new GioviLexer(CharStreams.fromString(code));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new GioviVisitor().visit(new GioviParser(tokens).expression());
	}

	@Test
	void testText() {
		String actual = run("""
			"hello world"
			""").asGioviText().unbox();
		assertEquals("hello world", actual);
	}

	@Test
	void testExactText() {
		String actual = run("""
			'hello world'
			""").asGioviText().unbox();
		assertEquals("hello world", actual);
	}

	@Nested
	class TextBlock {

		@Test
		void singleLineAndEmpty_shouldBeEmptyString() {
			String actual = run("""
				"{"}
				""").asGioviText().unbox();
			assertEquals("", actual);
		}

		@Test
		void singleLineNotEmpty_shouldBeOneLineString() {
			String actual = run("""
				"{hello world"}
				""").asGioviText().unbox();
			assertEquals("hello world", actual);
		}

		@Test
		void multiline_withEmptyDelimiterLines_shouldBeOneLineString() {
			String actual = run("""
				"{
				hello world
				"}
				""").asGioviText().unbox();
			assertEquals("hello world\n", actual);
		}

		@Test
		void multiline_withEmptyDelimiterLines_shouldBeMultilineString() {
			String actual = run("""
				"{
				hello
				world
				"}
				""").asGioviText().unbox();
			assertEquals("hello\nworld\n", actual);
		}

		@Test
		void multiline_withEmptyFirstDelimiterLine_shouldBeMultiLineString() {
			String actual = run("""
				"{hello
				world
				"}
				""").asGioviText().unbox();
			assertEquals("hello\nworld\n", actual);
		}

		@Test
		void multiline_withEmptyLastDelimiterLine_shouldBeMultiLineString() {
			String actual = run("""
				"{
				hello
				world"}
				""").asGioviText().unbox();
			assertEquals("hello\nworld", actual);
		}


		@Test
		void multiline_emptyLines_shouldBeMultiLineEmptyString() {
			String actual = run("""
				"{
							

				"}
				""").asGioviText().unbox();
			assertEquals("\n\n", actual);
		}


		@Nested
		class TestIncidentalWhitespaces {

			@Test
			void firstLine_shouldNotMatter() {
				String actual = run("""
					"{	hello
					world
					"}
					""").asGioviText().unbox();
				assertEquals("\thello\nworld\n", actual);
			}

			@Test
			void firstLine_shouldNotMatter2() {
				String actual = run("""
					"{
						hello
						world
						"}
					""").asGioviText().unbox();
				assertEquals("hello\nworld\n", actual);
			}

			@Test
			void testIndentation_tabs() {
				String actual = run("""
							"{
								hello
							world
									!!!
							"}
					""").asGioviText().unbox();
				assertEquals("\thello\nworld\n\t\t!!!\n", actual);
			}

			@Test
			void testIndentation_tabs2() {
				String actual = run("""
							"{
								hello
								world
								!!!
							"}
					""").asGioviText().unbox();
				assertEquals("\thello\n\tworld\n\t!!!\n", actual);
			}

			@Test
			void testIndentation_spaces() {
				String actual = run("""
					    "{
					        hello
					    world
					            !!!
					    "}
					""").asGioviText().unbox();
				assertEquals("    hello\nworld\n        !!!\n", actual);
			}

			@Test
			void testIndentation_spaces2() {
				String actual = run("""
					    "{
					        hello
					        world
					        !!!
					    "}
					""").asGioviText().unbox();
				assertEquals("    hello\n    world\n    !!!\n", actual);
			}

			@Test
			void testIndentation_mixed_leaderTab() {
				String actual = run("""
							    "{
							    hello
							    world
							    !!!
							    "}
					""").asGioviText().unbox();
				assertEquals("    hello\n    world\n    !!!\n    ", actual);
			}

			@Test
			void testIndentation_mixed_leaderTab2() {
				String actual = run("""
							"{
								hello
							  world
							    !!!
							  "}
					""").asGioviText().unbox();
				assertEquals("\thello\n  world\n    !!!\n  ", actual);
			}

			@Test
			void testIndentation_mixed_leaderSpace() {
				String actual = run("""
					    "{
					    	hello
					    	world
					    	!!!
					    	"}
					""").asGioviText().unbox();
				assertEquals("\thello\n\tworld\n\t!!!\n\t", actual);
			}

		}

	}
	
}
