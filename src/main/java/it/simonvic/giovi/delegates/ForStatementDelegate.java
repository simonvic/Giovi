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
import it.simonvic.giovi.exceptions.UnsupportedIterator;

public class ForStatementDelegate extends GioviVisitorDelegate {

	// TODO: based on iterator, flatten iterated value first and then iterate, instead of doing whatever this is

	public static GioviParserVisitResult doVisit(
		GioviVisitor gioviVisitor,
		ForStatementContext ctx
	) {
		return new ForStatementDelegate(gioviVisitor, ctx).doVisit();
	}

	private final ForStatementContext ctx;
	private final StatementOrBlockContext bodyCtx;
	private final IteratorContext iteratorCtx;
	private final GioviParserVisitResult iterated;

	public ForStatementDelegate(GioviVisitor gioviVisitor, ForStatementContext ctx) {
		super(gioviVisitor);
		this.ctx = ctx;
		this.bodyCtx = ctx.statementOrBlock();
		this.iteratorCtx = ctx.iterator();
		var iterated = gioviVisitor.visit(ctx.iterable());
		this.iterated = (iterated instanceof Var<?> v) // TODO: figure out a better way to handle vars
			? v.unwrap()
			: iterated;
	}

	// TODO: check if necessary to new VarD when deconstructing
	// TODO: check when declaring if it should be copied (by value) (e.g. iterating over a tuple)

	public GioviParserVisitResult doVisit() {

		switch (iteratorCtx) {

			case IteratorTopLevelMapContext iCtx when iterated instanceof GioviMap map
				-> forEach(map, iCtx.iteratorMap());

			case IteratorTopLevelProductContext iCtx when iterated instanceof GioviList list
				-> forEach(list, Optional.empty(), iCtx.iteratorProductFields());

			case IteratorTopLevelNestedContext nestedCtx -> {

				switch (nestedCtx.nestedIterator()) {

					case IteratorNestedPlaceholderContext _ -> {
						switch (iterated) {
							case Range<?> range -> forEach(range);
							case GioviList list -> forEach(list);
							case GioviMap map -> forEach(map);
							default -> throw new UnsupportedIterator(nestedCtx, iterated);
						}
					}

					case IteratorNestedUnaryContext iCtx -> {
						switch (iterated) {
							case Range<?> range -> forEach(range, iCtx.unaryIterator());
							case GioviList list -> forEach(list, iCtx.unaryIterator());
							case GioviMap map -> forEach(map, iCtx.unaryIterator());
							default -> throw new UnsupportedIterator(nestedCtx, iterated);
						}
					}

					case IteratorNestedListContext iCtx when iterated instanceof GioviList list
						-> forEach(list, iCtx.nestedIterator());

					case IteratorNestedProductContext iCtx when iterated instanceof GioviList list
						-> forEach(
							list,
							Optional.ofNullable(iCtx.whole),
							iCtx.iteratorProductFields()
						);

					case IteratorNestedMapContext iCtx when iterated instanceof GioviMap map
						-> forEach(map, iCtx.iteratorMap());

					default -> throw new UnsupportedIterator(nestedCtx, iterated);
				}
			}

			default -> throw new UnsupportedIterator(iteratorCtx, iterated);

		}
		return null;
	}

	// TODO: should iterators be by value?
	// TODO: for r Real in [1 2 3] do {} declare r first, then widen the entries

	/**
	 * Positional
	 * @param list
	 * @param wholeOpt
	 * @param iteratorProductFields
	 */
	private void forEach(
		GioviList list,
		Optional<UnaryIteratorContext> wholeOpt,
		IteratorProductFieldsContext iteratorProductFields
	) {
		forEach(list, (i) -> {
			deconstruct(i, wholeOpt, iteratorProductFields);
			gioviVisitor.visit(bodyCtx);
		});
	}

	private void forEach(GioviList list, NestedIteratorContext nestedIterator) {
		Consumer<Var<?>> iterationStrategy = switch (nestedIterator) {

			case IteratorNestedPlaceholderContext _ -> (_) -> {
				gioviVisitor.visit(bodyCtx);
			};

			case IteratorNestedUnaryContext iCtx -> (i) -> {
				deconstruct(i, iCtx.unaryIterator());
				gioviVisitor.visit(bodyCtx);
			};

			case IteratorNestedProductContext iCtx -> (i) -> {
				deconstruct(i, iCtx);
				gioviVisitor.visit(bodyCtx);
			};

			case IteratorNestedMapContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviMap nestedMap) {
					forEach(nestedMap, vCtx.iteratorMap());
				} else {
					throw new UnsupportedIterator(vCtx, v.unwrap());
				}
			};

			case IteratorNestedListContext iCtx -> (v) -> {
				if (v.unwrap() instanceof GioviList nestedList) {
					forEach(nestedList, iCtx.nestedIterator());
				} else {
					throw new UnsupportedIterator(iCtx.nestedIterator(), v.unwrap());
				}
			};

