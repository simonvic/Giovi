package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBoolExpressions {


	private Object run(String text) {
		it.simonvic.giovi.antlr.GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		it.simonvic.giovi.antlr.GioviParser parser = new GioviParser(tokens);
		GioviVisitor visitor = new GioviVisitor();
		return visitor.visit(parser.expression());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		true, true
		false, false
		"""
	)
	public void testLiterals(String code, boolean expected) {
		assertEquals(expected, run(code));
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
		assertEquals(expected, run(code));
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
		assertEquals(expected, run(code));
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
		assertEquals(expected, run(code));
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
		assertEquals(expected, run(code));
	}

}
