package it.simonvic.giovi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static it.simonvic.giovi.GioviVisitor.visitExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestExpressionRelational {

	@ParameterizedTest
	@CsvSource(textBlock = """
		10 > 0, true
		0 > 10, false
		-1 > -5, true
		10 > -10, true
		-10 > 10, false
		"""
	)
	void testGreater(String code, boolean expected) {
		assertEquals(expected, visitExpression(code).asNativeBool());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		10 < 0, false
		0 < 10, true
		-1 < -5, false
		10 < -10, false
		-10 < 10, true
		"""
	)
	void testLess(String code, boolean expected) {
		assertEquals(expected, visitExpression(code).asNativeBool());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		10 <= 0, false
		0 <= 10, true
		-1 <= -5, false
		10 <= -10, false
		-10 <= 10, true
		10 <= 10, true
		-10 <= -10, true
		10 <= -10, false
		"""
	)
	void testLessEqual(String code, boolean expected) {
		assertEquals(expected, visitExpression(code).asNativeBool());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		10 >= 0, true
		0 >= 10, false
		-1 >= -5, true
		10 >= -10, true
		-10 >= 10, false
		10 >= 10, true
		-10 >= -10, true
		10 >= -10, true
		"""
	)
	void testGreaterEqual(String code, boolean expected) {
		assertEquals(expected, visitExpression(code).asNativeBool());
	}

}
