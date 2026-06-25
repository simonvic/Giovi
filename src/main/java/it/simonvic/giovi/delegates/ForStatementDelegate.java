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
	private final PatternContext patternCtx;
	private final GioviParserVisitResult iterated;

	public ForStatementDelegate(GioviVisitor gioviVisitor, ForStatementContext ctx) {
		super(gioviVisitor);
		this.ctx = ctx;
		this.bodyCtx = ctx.statementOrBlock();
		this.patternCtx = ctx.pattern();
		var iterated = gioviVisitor.visit(ctx.iterable());
		this.iterated = (iterated instanceof Var<?> v) // TODO: figure out a better way to handle vars
			? v.unwrap()
			: iterated;
	}

	// TODO: check if necessary to new VarD when deconstructing
	// TODO: check when declaring if it should be copied (by value) (e.g. iterating over a tuple)

	public GioviParserVisitResult doVisit() {

		switch (patternCtx) {

			case PatternTopLevelMapContext iCtx when iterated instanceof GioviMap map
				-> forEach(map, iCtx.patternMap());

			case PatternTopLevelNestedContext nestedCtx -> {

				switch (nestedCtx.patternNested()) {

					case PatternNestedPlaceholderContext _ -> {
						switch (iterated) {
							case Range<?> range -> forEach(range);
							case GioviList list -> forEach(list);
							case GioviMap map -> forEach(map);
							default -> throw new UnsupportedPattern(nestedCtx, iterated);
						}
					}

					case PatternNestedUnaryContext iCtx -> {
						switch (iterated) {
							case Range<?> range -> forEach(range, iCtx.patternUnary());
							case GioviList list -> forEach(list, iCtx.patternUnary());
							case GioviMap map -> forEach(map, iCtx.patternUnary());
							default -> throw new UnsupportedPattern(nestedCtx, iterated);
						}
					}

					case PatternNestedListContext iCtx when iterated instanceof GioviList list
						-> forEach(list, iCtx.patternNested());

					case PatternNestedProductContext iCtx when iterated instanceof GioviList list
						-> forEach(
							list,
							Optional.ofNullable(iCtx.whole),
							iCtx.patternProductMembers()
						);

					case PatternNestedMapContext iCtx when iterated instanceof GioviMap map
						-> forEach(map, iCtx.patternMap());

					default -> throw new UnsupportedPattern(nestedCtx, iterated);
				}
			}

			default -> throw new UnsupportedPattern(patternCtx, iterated);

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

	private void forEach(GioviList list, PatternNestedContext patternNested) {
		Consumer<Var<?>> iterationStrategy = switch (patternNested) {

			case PatternNestedPlaceholderContext _ -> (_) -> {
				gioviVisitor.visit(bodyCtx);
			};

			case PatternNestedUnaryContext iCtx -> (i) -> {
				deconstruct(i, iCtx.patternUnary());
				gioviVisitor.visit(bodyCtx);
			};

			case PatternNestedProductContext iCtx -> (i) -> {
				deconstruct(i, iCtx);
				gioviVisitor.visit(bodyCtx);
			};

			case PatternNestedMapContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviMap nestedMap) {
					forEach(nestedMap, vCtx.patternMap());
				} else {
					throw new UnsupportedPattern(vCtx, v.unwrap());
				}
			};

			case PatternNestedListContext iCtx -> (v) -> {
				if (v.unwrap() instanceof GioviList nestedList) {
					forEach(nestedList, iCtx.patternNested());
				} else {
					throw new UnsupportedPattern(iCtx.patternNested(), v.unwrap());
				}
			};

			default -> throw new UnsupportedPattern(patternNested, iterated);

		};

		forEach(list, iterationStrategy);
	}

	private void forEach(
		GioviMap map,
		PatternMapContext patternMapCtx
	) {

		Consumer<Var<?>> keyIterationStrategy = switch (patternMapCtx.key) {

			case PatternNestedPlaceholderContext _ -> (_) -> {
				// do nothing
			};

			case PatternNestedUnaryContext kCtx -> (k) -> {
				deconstruct(k, kCtx.patternUnary());
			};

			case PatternNestedProductContext kCtx -> (k) -> {
				deconstruct(k, kCtx);
			};

			default -> throw new UnsupportedDeconstruction(map, patternMapCtx.key);
		};

		Consumer<Var<?>> valueIterationStrategy = switch (patternMapCtx.value) {

			case PatternNestedPlaceholderContext _ -> (_) -> {
				gioviVisitor.visit(bodyCtx);
			};

			case PatternNestedUnaryContext vCtx -> (v) -> {
				deconstruct(v, vCtx.patternUnary());
				gioviVisitor.visit(bodyCtx);
			};

			case PatternNestedListContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviList nestedList) {
					forEach(nestedList, vCtx.patternNested());
				} else {
					throw new UnsupportedPattern(vCtx, v.unwrap());
				}
			};

			case PatternNestedMapContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviMap nestedMap) {
					forEach(nestedMap, vCtx.patternMap());
				} else {
					throw new UnsupportedPattern(vCtx, v.unwrap());
				}
			};

			case PatternNestedProductContext vCtx -> (v) -> {
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

	private void deconstruct(Var<?> i, PatternNestedProductContext iCtx) {
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
						-> deconstructPositional(struct.value(), pCtx.patternNested());
					case GioviRecord record
						-> deconstructPositional(record.value(), pCtx.patternNested());
					case GioviTuple tuple
						-> deconstructPositional(tuple.value(), pCtx.patternNested());
					default
						-> throw new UnsupportedDeconstruction(i.unwrap(), iCtx);
				}
			}
			case PatternProductNamedContext nCtx -> {
				switch (i.unwrap()) {
					case GioviStruct struct
						-> deconstructNamed(struct.value(), nCtx.patternNested());
					case GioviRecord record
						-> deconstructNamed(record.value(), nCtx.patternNested());
					default
						-> throw new UnsupportedDeconstruction(i.unwrap(), iCtx);
				}
			}
			default -> throw new UnsupportedDeconstruction(i.unwrap(), iCtx);
		}
	}

	private void deconstructNamed(
		Record record,
		List<PatternNestedContext> list
	) {
		for (var patternCtx : list) {
			switch (patternCtx) {

				case PatternNestedPlaceholderContext _ -> {
					// do nothing
				}

				case PatternNestedUnaryContext iCtx -> {
					String fieldIdentifier = iCtx.patternUnary().identifier().getText();
					var field = record.getField(fieldIdentifier)
						.orElseThrow(
							() -> new UnsupportedDeconstruction(
								record,
								iCtx.patternUnary(),
								new NoSuchField(fieldIdentifier)
							)
						);
					deconstruct(field, iCtx.patternUnary());
				}

				case PatternNestedProductContext iCtx -> {
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
		List<PatternNestedContext> patterns
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
			switch (patternCtx) {
				case PatternNestedPlaceholderContext _ -> { /* do nothing */ }
				case PatternNestedUnaryContext iCtx -> deconstruct(field, iCtx.patternUnary());
				case PatternNestedProductContext iCtx -> deconstruct(field, iCtx);
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
