package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitor.visitExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestExpressionArithmetic {


	@ParameterizedTest
	@CsvSource(textBlock = """
		10 + 5, 15
		10 - 5, 5
		10 + -5, 5
		10 - -5, 15
		-10 - -5, -5
		"""
	)
	void testAdditiveExpression(String code, Integer expected) {
		assertEquals(expected, visitExpression(code).asNativeInt());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		2.0 * 2.0, 4
		0.5 * 4, 2
		10.0 / 2, 5
		10 / 0.5, 20
		"""
	)
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
		"""
	)
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
		"""
	)
	void testModuloExpression(String code, Integer expected) {
		assertEquals(expected, visitExpression(code).asNativeInt());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		10 * {1 + 1}, 20
		10 * {2 * {3 + 2}}, 100
		"""
	)
	void testParenthesizedExpression(String code, Integer expected) {
		assertEquals(expected, visitExpression(code).asNativeInt());
	}

}