			default -> throw new UnsupportedIterator(nestedIterator, iterated);

		};

		forEach(list, iterationStrategy);
	}

	private void forEach(
		GioviMap map,
		IteratorMapContext iteratorMapCtx
	) {

		Consumer<Var<?>> keyIterationStrategy = switch (iteratorMapCtx.key) {

			case IteratorNestedPlaceholderContext _ -> (_) -> {
				// do nothing
			};

			case IteratorNestedUnaryContext kCtx -> (k) -> {
				deconstruct(k, kCtx.unaryIterator());
			};

			case IteratorNestedProductContext kCtx -> (k) -> {
				deconstruct(k, kCtx);
			};

			default -> throw new UnsupportedDeconstruction(map, iteratorMapCtx.key);
		};

		Consumer<Var<?>> valueIterationStrategy = switch (iteratorMapCtx.value) {

			case IteratorNestedPlaceholderContext _ -> (_) -> {
				gioviVisitor.visit(bodyCtx);
			};

			case IteratorNestedUnaryContext vCtx -> (v) -> {
				deconstruct(v, vCtx.unaryIterator());
				gioviVisitor.visit(bodyCtx);
			};

			case IteratorNestedListContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviList nestedList) {
					forEach(nestedList, vCtx.nestedIterator());
				} else {
					throw new UnsupportedIterator(vCtx, v.unwrap());
				}
			};

			case IteratorNestedMapContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviMap nestedMap) {
					forEach(nestedMap, vCtx.iteratorMap());
				} else {
					throw new UnsupportedIterator(vCtx, v.unwrap());
				}
			};

			case IteratorNestedProductContext vCtx -> (v) -> {
				deconstruct(v, vCtx);
				gioviVisitor.visit(bodyCtx);
			};

			default -> throw new UnsupportedDeconstruction(map, iteratorMapCtx.value);
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

	private void forEach(Range<?> range, UnaryIteratorContext iCtx) {
		forEach(range, (i) -> {
			deconstruct(i, iCtx);
			gioviVisitor.visit(bodyCtx);
		});
	}

	private void forEach(GioviList list, UnaryIteratorContext iCtx) {
		forEach(list, (i) -> {
			deconstruct(i, iCtx);
			gioviVisitor.visit(bodyCtx);
		});
	}

	private void forEach(GioviMap map, UnaryIteratorContext iCtx) {
		forEach(map, (k, _) -> {
			deconstruct(k, iCtx);
			gioviVisitor.visit(bodyCtx);
		});
	}

	//////////////////////////////////////////////////////////////////////////////// Deconstruct

	private void deconstruct(Var<?> i, UnaryIteratorContext unaryIterator) {
		getCurrentScope().declare(
			unaryIterator.identifier().getText(),
			new VarD<>(i.copy().unwrap())
		);
	}

	private void deconstruct(Var<?> i, IteratorNestedProductContext iCtx) {
		deconstruct(i, Optional.ofNullable(iCtx.whole), iCtx.iteratorProductFields());
	}

	private void deconstruct(
		Var<?> i,
		Optional<UnaryIteratorContext> wholeCtx,
		IteratorProductFieldsContext iCtx
	) {
		wholeCtx.ifPresent(
			wCtx -> getCurrentScope().declare(
				wCtx.identifier().getText(),
				new VarD<>(i.copy().unwrap())
			)
		);
		switch (iCtx) {
			case IteratorProductPositionalContext pCtx -> {
				switch (i.unwrap()) {
					case GioviStruct struct
						-> deconstructPositional(struct.value(), pCtx.nestedIterator());
					case GioviRecord record
						-> deconstructPositional(record.value(), pCtx.nestedIterator());
					case GioviTuple tuple
						-> deconstructPositional(tuple.value(), pCtx.nestedIterator());
					default
						-> throw new UnsupportedDeconstruction(i.unwrap(), pCtx.nestedIterator());
				}
			}
			case IteratorProductNamedContext nCtx -> {
				switch (i.unwrap()) {
					case GioviStruct struct
						-> deconstructNamed(struct.value(), nCtx.nestedIterator());
					case GioviRecord record
						-> deconstructNamed(record.value(), nCtx.nestedIterator());
					default
						-> throw new UnsupportedDeconstruction(i.unwrap(), nCtx.nestedIterator());
				}
			}
			default -> throw new UnsupportedDeconstruction(i.unwrap(), iCtx);
		}
	}

	private void deconstructNamed(
		Record record,
		List<NestedIteratorContext> list
	) {
		for (var iteratorCtx : list) {
			switch (iteratorCtx) {

				case IteratorNestedPlaceholderContext _ -> {
					// do nothing
				}

				case IteratorNestedUnaryContext iCtx -> {
					String fieldIdentifier = iCtx.unaryIterator().identifier().getText();
					var field = record.getField(fieldIdentifier)
						.orElseThrow(
							() -> new UnsupportedDeconstruction(
								record,
								iCtx.unaryIterator(),
								new NoSuchField(fieldIdentifier)
							)
						);
					deconstruct(field, iCtx.unaryIterator());
				}

				case IteratorNestedProductContext iCtx -> {
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
								iCtx.unaryIterator(),
								new NoSuchField(fieldIdentifier)
							)
						);
					deconstruct(field, iCtx);
				}

				default -> throw new UnsupportedDeconstruction(record, iteratorCtx);
			}
		}
	}

	private void deconstructPositional(
		Tuple tuple,
		List<NestedIteratorContext> iterators
	) {
		for (int i = 0; i < iterators.size(); i++) {
			final int j = i;
			var field = tuple.getField(i)
				.orElseThrow(
					() -> new UnsupportedDeconstruction(
						tuple,
						new NoSuchField(j)
					)
				);
			var iteratorCtx = iterators.get(i);
			switch (iteratorCtx) {
				case IteratorNestedPlaceholderContext _ -> { /* do nothing */ }
				case IteratorNestedUnaryContext iCtx -> deconstruct(field, iCtx.unaryIterator());
				case IteratorNestedProductContext iCtx -> deconstruct(field, iCtx);
				default -> throw new UnsupportedDeconstruction(tuple, iterators);
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
