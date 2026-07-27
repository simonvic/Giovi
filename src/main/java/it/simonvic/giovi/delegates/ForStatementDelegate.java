package it.simonvic.giovi.delegates;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import giovi.GioviParserVisitResult;
import giovi.Range;
import giovi.lang.GioviList;
import giovi.lang.GioviMap;
import it.simonvic.giovi.Entry;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.GioviVisitorDelegate;
import it.simonvic.giovi.ValueWrapper;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.exceptions.UnsupportedDeconstruction;
import it.simonvic.giovi.exceptions.UnsupportedPattern;
import it.simonvic.giovi.util.VisitorUtils;

public class ForStatementDelegate extends GioviVisitorDelegate {

	public static GioviParserVisitResult doVisit(
		GioviVisitor gioviVisitor,
		ForStatementContext ctx
	) {
		return new ForStatementDelegate(gioviVisitor, ctx).doVisit();
	}

	private final ForStatementContext ctx;
	private final StatementOrBlockContext bodyCtx;
	private final IteratorPatternContext iteratorPatternCtx;
	private final GioviParserVisitResult iterated;

	public ForStatementDelegate(GioviVisitor gioviVisitor, ForStatementContext ctx) {
		super(gioviVisitor);
		this.ctx = ctx;
		this.bodyCtx = ctx.statementOrBlock();
		this.iteratorPatternCtx = ctx.iteratorPattern();
		var iterated = gioviVisitor.visit(ctx.iterable());
		this.iterated = (iterated instanceof ValueWrapper<?> v) // TODO: figure out a better way to handle vars
			? v.unwrap()
			: iterated;
	}

	/**
	 * Perform the visit of the loop
	 * @return null
	 */
	public GioviParserVisitResult doVisit() {

		switch (iteratorPatternCtx.getChild(0)) {

			case PatternMapNakedContext iCtx when iterated instanceof GioviMap map
				-> forEach(map, iCtx);

			case PatternContext iCtx -> {

				switch (iCtx) {

					case PatternPlaceholderContext _ -> {
						switch (iterated) {
							case Range<?> range -> forEach(range);
							case GioviList list -> forEach(list);
							case GioviMap map -> forEach(map);
							default -> throw new UnsupportedPattern(iCtx, iterated);
						}
					}

					case PatternUnaryContext pCtx -> {
						switch (iterated) {
							case Range<?> range -> forEach(range, pCtx);
							case GioviList list -> forEach(list, pCtx);
							case GioviMap map -> forEach(map, pCtx);
							default -> throw new UnsupportedPattern(iCtx, iterated);
						}
					}

					case PatternListContext pCtx when iterated instanceof GioviList list
						-> forEach(list, pCtx.listPattern().pattern());

					case PatternProductContext pCtx when iterated instanceof GioviList list
						-> forEach(
							list,
							Optional.ofNullable(pCtx.productPattern().whole),
							pCtx.productPattern().patternProductMembers()
						);

					case PatternMapContext pCtx when iterated instanceof GioviMap map
						-> forEach(map, pCtx.mapPattern().patternMapNaked());

					default -> throw new UnsupportedPattern(iCtx, iterated);
				}
			}

			default -> throw new UnsupportedPattern(iteratorPatternCtx, iterated);

		}
		return null;
	}

	// TODO: should iterators be by value?

	/**
	 * Iterator over list with product pattern
	 * @param list
	 * @param wholeOpt
	 * @param patternMembers
	 */
	private void forEach(
		GioviList list,
		Optional<UnaryPatternContext> wholeOpt,
		PatternProductMembersContext patternMembers
	) {
		forEach(list, (i) -> {
			VisitorUtils.deconstruct(
				gioviVisitor,
				gioviVisitor.getCurrentScope(),
				i,
				wholeOpt,
				patternMembers
			);
			gioviVisitor.visit(bodyCtx);
		});
	}

	/**
	 * Iterator over list with a pattern
	 * @param list
	 * @param patternCtx
	 */
	private void forEach(GioviList list, PatternContext patternCtx) {
		Consumer<Entry<?>> iterationStrategy = switch (patternCtx) {

			case PatternPlaceholderContext _ -> (_) -> {
				gioviVisitor.visit(bodyCtx);
			};

			case PatternUnaryContext iCtx -> (i) -> {
				VisitorUtils.deconstruct(gioviVisitor, gioviVisitor.getCurrentScope(), i, iCtx);
				gioviVisitor.visit(bodyCtx);
			};

			case PatternProductContext iCtx -> (i) -> {
				VisitorUtils.deconstruct(gioviVisitor, gioviVisitor.getCurrentScope(), i, iCtx);
				gioviVisitor.visit(bodyCtx);
			};

			case PatternMapContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviMap nestedMap) {
					forEach(nestedMap, vCtx.mapPattern().patternMapNaked());
				} else {
					throw new UnsupportedPattern(vCtx, v.unwrap());
				}
			};

			case PatternListContext iCtx -> (v) -> {
				if (v.unwrap() instanceof GioviList nestedList) {
					forEach(nestedList, iCtx.listPattern().pattern());
				} else {
					throw new UnsupportedPattern(iCtx.listPattern().pattern(), v.unwrap());
				}
			};

