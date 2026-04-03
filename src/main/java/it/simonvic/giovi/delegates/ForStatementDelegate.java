package it.simonvic.giovi.delegates;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.antlr.v4.runtime.ParserRuleContext;

import giovi.GioviParserVisitResult;
import giovi.Range;
import giovi.lang.*;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.GioviVisitorDelegate;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NoSuchField;

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

	private static GioviException unsupportedIterator(
		ParserRuleContext iteratorCtx,
		GioviParserVisitResult value
		// TODO: add cause
	) {
		String iteratedText = (value instanceof GioviValue<?> v)
			? v.getGioviType().toString()
			: value.toString();
		return new GioviException(
			"Can't interate with " + iteratorCtx.getText() + " on " + iteratedText
		);
	}

	private static GioviException unsupportedDeconstruction(
		GioviParserVisitResult value,
		ParserRuleContext iteratorCtx
	) {
		return new GioviException("Can't deconstruct " + value + " into " + iteratorCtx.getText());
	}

	private static GioviException unsupportedDeconstruction(
		GioviParserVisitResult value,
		List<NestedIteratorContext> nestedIterators
	) {
		return new GioviException("Can't deconstruct " + value + " into " + nestedIterators);
	}

	private static GioviException unsupportedDeconstruction(
		GioviParserVisitResult value,
		ParserRuleContext iteratorCtx,
		Exception cause
	) {
		return new GioviException(
			cause, "Can't deconstruct " + value + " into " + iteratorCtx.getText()
		);
	}


	public GioviParserVisitResult doVisit() {

		switch (iteratorCtx) {

			case IteratorPlaceholderContext iCtx -> {
				switch (iterated) {
					case Range<?> range -> forEach(range);
					case GioviList list -> forEach(list);
					case GioviMap map -> forEach(map);
					default -> throw unsupportedIterator(iCtx, iterated);
				}
			}

			case IteratorUnaryContext iCtx -> {
				switch (iterated) {
					case Range<?> range -> forEach(range, iCtx.unaryIterator());
					case GioviList list -> forEach(list, iCtx.unaryIterator());
					case GioviMap map -> forEach(map, iCtx.unaryIterator());
					default -> throw unsupportedIterator(iCtx, iterated);
				}
			}

			case IteratorProductContext iCtx when iterated instanceof GioviList list
				-> forEach(list, iCtx);

			case IteratorMapContext iCtx when iterated instanceof GioviMap map
				-> forEach(map, iCtx.key, iCtx.value);


			case IteratorNestedContext nestedCtx -> {

				// NOTE: top level nested iterators are treated the same
				switch (nestedCtx.nestedIterator()) {

					case IteratorNestedPlaceholderContext _ -> {
						switch (iterated) {
							case Range<?> range -> forEach(range);
							case GioviList list -> forEach(list);
							case GioviMap map -> forEach(map);
							default -> throw unsupportedIterator(nestedCtx, iterated);
						}
					}

					case IteratorNestedUnaryContext iCtx -> {
						switch (iterated) {
							case Range<?> range -> forEach(range, iCtx.unaryIterator());
							case GioviList list -> forEach(list, iCtx.unaryIterator());
							case GioviMap map -> forEach(map, iCtx.unaryIterator());
							default -> throw unsupportedIterator(nestedCtx, iterated);
						}
					}

					case IteratorNestedListContext iCtx when iterated instanceof GioviList list
						-> forEach(list, iCtx.nestedIterator());

					case IteratorNestedProductContext iCtx when iterated instanceof GioviList list
						-> forEach(list, iCtx);

					case IteratorNestedMapContext iCtx when iterated instanceof GioviMap map
						-> forEach(map, iCtx.key, iCtx.value);

					default -> throw unsupportedIterator(nestedCtx, iterated);
				}
			}

			default -> throw unsupportedIterator(iteratorCtx, iterated);

		}
		return null;
	}

	// TODO: should iterators be by value?
	// TODO: for r Real in [1 2 3] do {} declare r first, then widen the entries

	private void forEach(GioviList list, NestedIteratorContext nestedIterator) {
		Consumer<Var<?>> iterationStrategy = switch (nestedIterator) {

			case IteratorNestedPlaceholderContext iCtx -> (_) -> {
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
					forEach(nestedMap, vCtx.key, vCtx.value);
				} else {
					throw unsupportedIterator(vCtx, v.unwrap());
				}
			};

			case IteratorNestedListContext iCtx -> (v) -> {
				if (v.unwrap() instanceof GioviList nestedList) {
					forEach(nestedList, iCtx.nestedIterator());
				} else {
					throw unsupportedIterator(iCtx.nestedIterator(), v.unwrap());
				}
			};

			default -> throw unsupportedIterator(nestedIterator, iterated);

		};

		forEach(list, iterationStrategy);
	}

	private void forEach(
		GioviMap map,
		NestedIteratorContext keyIteratorCtx,
		NestedIteratorContext valueIteratorCtx
	) {

		Consumer<Var<?>> keyIterationStrategy = switch (keyIteratorCtx) {

			case IteratorNestedPlaceholderContext _ -> (_) -> {
				// do nothing
			};

			case IteratorNestedUnaryContext kCtx -> (k) -> {
				deconstruct(k, kCtx.unaryIterator());
			};

			case IteratorNestedProductContext kCtx -> (k) -> {
				deconstruct(k, kCtx);
			};

			default -> throw unsupportedDeconstruction(map, keyIteratorCtx);
		};

		Consumer<Var<?>> valueIterationStrategy = switch (valueIteratorCtx) {
			case IteratorNestedPlaceholderContext _ -> (_) -> {
				gioviVisitor.visit(bodyCtx);
			};
			case IteratorNestedUnaryContext vCtx -> (v) -> {
				deconstruct(v, vCtx.unaryIterator());
				gioviVisitor.visit(bodyCtx);
			};
			case IteratorNestedMapContext vCtx -> (v) -> {
				if (v.unwrap() instanceof GioviMap nestedMap) {
					forEach(nestedMap, vCtx.key, vCtx.value);
				} else {
					throw unsupportedIterator(vCtx, v.unwrap());
				}
			};

			case IteratorNestedProductContext vCtx -> (v) -> {
				deconstruct(v, vCtx);
				gioviVisitor.visit(bodyCtx);
			};

			default -> throw unsupportedDeconstruction(map, valueIteratorCtx);
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

	private void forEach(GioviList list, IteratorProductContext iCtx) {
		forEach(list, (i) -> {
			deconstruct(i, iCtx);
			gioviVisitor.visit(bodyCtx);
		});
	}


	//////////////////////////////////////////////////////////////////////////////// Nested

	private void forEach(GioviList list, IteratorNestedProductContext nestedIterator) {
		throw new UnsupportedOperationException("Unimplemented method 'forEach'");
	}

	//////////////////////////////////////////////////////////////////////////////// Deconstruct

	private void deconstruct(Var<?> i, UnaryIteratorContext unaryIterator) {
		getCurrentScope().declare(
			unaryIterator.identifier().getText(),
			new VarD<>(i.copy().unwrap())
		);
	}

	/**
	 * Deconstruct a product type and declare the variables
	 * @param i
	 * @param iCtx
	 */
	private void deconstruct(Var<?> i, IteratorProductContext iCtx) {
		deconstruct(i, Optional.ofNullable(iCtx.whole), iCtx.nestedIterator());
	}

	private void deconstruct(Var<?> i, IteratorNestedProductContext iCtx) {
		deconstruct(i, Optional.ofNullable(iCtx.whole), iCtx.nestedIterator());
	}

	private void deconstruct(
		Var<?> i,
		Optional<UnaryIteratorContext> whole,
		List<NestedIteratorContext> nestedIterators
	) {
		whole.ifPresent(
			w -> getCurrentScope().declare(
				w.getText(),
				new VarD<>(i.copy().unwrap())
			)
		);
		switch (i.unwrap()) {
			case GioviStruct struct -> deconstruct(struct, nestedIterators);
			case GioviRecord tuple -> deconstruct(tuple, nestedIterators);
			case GioviTuple tuple -> deconstruct(tuple, nestedIterators);
			default -> throw unsupportedDeconstruction(i, nestedIterators);
		}
	}

	/**
	 * Deconstruct a tuple and declare the variables
	 * @param tuple
	 * @param iCtx
	 */
	private void deconstruct(GioviTuple tuple, List<NestedIteratorContext> fieldsIteratorsCtx) {
		throw new GioviException("Tuple deconstruct not implemented yet");
	}

	/**
	 * Deconstruct a record and declare the variables
	 * @param tuple
	 * @param iCtx
	 */
	private void deconstruct(GioviRecord tuple, List<NestedIteratorContext> fieldsIteratorsCtx) {
		throw new GioviException("Record deconstruct not implemented yet");
	}

	/**
	 * Deconstruct a struct and declare the variables
	 * @param struct
	 * @param iCtx
	 */
	private void deconstruct(GioviStruct struct, List<NestedIteratorContext> fieldsIteratorsCtx) {
		// TODO: iterate by identifiers or by field index?
		// TODO: should fields be by value?
		// TODO: handle concurrent modifications
		for (var fieldIteratorCtx : fieldsIteratorsCtx) {
			switch (fieldIteratorCtx) {

				case IteratorNestedPlaceholderContext _ -> {
					// do nothing
				}

				case IteratorNestedUnaryContext unaryCtx -> {
					String fieldIdentifier = unaryCtx.unaryIterator().identifier().getText();
					var field = struct.value()
						.getField(fieldIdentifier)
						.orElseThrow(
							() -> unsupportedDeconstruction(
								struct,
								unaryCtx,
								new NoSuchField(fieldIdentifier)
							)
						);
					getCurrentScope().declare(fieldIdentifier, field);
				}

				case IteratorNestedProductContext productCtx -> {
					if (productCtx.whole == null) {
						throw new GioviException(
							"Whole identifier is needed for named deconstruction"
						);
					}
					String fieldIdentifier = productCtx.whole.getText();
					var field = struct.value()
						.getField(fieldIdentifier)
						.orElseThrow(
							() -> unsupportedDeconstruction(
								struct,
								productCtx,
								new NoSuchField(fieldIdentifier)
							)
						);
					getCurrentScope().declare(fieldIdentifier, field);
					if (field.unwrap() instanceof GioviStruct structField) {
						deconstruct(structField, productCtx.nestedIterator());
					} else {
						throw unsupportedDeconstruction(field, productCtx);
					}
				}

				default -> throw new GioviException("Can't deconstruct");
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
