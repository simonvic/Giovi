package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import giovi.lang.GioviBool;

public class TestGioviBool {

	@Nested
	class Creation {

		@ParameterizedTest
		@CsvSource(textBlock = """
			true, true
			false, false
			""")
		public void testLiterals(String code, boolean expected) {
			assertThat(visitExpression(code).asNativeBool()).isEqualTo(expected);
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	@Nested
	class Operations {

		//////////////////////////////////////////////////////////////////////////////////////////// redirection

		@Nested
		class Redirection {

			@Test
			void testRedirection() {
				assertThat(
					visit("""
						x Bool
						true -> x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
						.value()
				).isEqualTo(true);
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// arithmetic

		//////////////////////////////////////////////////////////////////////////////////////////// boolean

		@Nested
		class Boolean {

			@ParameterizedTest
			@CsvSource(textBlock = """
				true & true, true
				true & false, false
				false & true, false
				false & false, false
				""")
			public void testAndOperator(String code, boolean expected) {
				assertThat(visitExpression(code).asNativeBool()).isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				true | true, true
				true | false, true
				false | true, true
				false | false, false
				""")
			public void testOrOperator(String code, boolean expected) {
				assertThat(visitExpression(code).asNativeBool()).isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				!true, false
				!false, true
				!!false, false
				!!true, true
				""")
			public void testNotOperator(String code, boolean expected) {
				assertThat(visitExpression(code).asNativeBool()).isEqualTo(expected);
			}


			@ParameterizedTest
			@CsvSource(textBlock = """
				false | {true & false}, false
				false | {true & {false | true}}, true
				false | !{true & {false | true}}, false
				{true & true} | !{true & {false | true}}, true
				""")
			public void testParenthesizedExpression(String code, boolean expected) {
				assertThat(visitExpression(code).asNativeBool()).isEqualTo(expected);
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// relational

		//////////////////////////////////////////////////////////////////////////////////////////// access

		@Nested
		class Access {

			@Test
			void testAccessDefault() {
				assertThat(visitExpression(visit("""
					x Bool default true
					"""), """
					x.default
					"""))
					.isEqualTo(new GioviBool(true));
			}

		}

	}

}