			default -> throw new UnsupportedPattern(patternCtx, iterated);

		};

		forEach(list, iterationStrategy);
	}

	/**
	 * Iterator over map with a map pattern
	 * @param map
	 * @param patternMapCtx
	 */
	private void forEach(GioviMap map, PatternMapNakedContext patternMapCtx) {
		Consumer<Entry<?>> keyIterationStrategy = switch (patternMapCtx.key) {

			case PatternMapKeyPlaceholderContext _ -> (_) -> {
				// noop
			};

			case PatternMapKeyUnaryContext kCtx -> (k) -> {
				VisitorUtils.deconstruct(
					gioviVisitor, gioviVisitor.getCurrentScope(), k, kCtx.unaryPattern()
				);
			};

			case PatternMapKeyProductContext kCtx -> (k) -> {
				VisitorUtils.deconstruct(
					gioviVisitor, gioviVisitor.getCurrentScope(), k, kCtx.productPattern()
				);
			};

			default -> throw new UnsupportedDeconstruction(map, patternMapCtx.key);
		};

		Consumer<Entry<?>> valueIterationStrategy = switch (patternMapCtx.value) {

			case PatternPlaceholderContext _ -> (_) -> {
				gioviVisitor.visit(bodyCtx);
			};

			case PatternUnaryContext vCtx -> (v) -> {
				VisitorUtils.deconstruct(gioviVisitor, gioviVisitor.getCurrentScope(), v, vCtx);
				gioviVisitor.visit(bodyCtx);
			};

			case PatternListContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviList nestedList) {
					forEach(nestedList, vCtx.listPattern().pattern());
				} else {
					throw new UnsupportedPattern(vCtx, v.unwrap());
				}
			};

			case PatternMapContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviMap nestedMap) {
					forEach(nestedMap, vCtx.mapPattern().patternMapNaked());
				} else {
					throw new UnsupportedPattern(vCtx, v.unwrap());
				}
			};

			case PatternProductContext vCtx -> (v) -> {
				VisitorUtils.deconstruct(gioviVisitor, gioviVisitor.getCurrentScope(), v, vCtx);
				gioviVisitor.visit(bodyCtx);
			};

			default -> throw new UnsupportedDeconstruction(map, patternMapCtx.value);
		};

		forEach(map, (k, v) -> {
			keyIterationStrategy.accept(k);
			valueIterationStrategy.accept(v);
		});
	}

	//////////////////////////////////////////////////////////////////////////////// Placeholder

	/**
	 * Iterate over range with placeholder pattern
	 * @param range
	 */
	private void forEach(Range<?> range) {
		forEach(range, (_) -> gioviVisitor.visit(bodyCtx));
	}

	/**
	 * Iterate over list with placeholder pattern
	 * @param list
	 */
	private void forEach(GioviList list) {
		forEach(list, (_) -> gioviVisitor.visit(bodyCtx));
	}

	/**
	 * Iterate over map with placeholder pattern
	 * @param map
	 */
	private void forEach(GioviMap map) {
		forEach(map, (_, _) -> gioviVisitor.visit(bodyCtx));
	}

	//////////////////////////////////////////////////////////////////////////////// Unary

	/**
	 * Iterate over range with unary pattern
	 * @param range
	 * @param iCtx
	 */
	private void forEach(Range<?> range, PatternUnaryContext iCtx) {
		forEach(range, (i) -> {
			VisitorUtils.deconstruct(gioviVisitor, gioviVisitor.getCurrentScope(), i, iCtx);
			gioviVisitor.visit(bodyCtx);
		});
	}

	/**
	 * Iterate over list with unary pattern
	 * @param list
	 * @param iCtx
	 */
	private void forEach(GioviList list, PatternUnaryContext iCtx) {
		forEach(list, (i) -> {
			VisitorUtils.deconstruct(gioviVisitor, gioviVisitor.getCurrentScope(), i, iCtx);
			gioviVisitor.visit(bodyCtx);
		});
	}

	/**
	 * Iterate over map with unary context
	 * @param map
	 * @param iCtx
	 */
	private void forEach(GioviMap map, PatternUnaryContext iCtx) {
		forEach(map, (k, _) -> {
			VisitorUtils.deconstruct(gioviVisitor, gioviVisitor.getCurrentScope(), k, iCtx);
			gioviVisitor.visit(bodyCtx);
		});
	}

	/**
	 * Iterate over range and visit the loop body
	 * @param range
	 * @param loopBody
	 */
	private void forEach(
		Range<?> range,
		Consumer<Entry<?>> loopBody
	) {
		for (var entry : range) {
			enterLoopScope();
			loopBody.accept(new Entry<>(Range.toGioviValue(entry)));
			if (getCurrentScope().shouldBreak() || getCurrentScope().shouldReturn()) {
				exitScope();
				break;
			}
			if (getCurrentScope().shouldSkip()) {
				exitScope();
				continue;
			}
			exitScope();
		}
	}

	/**
	 * Iterate over list and visit the loop body
	 * @param list
	 * @param loopBody
	 */
	private void forEach(
		GioviList list,
		Consumer<Entry<?>> loopBody
	) {
		for (var entry : list.value()) {
			enterLoopScope();
			loopBody.accept(entry);
			if (getCurrentScope().shouldBreak() || getCurrentScope().shouldReturn()) {
				exitScope();
				break;
			}
			if (getCurrentScope().shouldSkip()) {
				exitScope();
				continue;
			}
			exitScope();
		}
	}

	/**
	 * Iterate over map and visit the loop body
	 * @param map
	 * @param loopBody
	 */
	private void forEach(
		GioviMap map,
		BiConsumer<Entry<?>, Entry<?>> loopBody
	) {
		for (var entry : map.value().entrySet()) {
			enterLoopScope();
			loopBody.accept(entry.getKey(), entry.getValue());
			if (getCurrentScope().shouldBreak() || getCurrentScope().shouldReturn()) {
				exitScope();
				break;
			}
			if (getCurrentScope().shouldSkip()) {
				exitScope();
				continue;
			}
			exitScope();
		}
	}


}
