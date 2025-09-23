package it.simonvic.giovi.lsp;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.SyntaxTree;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class LSPUtils {

	public static Range range(ParserRuleContext from, ParserRuleContext to) {
		if (from == to) {
			return range(from);
		}
		return new Range(
			new Position(from.getStart().getLine() - 1, from.getStart().getCharPositionInLine()),
			new Position(to.getStop().getLine() - 1, to.getStop().getCharPositionInLine()));
	}

	public static Range range(ParserRuleContext ctx) {
		return new Range(
			new Position(ctx.getStart().getLine() - 1, ctx.getStart().getCharPositionInLine()),
			new Position(ctx.getStop().getLine() - 1, ctx.getStop().getCharPositionInLine()));
	}

	public static List<Token> getHiddenTokensToLeft(SyntaxTree node, CommonTokenStream tokens) {
		var t = tokens.getHiddenTokensToLeft(node.getSourceInterval().a);
		return (t != null) ? t : Collections.emptyList();
	}

	public static List<Token> getHiddenTokensToRight(SyntaxTree node, CommonTokenStream tokens) {
		var t = tokens.getHiddenTokensToRight(node.getSourceInterval().b);
		return (t != null) ? t : Collections.emptyList();
	}

}

