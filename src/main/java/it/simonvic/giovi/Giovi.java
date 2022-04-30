package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class Giovi {

	public static void main(String[] args) throws IOException {
		GioviLexer lexer = new GioviLexer(CharStreams.fromStream(Giovi.class.getResourceAsStream("/sample.giovi")));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		GioviParser parser = new GioviParser(tokens);
		ParseTree tree = parser.begin();

		GioviVisitor visitor = new GioviVisitor();
		visitor.visit(tree);
		System.out.println("-------------");
	}

}
