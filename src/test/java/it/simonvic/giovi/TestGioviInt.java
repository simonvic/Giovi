package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import giovi.lang.GioviInt;

public class TestGioviInt {

	//////////////////////////////////////////////////////////////////////////////////////////////// Creation

	@Nested
	class Creation {

		@ParameterizedTest
		@CsvSource(textBlock = """
			69, 69
			420, 420
			69_420, 69_420
			1_000_000, 1_000_000
			0b1000101, 69
			0b0000_0001_1010_0100, 420
			0o105, 69
			0o207_454, 69420
			0x10f2c, 0x10f2c
			0xFF_FF_FF, 0xFFFFFF
			""")
		void testLiteral(String code, Integer expected) {
			assertThat(visitExpression(code).asNativeInt()).isEqualTo(expected);
		}

		@Test
		void testMaxValue() {
			assertThat(visitExpression("2147483647").asNativeInt()).isEqualTo(Integer.MAX_VALUE);
		}

		@Test
		@Disabled
		void testMinValue() {
			// FIXME: requires a complete rewrite of how we handle negative sign
			assertThat(visitExpression("-2147483648").asNativeInt()).isEqualTo(Integer.MIN_VALUE);
		}

		@Test
		void naturalGreaterThanMaxValue_shouldFail() {
			assertThatExceptionOfType(NumberFormatException.class)
				.isThrownBy(() -> visitExpression("2147483648"));
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
						x Int
						69 -> x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
						.value()
				).isEqualTo(69);
			}

			@Test
			void testRedirectionPlus() {
				assertThat(
					visit("""
						60 -> x Int
						9 ->+ x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
						.value()
				).isEqualTo(69);
			}

			@Test
			void testRedirectionMinus() {
				assertThat(
					visit("""
						79 -> x Int
						10 ->- x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
						.value()
				).isEqualTo(69);
			}

			@Test
			void testRedirectionMultiply() {
				assertThat(
					visit("""
						42 -> x Int
						10 ->* x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
						.value()
				).isEqualTo(420);
			}

			@Test
			void testRedirectionDivide() {
				assertThat(
					visit("""
						690 -> x Int
						10 ->/ x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
						.value()
				).isEqualTo(69);
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// arithmetic

		@Nested
		class Arithmetic {


			@ParameterizedTest
			@CsvSource(textBlock = """
				10 + 5, 15
				10 - 5, 5
				10 + -5, 5
				10 - -5, 15
				-10 - -5, -5
				""")
			void testAdditiveExpression(String code, Integer expected) {
				assertEquals(expected, visitExpression(code).asNativeInt());
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				2.0 * 2.0, 4
				0.5 * 4, 2
				10.0 / 2, 5
				10 / 0.5, 20
				""")
			void testMultiplicativeExpression(String code, Double expected) {
				assertEquals(expected, visitExpression(code).asNativeReal());
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				10.0 ^ 1, 10
				10.0 ^ 2, 100
				10.0 ^ -2, 0.01
				10.0 ^ {1 + 1}, 100
				10.0 ^ -{2 * 2}, 0.0001
				""")
			void testPowerExpression(String code, Double expected) {
				assertEquals(expected, visitExpression(code).asNativeReal());
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				10 % 1, 0
				10 % 2, 0
				10 % 3, 1
				10 % 4, 2
				10 % 6, 4
				10 % 7, 3
				""")
			void testModuloExpression(String code, Integer expected) {
				assertEquals(expected, visitExpression(code).asNativeInt());
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				10 * {1 + 1}, 20
				10 * {2 * {3 + 2}}, 100
				""")
			void testParenthesizedExpression(String code, Integer expected) {
				assertEquals(expected, visitExpression(code).asNativeInt());
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// boolean

		//////////////////////////////////////////////////////////////////////////////////////////// relational

		@Nested
		class Relational {
		}

		//////////////////////////////////////////////////////////////////////////////////////////// access

		@Nested
		class Access {

			@Test
			void testAccessDefault() {
				assertThat(visitExpression(visit("""
					x Int default 69
					"""), """
					x.default
					"""))
					.isEqualTo(new GioviInt(69));
			}

		}

	}

}
