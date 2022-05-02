package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMathExpressions {


	private GioviObject run(String text) {
		GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		GioviParser parser = new GioviParser(tokens);
		GioviVisitor visitor = new GioviVisitor();
		return visitor.visit(parser.expression());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		10 + 5, 15
		10 - 5, 5
		10 + -5, 5
		10 - -5, 15
		-10 - -5, -5
		"""
	)
	public void testAdditiveExpression(String code, Integer expected) {
		assertEquals(expected, run(code).asInt());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		2.0 * 2.0, 4
		0.5 * 4, 2
		10.0 / 2, 5
		10 / 0.5, 20
		"""
	)
	public void testMultiplicativeExpression(String code, Double expected) {
		assertEquals(expected, run(code).asDouble());
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
	public void testPowerExpression(String code, Double expected) {
		assertEquals(expected, run(code).asDouble());
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
	public void testModuloExpression(String code, Integer expected) {
		assertEquals(expected, run(code).asInt());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		10 * {1 + 1}, 20
		10 * {2 * {3 + 2}}, 100
		"""
	)
	public void testParenthesizedExpression(String code, Integer expected) {
		assertEquals(expected, run(code).asInt());
	}

}
