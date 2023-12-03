package it.simonvic.giovi;

import static it.simonvic.giovi.Giovi.runExpression;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import it.simonvic.giovi.lang.Range;
import it.simonvic.giovi.util.GioviLists;

public class TestGioviText {

	@Nested
	class Creation {

		@Test
		void testText() {
			assertThat(runExpression("""
				"hello world"
				""").asGioviText().unbox()).isEqualTo(
				"hello world");
		}

		@Test
		void testExactText() {
			assertThat(runExpression("""
				'hello world'
				""").asGioviText().unbox()).isEqualTo(
				"hello world");
		}

		@Nested
		class TextBlock {

			@Test
			void singleLineAndEmpty_shouldBeEmptyString() {
				assertThat(runExpression("""
					"{}"
					""").asGioviText().unbox()).isEqualTo(
					"");
			}

			@Test
			void singleLineNotEmpty_shouldBeOneLineString() {
				assertThat(runExpression("""
					"{hello world}"
					""").asGioviText().unbox()).isEqualTo(
					"hello world");
			}

			@Test
			void multiline_withEmptyDelimiterLines_shouldBeOneLineString() {
				assertThat(runExpression("""
					"{
					hello world
					}"
					""").asGioviText().unbox()).isEqualTo("""
					hello world
					""");
			}

			@Test
			void multiline_withEmptyDelimiterLines_shouldBeMultilineString() {
				assertThat(runExpression("""
					"{
					hello
					world
					}"
					""").asGioviText().unbox()).isEqualTo("""
					hello
					world
					""");
			}

			@Test
			void multiline_withEmptyFirstDelimiterLine_shouldBeMultiLineString() {
				assertThat(runExpression("""
					"{hello
					world
					}"
					""").asGioviText().unbox()).isEqualTo("""
					hello
					world
					""");
			}

			@Test
			void multiline_withEmptyLastDelimiterLine_shouldBeMultiLineString() {
				assertThat(runExpression("""
					"{
					hello
					world}"
					""").asGioviText().unbox()).isEqualTo("""
					hello
					world""");
			}

			@Test
			void multiline_emptyLines_shouldBeMultiLineEmptyString() {
				assertThat(runExpression("""
					"{


					}"
					""").asGioviText().unbox()).isEqualTo("""


					""");
			}


			@Nested
			class TestIncidentalWhitespaces {

				@Test
				void firstLine_shouldNotMatter() {
					assertThat(runExpression("""
						"{	hello
						world
						}"
						""").asGioviText().unbox()).isEqualTo(
						"\thello\nworld\n");
				}

				@Test
				void firstLine_shouldNotMatter2() {
					assertThat(runExpression("""
						"{
							hello
							world
							}"
						""").asGioviText().unbox()).isEqualTo(
						"hello\nworld\n");
				}

				@Test
				void testIndentation_tabs() {
					assertThat(runExpression("""
								"{
									hello
								world
										!!!
								}"
						""").asGioviText().unbox()).isEqualTo(
						"\thello\nworld\n\t\t!!!\n");
				}

				@Test
				void testIndentation_tabs2() {
					assertThat(runExpression("""
								"{
									hello
									world
									!!!
								}"
						""").asGioviText().unbox()).isEqualTo(
						"\thello\n\tworld\n\t!!!\n");
				}

				@Test
				void testIndentation_spaces() {
					assertThat(runExpression("""
						    "{
						        hello
						    world
						            !!!
						    }"
						""").asGioviText().unbox()).isEqualTo(
						"    hello\nworld\n        !!!\n");
				}

				@Test
				void testIndentation_spaces2() {
					assertThat(runExpression("""
						    "{
						        hello
						        world
						        !!!
						    }"
						""").asGioviText().unbox()).isEqualTo(
						"    hello\n    world\n    !!!\n");
				}

				@Test
				void testIndentation_mixed_leaderTab() {
					assertThat(runExpression("""
								    "{
								    hello
								    world
								    !!!
								    }"
						""").asGioviText().unbox()).isEqualTo(
						"    hello\n    world\n    !!!\n    ");
				}

				@Test
				void testIndentation_mixed_leaderTab2() {
					assertThat(runExpression("""
								"{
									hello
								  world
								    !!!
								  }"
						""").asGioviText().unbox()).isEqualTo(
						"\thello\n  world\n    !!!\n  ");
				}

				@Test
				void testIndentation_mixed_leaderSpace() {
					assertThat(runExpression("""
						    "{
						    	hello
						    	world
						    	!!!
						    	}"
						""").asGioviText().unbox()).isEqualTo(
						"\thello\n\tworld\n\t!!!\n\t");
				}

			}

		}

	}

	//                                                                                              Operations
	@Nested
	class Operations {

