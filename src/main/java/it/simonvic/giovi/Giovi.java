package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.GioviObject;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Giovi {

	public static void main(String[] args) throws IOException {
		CharStream charStream;
		if (args.length == 0) return;
		switch (args[0]) {
			case "-i", "--interactive" -> {
				interactiveMode();
				return;
			}
			case "-d", "--debug" ->
				charStream = CharStreams.fromStream(Giovi.class.getResourceAsStream("/sample.giovi"));
			case "-" -> charStream = CharStreams.fromStream(System.in);
			case "--" -> charStream = CharStreams.fromString(args[1]);
			default -> charStream = CharStreams.fromStream(new FileInputStream(args[0]));
		}
		GioviLexer lexer = new GioviLexer(charStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		GioviParser parser = new GioviParser(tokens);
		ParseTree tree = parser.begin();
		GioviVisitor visitor = new GioviVisitor();
		try {
			visitor.visit(tree);
		} catch (GioviException ex) {
			System.err.println(ex.getMessage());
		}
	}

	private static void interactiveMode() throws IOException {
		var reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		GioviVisitor visitor = new GioviVisitor();
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

	public static GioviVisitor visit(String code) {
		var lexer = new GioviLexer(CharStreams.fromString(code));
		var tokens = new CommonTokenStream(lexer);
		var visitor = new GioviVisitor();
		visitor.visit(new GioviParser(tokens).begin());
		return visitor;
	}

	public static GioviParser parserOf(String code) {
		GioviLexer lexer = new GioviLexer(CharStreams.fromString(code));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new GioviParser(tokens);
	}

	public static GioviObject runExpression(String code) {
		return new GioviVisitor().visit(parserOf(code).expression());
	}

	public static GioviObject run(String code) {
		return new GioviVisitor().visit(parserOf(code).begin());
	}

}
