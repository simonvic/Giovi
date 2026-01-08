package it.simonvic.giovi.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.antlr.TestUnitLexer;
import it.simonvic.giovi.antlr.TestUnitParser;

public class TestParser {

	@Test
	void parse() throws IOException, URISyntaxException {
		// var url = getClass().getClassLoader().getResource("corpus/foo.txt");
		var path = Path.of("/home/simonvic/Documents/Productivity/Programming/JAVA/Giovi/src/test/resources/corpus/foo.txt");
		var lexer = new TestUnitLexer(CharStreams.fromPath(path));
		var tokens = new CommonTokenStream(lexer);
		var parser = new TestUnitParser(tokens);
		var visitor = new TestUnitVisitor();
		visitor.visit(parser.begin());
	}


}
