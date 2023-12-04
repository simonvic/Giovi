package it.simonvic.giovi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;

public class GioviShell {

	public void run() throws RecognitionException, IOException {
		var reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		var visitor = new GioviVisitor();
		System.out.print("┃ ");
		while ((line = reader.readLine()) != null) {
			GioviLexer lexer = new GioviLexer(CharStreams.fromString(line));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			GioviParser parser = new GioviParser(tokens);
			ParseTree tree = parser.begin();
			try {
				visitor.visit(tree);
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
			System.out.print("┃ ");
		}
	}

}
