package it.simonvic.giovi.lsp;

import org.antlr.v4.runtime.ParserRuleContext;
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


}

