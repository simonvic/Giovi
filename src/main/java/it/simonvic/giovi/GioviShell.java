package it.simonvic.giovi;

import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;

public class GioviShell {

	public void run() throws RecognitionException, IOException {
		var console = System.console();
		var visitor = new GioviVisitor();
		System.out.print("┃ ");
		String line;
		while ((line = console.readLine()) != null) {
			var lexer = new GioviLexer(CharStreams.fromString(line));
			var parser = new GioviParser(new CommonTokenStream(lexer));
			var tree = parser.begin();
			try {
				visitor.visit(tree);
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
			System.out.print("┃ ");
		}
	}

}