		//                                                                                          redirection
		@Nested
		class Redirection {
			// TODO(test): ...
		}

		//                                                                                          arithmetic
		@Nested
		class Arithmetic {

			@Test
			void plus_shouldConcatenate() {
				assertThat(runExpression("""
					"hello" + " world"
					""").asGioviText().unbox()).isEqualTo(
					"hello world");
			}

			@Test
			void minus_shouldRemoveAllSubstrings() {
				assertThat(runExpression("""
					"hello world" - "o"
					""").asGioviText().unbox()).isEqualTo(
					"hell wrld");
			}

			@Test
			void multiply_shouldRepeat() {
				assertThat(runExpression("""
					"Hello" * 3
					""").asGioviText().unbox()).isEqualTo(
					"HelloHelloHello");
			}

			@Test
			void divide_shouldSplit() {
				assertThat(runExpression("""
					"hello-beatiful-world" / "-"
					""")).isEqualTo(GioviLists.of(
					"hello", "beatiful", "world"));
			}

		}

		//                                                                                          relational
		@Nested
		class Relational {

			@Test
			void less_shouldCompareLexicographically() {
				assertThat(runExpression("""
					"11" < "2"
					""").asGioviBool().unbox())
					.isTrue();
			}

			@Test
			void greater_shouldCompareLexicographically() {
				assertThat(runExpression("""
					"2" > "11"
					""").asGioviBool().unbox())
					.isTrue();
			}

			@Test
			void equalTexts_shouldBeEqual() {
				assertThat(runExpression("""
					"hello" = "hello"
					""").asGioviBool().unbox())
					.isTrue();
			}

			@Test
			void equal_shouldBeCaseSensitive() {
				assertThat(runExpression("""
					"hello" = "heLLO"
					""").asGioviBool().unbox())
					.isFalse();
			}

		}

		//                                                                                          access
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
				assertThat(runExpression(expression).asGioviText().unbox())
					.isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				"01234"@{-1}, 4
				"01234"@{-2}, 3
				"01234"@{-3}, 2
				"01234"@{-4}, 1
				"01234"@{-5}, 0
				""")
			void accessWithNegativeInt_shouldReturnCharAtRearIndex(
				String expression,
				String expected) {
				assertThat(runExpression(expression).asGioviText().unbox())
					.isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				"01234"@0..4, 01234
				"01234"@0..3, 0123
				"01234"@0..2, 012
				"01234"@0..1, 01
				"01234"@0..0, 0
				"01234"@1..4, 1234
				"01234"@2..4, 234
				"01234"@3..4, 34
				"01234"@4..4, 4
				"01234"@1..3, 123
				"01234"@2..4, 234
				""")
			void accessWithRange_shouldReturnSubstring(String expression, String expected) {
				assertThat(runExpression(expression).asGioviText().unbox())
					.isEqualTo(expected);
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
			void accessWithInverseRange_shouldReturnSubstringReversed(
				String expression,
				String expected) {
				assertThat(runExpression(expression).asGioviText().unbox())
					.isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				"01234"@0..4..2, 024
				"01234"@0..3..2, 02
				"01234"@0..2..2, 02
				"01234"@0..1..2, 0
				"01234"@0..0..2, 0
				"01234"@1..4..2, 13
				"01234"@2..4..2, 24
				"01234"@3..4..2, 3
				"01234"@4..4..2, 4
				"01234"@1..3..2, 13
				"01234"@2..4..2, 24
				""")
			void accessWithStepRange_shouldReturnSubstring(String expression, String expected) {
				assertThat(runExpression(expression).asGioviText().unbox())
					.isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				"01234"@4..0..2, 420
				"01234"@3..0..2, 31
				"01234"@2..0..2, 20
				"01234"@4..1..2, 42
				"01234"@4..2..2, 42
				"01234"@4..3..2, 4
				"01234"@3..1..2, 31
				"01234"@4..2..2, 42
				""")
			void accessWithInverseStepRange_shouldReturnSubstringReversed(
				String expression,
				String expected) {
				assertThat(runExpression(expression).asGioviText().unbox())
					.isEqualTo(expected);
			}

			@Test
			void accessWithLengthOperator_shouldReturnCharactersCount() {
				assertThat(runExpression("""
					"01234"@%
					""").asGioviInt().unbox())
					.isEqualTo(5);
			}

			@Test
			void accessWithElementsOperator_shouldReturnListOfCharacters() {
				assertThat(runExpression("""
					"01234"@*
					"""))
					.isEqualTo(GioviLists.of("0", "1", "2", "3", "4"));
			}

			@Test
			void accessWithIndexesOperator_shouldReturnRange() {
				assertThat(runExpression("""
					"01234"@@
					"""))
					.isEqualTo(Range.of(0, 4, 1));
			}

		}

	}

}
