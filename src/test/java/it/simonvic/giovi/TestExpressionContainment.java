package it.simonvic.giovi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static it.simonvic.giovi.GioviVisitor.runExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestExpressionContainment {

	@ParameterizedTest
	@CsvSource(textBlock = """
		"simone" =~ "mon", true
		"mon" ~= "simone", true
		"simone" =~ "zzz", false
		"zzz" ~= "simone", false
		"""
	)
	void testStrings(String code, boolean expected) {
		assertEquals(expected, runExpression(code).asBoolean());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		[0 1 2 3] =~ 2, true
		[0 1 2 3] =~ 3, true
		[0 1 2 3] =~ 4, false
		2 ~= [0 1 2 3], true
		3 ~= [0 1 2 3], true
		4 ~= [0 1 2 3], false
		"""
	)
	void testLists(String code, boolean expected) {
		assertEquals(expected, runExpression(code).asBoolean());
	}
}
