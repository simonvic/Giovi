package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.lang.GioviObject;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestContainmentExpression {

	private GioviObject run(String text) {
		GioviLexer lexer = new GioviLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		GioviParser parser = new GioviParser(tokens);
		GioviVisitor visitor = new GioviVisitor();
		return visitor.visit(parser.expression());
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
		"simone" =~ "mon", true
		"mon" ~= "simone", true
		"simone" =~ "zzz", false
		"zzz" ~= "simone", false
		"""
	)
	void testStrings(String code, boolean expected) {
		assertEquals(expected, run(code).asBoolean());
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
		assertEquals(expected, run(code).asBoolean());
	}
}
