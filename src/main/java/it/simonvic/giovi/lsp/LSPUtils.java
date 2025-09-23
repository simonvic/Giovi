package it.simonvic.giovi.lsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.SyntaxTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class LSPUtils {

	/**
	 * Compute the (LSP) range going from the start of one node to the end of another
	 * @param from
	 * @param to
	 * @return range
	 */
	public static Range range(ParserRuleContext from, ParserRuleContext to) {
		if (from == to) {
			return range(from);
		}
		return new Range(
			new Position(from.getStart().getLine() - 1, from.getStart().getCharPositionInLine()),
			new Position(to.getStop().getLine() - 1, to.getStop().getCharPositionInLine()));
	}

	/**
	 * Compute the (LSP) range going from the start to the end of a node
	 * @param ctx
	 * @return range
	 */
	public static Range range(ParserRuleContext ctx) {
		return new Range(
			new Position(ctx.getStart().getLine() - 1, ctx.getStart().getCharPositionInLine()),
			new Position(ctx.getStop().getLine() - 1, ctx.getStop().getCharPositionInLine()));
	}

	/**
	 * Get hidden tokens to the left of the given node up until we see a token on
	 * DEFAULT_TOKEN_CHANNEL.
	 * @param node
	 * @param tokens
	 * @return list of hidden tokens
	 */
	public static List<Token> getHiddenTokensToLeft(SyntaxTree node, CommonTokenStream tokens) {
		var t = tokens.getHiddenTokensToLeft(node.getSourceInterval().a);
		return (t != null) ? t : Collections.emptyList();
	}

	/**
	 * Get hidden tokens to the right of the given node up until we see a token on
	 * DEFAULT_TOKEN_CHANNEL.
	 * @param node
	 * @param tokens
	 * @return list of hidden tokens
	 */
	public static List<Token> getHiddenTokensToRight(SyntaxTree node, CommonTokenStream tokens) {
		var t = tokens.getHiddenTokensToRight(node.getSourceInterval().b);
		return (t != null) ? t : Collections.emptyList();
	}

	/**
	 * From a node, traverse the the tree upwards until an ancestor of the given type is found
	 * @param <T>
	 * @param leaf
	 * @param klass
	 * @return ancestor or null if the node is the root
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ParserRuleContext> Optional<T> findClosestAncestor(
		ParseTree node,
		Class<T> klass
	) {
		var ancestor = node.getParent();
		while (ancestor != null && !klass.isInstance(ancestor)) {
			ancestor = ancestor.getParent();
		}
		return (ancestor == null)
			? Optional.empty()
			: Optional.of((T) ancestor);
	}

	/**
	 * Get a list of the ancestors ordered from the direct parent to the root of the tree
	 * @param node
	 * @return ancestors
	 */
	public static List<ParseTree> getHierarchy(ParseTree node) {
		var hierarchy = new ArrayList<ParseTree>();
		ParseTree parent = node.getParent();
		while (parent != null) {
			hierarchy.add(parent);
			parent = parent.getParent();
		}
		return hierarchy;
	}

	/**
	 * Find the terminal node at the given position
	 * @param tree
	 * @param position
	 * @return leaf if found
	 */
	public static Optional<TerminalNode> findLeaf(ParseTree tree, Position position) {
		return switch (tree) {
			case TerminalNode leaf -> {
				var token = leaf.getSymbol();
				if (token.getLine() - 1 != position.getLine()) {
					yield Optional.empty();
				}

				int tokenStart = token.getCharPositionInLine();
				int tokenStop = tokenStart + (token.getStopIndex() - token.getStartIndex());
				if (tokenStart <= position.getCharacter() && position.getCharacter() <= tokenStop) {
					yield Optional.of(leaf);
				}
				yield Optional.empty();
			}
			case ParserRuleContext ctx -> {
				if (ctx.getStart().getLine() - 1 > position.getLine()) {
					yield Optional.empty();
				}
				if (ctx.getStart().getLine() - 1 == position.getLine()
					&& position.getCharacter() < ctx.getStart().getCharPositionInLine()) {
					yield Optional.empty();
				}
				int childCount = ctx.getChildCount();
				for (int i = 0; i < childCount; i++) {
					var childCtx = ctx.getChild(i);
					var result = findLeaf(childCtx, position);
					if (result.isPresent()) {
						yield result;
					}
				}
				yield Optional.empty();
			}
			default -> Optional.empty();
		};
	}

}

