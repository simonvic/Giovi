package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRelationalExpressions {

	private GioviObject run(String text) {
		it.simonvic.giovi.antlr.GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		it.simonvic.giovi.antlr.GioviParser parser = new GioviParser(tokens);
		GioviVisitor visitor = new GioviVisitor();
		return visitor.visit(parser.expression());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		10 > 0, true
		0 > 10, false
		-1 > -5, true
		10 > -10, true
		-10 > 10, false
		"""
	)
	public void testGreater(String code, boolean expected) {
		assertEquals(expected, run(code).asBoolean());
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
	public void testLess(String code, boolean expected) {
		assertEquals(expected, run(code).asBoolean());
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
	public void testLessEqual(String code, boolean expected) {
		assertEquals(expected, run(code).asBoolean());
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
	public void testGreaterEqual(String code, boolean expected) {
		assertEquals(expected, run(code).asBoolean());
	}

}
