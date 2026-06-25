package it.simonvic.giovi.delegates;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import giovi.GioviParserVisitResult;
import giovi.Range;
import giovi.Record;
import giovi.Tuple;
import giovi.lang.*;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.GioviVisitorDelegate;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.UnsupportedDeconstruction;
import it.simonvic.giovi.exceptions.UnsupportedPattern;

public class ForStatementDelegate extends GioviVisitorDelegate {

	// TODO: based on pattern, flatten iterated value first and then iterate, instead of doing whatever this is

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
		this.iterated = (iterated instanceof Var<?> v) // TODO: figure out a better way to handle vars
			? v.unwrap()
			: iterated;
	}

	// TODO: check if necessary to new VarD when deconstructing
	// TODO: check when declaring if it should be copied (by value) (e.g. iterating over a tuple)

	public GioviParserVisitResult doVisit() {

		switch (iteratorPatternCtx.getChild(0)) {

			case PatternMapNakedContext iCtx when iterated instanceof GioviMap map
				-> forEach(map, iCtx);

			case PatternContext iCtx -> {

				switch (iCtx.getChild(0)) {

					case LiteralPlaceholderContext _ -> {
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
						-> forEach(list, pCtx.pattern());

					case PatternProductContext pCtx when iterated instanceof GioviList list
						-> forEach(
							list,
							Optional.ofNullable(pCtx.whole),
							pCtx.patternProductMembers()
						);

					case PatternMapContext pCtx when iterated instanceof GioviMap map
						-> forEach(map, pCtx.patternMapNaked());

					default -> throw new UnsupportedPattern(iCtx, iterated);
				}
			}

			default -> throw new UnsupportedPattern(iteratorPatternCtx, iterated);

		}
		return null;
	}

	// TODO: should iterators be by value?
	// TODO: for r Real in [1 2 3] do {} declare r first, then widen the entries

	/**
	 * Positional
	 * @param list
	 * @param wholeOpt
	 * @param patternMembers
	 */
	private void forEach(
		GioviList list,
		Optional<PatternUnaryContext> wholeOpt,
		PatternProductMembersContext patternMembers
	) {
		forEach(list, (i) -> {
			deconstruct(i, wholeOpt, patternMembers);
			gioviVisitor.visit(bodyCtx);
		});
	}

	private void forEach(GioviList list, PatternContext patternCtx) {
		Consumer<Var<?>> iterationStrategy = switch (patternCtx.getChild(0)) {

			case LiteralPlaceholderContext _ -> (_) -> {
				gioviVisitor.visit(bodyCtx);
			};

			case PatternUnaryContext iCtx -> (i) -> {
				deconstruct(i, iCtx);
				gioviVisitor.visit(bodyCtx);
			};

			case PatternProductContext iCtx -> (i) -> {
				deconstruct(i, iCtx);
				gioviVisitor.visit(bodyCtx);
			};

			case PatternMapContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviMap nestedMap) {
					forEach(nestedMap, vCtx.patternMapNaked());
				} else {
					throw new UnsupportedPattern(vCtx, v.unwrap());
				}
			};

			case PatternListContext iCtx -> (v) -> {
				if (v.unwrap() instanceof GioviList nestedList) {
					forEach(nestedList, iCtx.pattern());
				} else {
					throw new UnsupportedPattern(iCtx.pattern(), v.unwrap());
				}
			};

			default -> throw new UnsupportedPattern(patternCtx, iterated);

		};

		forEach(list, iterationStrategy);
	}

	private void forEach(
		GioviMap map,
		PatternMapNakedContext patternMapCtx
	) {

		Consumer<Var<?>> keyIterationStrategy = switch (patternMapCtx.key.getChild(0)) {

			case LiteralPlaceholderContext _ -> (_) -> {
				// do nothing
			};

			case PatternUnaryContext kCtx -> (k) -> {
				deconstruct(k, kCtx);
			};

			case PatternProductContext kCtx -> (k) -> {
				deconstruct(k, kCtx);
			};

			default -> throw new UnsupportedDeconstruction(map, patternMapCtx.key);
		};

		Consumer<Var<?>> valueIterationStrategy = switch (patternMapCtx.value.getChild(0)) {

			case LiteralPlaceholderContext _ -> (_) -> {
				gioviVisitor.visit(bodyCtx);
			};

			case PatternUnaryContext vCtx -> (v) -> {
				deconstruct(v, vCtx);
				gioviVisitor.visit(bodyCtx);
			};

			case PatternListContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviList nestedList) {
					forEach(nestedList, vCtx.pattern());
				} else {
					throw new UnsupportedPattern(vCtx, v.unwrap());
				}
			};

			case PatternMapContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviMap nestedMap) {
					forEach(nestedMap, vCtx.patternMapNaked());
				} else {
					throw new UnsupportedPattern(vCtx, v.unwrap());
				}
			};

			case PatternProductContext vCtx -> (v) -> {
				deconstruct(v, vCtx);
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

	private void forEach(Range<?> range) {
		forEach(range, (_) -> gioviVisitor.visit(bodyCtx));
	}

	private void forEach(GioviList list) {
		forEach(list, (_) -> gioviVisitor.visit(bodyCtx));
	}

	private void forEach(GioviMap map) {
		forEach(map, (_, _) -> gioviVisitor.visit(bodyCtx));
	}

	//////////////////////////////////////////////////////////////////////////////// Unary

	private void forEach(Range<?> range, PatternUnaryContext iCtx) {
		forEach(range, (i) -> {
			deconstruct(i, iCtx);
			gioviVisitor.visit(bodyCtx);
		});
	}

	private void forEach(GioviList list, PatternUnaryContext iCtx) {
		forEach(list, (i) -> {
			deconstruct(i, iCtx);
			gioviVisitor.visit(bodyCtx);
		});
	}

	private void forEach(GioviMap map, PatternUnaryContext iCtx) {
		forEach(map, (k, _) -> {
			deconstruct(k, iCtx);
			gioviVisitor.visit(bodyCtx);
		});
	}

	//////////////////////////////////////////////////////////////////////////////// Deconstruct

	private void deconstruct(Var<?> i, PatternUnaryContext patternUnary) {
		getCurrentScope().declare(
			patternUnary.identifier().getText(),
			new VarD<>(i.copy().unwrap())
		);
	}

	private void deconstruct(Var<?> i, PatternProductContext iCtx) {
		deconstruct(i, Optional.ofNullable(iCtx.whole), iCtx.patternProductMembers());
	}

	private void deconstruct(
		Var<?> i,
		Optional<PatternUnaryContext> wholeCtx,
		PatternProductMembersContext iCtx
	) {
		wholeCtx.ifPresent(
			wCtx -> getCurrentScope().declare(
				wCtx.identifier().getText(),
				new VarD<>(i.copy().unwrap())
			)
		);
		switch (iCtx) {
			case PatternProductPositionalContext pCtx -> {
				switch (i.unwrap()) {
					case GioviStruct struct
						-> deconstructPositional(struct.value(), pCtx.patternMember());
					case GioviRecord record
						-> deconstructPositional(record.value(), pCtx.patternMember());
					case GioviTuple tuple
						-> deconstructPositional(tuple.value(), pCtx.patternMember());
					default
						-> throw new UnsupportedDeconstruction(i.unwrap(), iCtx);
				}
			}
			case PatternProductNamedContext nCtx -> {
				switch (i.unwrap()) {
					case GioviStruct struct
						-> deconstructNamed(struct.value(), nCtx.patternMember());
					case GioviRecord record
						-> deconstructNamed(record.value(), nCtx.patternMember());
					default
						-> throw new UnsupportedDeconstruction(i.unwrap(), iCtx);
				}
			}
			default -> throw new UnsupportedDeconstruction(i.unwrap(), iCtx);
		}
	}

	private void deconstructNamed(
		Record record,
		List<PatternMemberContext> list
	) {
		for (var patternCtx : list) {
			switch (patternCtx.getChild(0)) {

				case LiteralPlaceholderContext _ -> {
					// do nothing
				}

				case PatternUnaryContext iCtx -> {
					String fieldIdentifier = iCtx.identifier().getText();
					var field = record.getField(fieldIdentifier)
						.orElseThrow(
							() -> new UnsupportedDeconstruction(
								record,
								iCtx,
								new NoSuchField(fieldIdentifier)
							)
						);
					deconstruct(field, iCtx);
				}

				case PatternProductContext iCtx -> {
					if (iCtx.whole == null) {
						throw new UnsupportedDeconstruction(
							record,
							iCtx,
							new GioviException("Whole field is required with named deconstruction")
						);
					}
					String fieldIdentifier = iCtx.whole.identifier().getText();
					var field = record.getField(fieldIdentifier)
						.orElseThrow(
							() -> new UnsupportedDeconstruction(
								record,
								iCtx.patternUnary(),
								new NoSuchField(fieldIdentifier)
							)
						);
					deconstruct(field, iCtx);
				}

				default -> throw new UnsupportedDeconstruction(record, patternCtx);
			}
		}
	}

	private void deconstructPositional(
		Tuple tuple,
		List<PatternMemberContext> patterns
	) {
		for (int i = 0; i < patterns.size(); i++) {
			final int j = i;
			var field = tuple.getField(i)
				.orElseThrow(
					() -> new UnsupportedDeconstruction(
						tuple,
						new NoSuchField(j)
					)
				);
			var patternCtx = patterns.get(i);
			switch (patternCtx.getChild(0)) {
				case LiteralPlaceholderContext _ -> { /* do nothing */ }
				case PatternUnaryContext iCtx -> deconstruct(field, iCtx);
				case PatternProductContext iCtx -> deconstruct(field, iCtx);
				default -> throw new UnsupportedDeconstruction(tuple, patternCtx);
			}
		}
	}

	/**
	 * @param range
	 * @param loopBody
	 */
	private void forEach(
		Range<?> range,
		Consumer<Var<?>> loopBody
	) {
		// TODO: hacky way of iterating range
		for (var entry : range.expandAsGioviList().value()) {
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
	 * @param list
	 * @param loopBody
	 */
	private void forEach(
		GioviList list,
		Consumer<Var<?>> loopBody
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
	 * @param map
	 * @param loopBody
	 */
	private void forEach(
		GioviMap map,
		BiConsumer<Var<?>, Var<?>> loopBody
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
