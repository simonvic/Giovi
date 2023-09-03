package it.simonvic.giovi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static it.simonvic.giovi.Giovi.runExpression;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestBoolExpressions {

	@ParameterizedTest
	@CsvSource(textBlock = """
		true, true
		false, false
		"""
	)
	public void testLiterals(String code, boolean expected) {
		assertEquals(expected, runExpression(code).asBoolean());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		true & true, true
		true & false, false
		false & true, false
		false & false, false
		"""
	)
	public void testAndOperator(String code, boolean expected) {
		assertEquals(expected, runExpression(code).asBoolean());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		true | true, true
		true | false, true
		false | true, true
		false | false, false
		"""
	)
	public void testOrOperator(String code, boolean expected) {
		assertEquals(expected, runExpression(code).asBoolean());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		!true, false
		!false, true
		!!false, false
		!!true, true
		"""
	)
	public void testNotOperator(String code, boolean expected) {
		assertEquals(expected, runExpression(code).asBoolean());
	}


	@ParameterizedTest
	@CsvSource(textBlock = """
		false | {true & false}, false
		false | {true & {false | true}}, true
		false | !{true & {false | true}}, false
		{true & true} | !{true & {false | true}}, true
		"""
	)
	public void testParenthesizedExpression(String code, boolean expected) {
		assertEquals(expected, runExpression(code).asBoolean());
	}

}
