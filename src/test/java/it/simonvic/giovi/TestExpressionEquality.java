package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestExpressionEquality {

	@ParameterizedTest
	@CsvSource(textBlock = """
		10 = 10, true
		10 = 50, false
		10.0 = 10, true
		10 = 10.0, true
				
		10 != 10, false
		10 != 50, true
		10.0 != 10, false
		10 != 10.0, false
		"""
	)
	public void testEquality_Numbers(String code, boolean expected) {
		assertEquals(expected, visitExpression(code).asNativeBool());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		"simone" = "simone", true
		"simone" = "Simone", false
		"simone" != "simone", false
		"simone" != "Simone", true
		"""
	)
	public void testEquality_Strings(String code, boolean expected) {
		assertEquals(expected, visitExpression(code).asNativeBool());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		[0 1 2 3] = [0 1 2 3], true
		[0 1 2 3] = [0 1 2 3 4], false
		[0 1 2 3] = [0 1 2], false
		"""
	)
	public void testEquality_List(String code, boolean expected) {
		assertEquals(expected, visitExpression(code).asNativeBool());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		[0:0 1:1 2:2 3:3] = [0:0 1:1 2:2 3:3], true
		[0:"a" 1:"b" 2:"c" 3:"d"] = [0:"a" 1:"b" 2:"c" 3:"d"], true
		[0:0 1:1 2:2 3:3] = [0:0 1:1 2:2 3:69], false
		[0:0 1:1 2:2 3:3] = [0:0 1:1 2:2 3:3 4:4], false
		[0:0 1:1 2:2 3:3] = [0:0 1:1 2:2], false
		"""
	)
	public void testEquality_Map(String code, boolean expected) {
		assertEquals(expected, visitExpression(code).asNativeBool());
	}

}
