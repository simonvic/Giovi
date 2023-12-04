package it.simonvic.giovi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.GioviObject;

public class Giovi {

	public static void main(String[] args) throws IOException {
		CharStream charStream;
		if (args.length == 0) return;
		switch (args[0]) {
			case "-i", "--interactive" -> {
				interactiveMode();
				return;
			}
			case "-d", "--debug" -> charStream = CharStreams
				.fromStream(Giovi.class.getResourceAsStream("/sample.giovi"));
			case "-" -> charStream = CharStreams.fromStream(System.in);
			case "--" -> charStream = CharStreams.fromString(args[1]);
			default -> charStream = CharStreams.fromStream(new FileInputStream(args[0]));
		}
		var lexer = new GioviLexer(charStream);
		var parser = new GioviParser(new CommonTokenStream(lexer));
		var tree = parser.begin();
		var visitor = new GioviVisitor();
		try {
			visitor.visit(tree);
		} catch (GioviException ex) {
			System.err.println(ex.getMessage());
		}
	}

	private static void interactiveMode() throws IOException {
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
