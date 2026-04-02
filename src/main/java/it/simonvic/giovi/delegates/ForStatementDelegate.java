package it.simonvic.giovi.delegates;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

	public static GioviParserVisitResult doVisit(
		GioviVisitor gioviVisitor,
		ForStatementContext ctx
	) {
		return new ForStatementDelegate(gioviVisitor, ctx).doVisit();
	}

	private final ForStatementContext ctx;
	private final StatementOrBlockContext bodyCtx;
	private final IteratorContext iteratorCtx;

	public ForStatementDelegate(GioviVisitor gioviVisitor, ForStatementContext ctx) {
		super(gioviVisitor);
		this.ctx = ctx;
		this.bodyCtx = ctx.statementOrBlock();
		this.iteratorCtx = ctx.iterator();
	}

	// TODO: check if necessary to new VarD when deconstructing
	// TODO: check when declaring if it should be copied (by value) (e.g. iterating over a tuple)
	// TODO: for r Real in [1 2 3] do {} declare r first, then widen the entries

	public GioviParserVisitResult doVisit() {
		var iterated = gioviVisitor.visit(ctx.iterable());
		// TODO: figure out a better way to handle vars
		if (iterated instanceof Var<?> v) {
			iterated = v.unwrap();
		}
		switch (iterated) {

			case Range<?> range -> {
				Consumer<Var<?>> loopBody = switch (iteratorCtx) {

					case IteratorPlaceholderContext _ -> (_) -> {
						gioviVisitor.visit(bodyCtx);
					};

					case IteratorUnaryContext iCtx -> (entry) -> {
						getCurrentScope().declare(
							iCtx.unaryIterator().identifier().getText(),
							new VarD<>(entry.unwrap().copy())
						);
						gioviVisitor.visit(bodyCtx);
					};

					default -> throw new GioviException(
						"Can't iterate over " + range + " with iterator " + iteratorCtx.getText()
					);
				};
				forEach(range, loopBody);
			}

			case GioviList list -> {
				Consumer<Var<?>> loopBody = switch (iteratorCtx) {

					case IteratorPlaceholderContext _ -> (_) -> {
						gioviVisitor.visit(bodyCtx);
					};

					case IteratorUnaryContext iCtx -> (entry) -> {
						getCurrentScope().declare(
							iCtx.unaryIterator().identifier().getText(),
							new VarD<>(entry.unwrap().copy())
						);
						gioviVisitor.visit(bodyCtx);
					};

					case IteratorAggregateContext iCtx -> (entry) -> {
						deconstruct(entry.copy(), iCtx);
						gioviVisitor.visit(bodyCtx);
					};

					default -> throw new GioviException(
						"Can't iterate over " + list + " with iterator " + iteratorCtx.getText()
					);
				};
				forEach(list, loopBody);
			}

			case GioviMap map -> {
				BiConsumer<Var<?>, Var<?>> loopBody = switch (iteratorCtx) {

					case IteratorPlaceholderContext _ -> (_, _) -> {
						gioviVisitor.visit(bodyCtx);
					};

					case IteratorUnaryContext iCtx -> (key, _) -> {
						getCurrentScope().declare(
							iCtx.unaryIterator().identifier().getText(),
							new VarD<>(key.unwrap().copy())
						);
						gioviVisitor.visit(bodyCtx);
					};

					case IteratorMapContext iCtx -> loopBodyFor(map, iCtx);

					default -> throw new GioviException(
						"Can't iterate over " + map + " with iterator " + iteratorCtx.getText()
					);
				};
				forEach(map, loopBody);
			}

			default -> throw new GioviException(
				"Can't iterate over " + iterated + " with iterator " + iteratorCtx.getText()
			);
		}
		return null;
	}

	// TODO: should iterators be by value?

	/**
	 * Iterate over a map
	 * @param map
	 * @param bodyCtx
	 * @param iteratorCtx
	 */
	private BiConsumer<Var<?>, Var<?>> loopBodyFor(
		GioviMap map,
		IteratorMapContext iteratorCtx
	) {
		var keyIteratorCtx = iteratorCtx.key;
		var valueIteratorCtx = iteratorCtx.value;
		return switch (keyIteratorCtx) {

			case IteratorPlaceholderContext _ -> switch (valueIteratorCtx) {

				case IteratorPlaceholderContext _ -> (_, _) -> gioviVisitor.visit(bodyCtx);

				case IteratorUnaryContext vCtx -> (_, v) -> {
					getCurrentScope().declare(
						vCtx.unaryIterator().identifier().getText(),
						new VarD<>(v.unwrap().copy())
					);
					gioviVisitor.visit(bodyCtx);
				};

				case IteratorAggregateContext vCtx -> (_, v) -> {
					deconstruct(v.copy(), vCtx);
					gioviVisitor.visit(bodyCtx);
				};

				case IteratorMapContext vCtx -> (k, v) -> {
					if (v.unwrap() instanceof GioviMap nestedMap) {
						forEach(nestedMap, loopBodyFor(nestedMap, vCtx));
					}
				};

				default -> throw new GioviException("Unsupported map value");
			};

			case IteratorUnaryContext kCtx -> {
				String keyIdentifier = kCtx.unaryIterator().identifier().getText();

				yield switch (valueIteratorCtx) {
					case IteratorPlaceholderContext _ -> (k, _) -> {
						getCurrentScope().declare(
							keyIdentifier,
							new VarD<>(k.unwrap().copy())
						);
						gioviVisitor.visit(bodyCtx);
					};

					case IteratorUnaryContext vCtx -> (k, v) -> {
						getCurrentScope().declare(
							keyIdentifier,
							new VarD<>(k.unwrap().copy())
						);
						getCurrentScope().declare(
							vCtx.unaryIterator().identifier().getText(),
							new VarD<>(v.unwrap().copy())
						);
						gioviVisitor.visit(bodyCtx);
					};

					case IteratorAggregateContext vCtx -> (k, v) -> {
						getCurrentScope().declare(
							keyIdentifier,
							new VarD<>(k.unwrap().copy())
						);
						deconstruct(v.copy(), vCtx);
						gioviVisitor.visit(bodyCtx);
					};

					case IteratorMapContext vCtx -> (k, v) -> {
						getCurrentScope().declare(
							keyIdentifier,
							new VarD<>(k.unwrap().copy())
						);
						if (v.unwrap() instanceof GioviMap nestedMap) {
							forEach(nestedMap, loopBodyFor(nestedMap, vCtx));
						}
					};

					default -> throw new GioviException("Unsupported map value");
				};
			}

			case IteratorAggregateContext kCtx -> switch (valueIteratorCtx) {

				case IteratorPlaceholderContext _ -> (k, _) -> {
					deconstruct(k.copy(), kCtx);
					gioviVisitor.visit(bodyCtx);
				};

				case IteratorUnaryContext vCtx -> (k, v) -> {
					deconstruct(k.copy(), kCtx);
					getCurrentScope().declare(
						vCtx.unaryIterator().identifier().getText(),
						new VarD<>(v.unwrap().copy())
					);
					gioviVisitor.visit(bodyCtx);
				};

				case IteratorAggregateContext vCtx -> (k, v) -> {
					deconstruct(k.copy(), kCtx);
					deconstruct(v.copy(), vCtx);
					gioviVisitor.visit(bodyCtx);
				};

				default -> throw new GioviException("Unsupported map value");
			};
			default -> throw new GioviException("Unsupported map key");
		};
	}

	/**
	 * Deconstruct a product type and declare the variables
	 * @param aggregate
	 * @param aggregateCtx
	 */
	private void deconstruct(Var<?> aggregate, IteratorAggregateContext aggregateCtx) {
		if (aggregateCtx.whole != null) {
			getCurrentScope().declare(
				aggregateCtx.whole.getText(),
				new VarD<>(aggregate.unwrap())
			);
		}
		switch (aggregate.unwrap()) {
			case GioviStruct struct -> deconstruct(struct, aggregateCtx);
			case GioviRecord tuple -> deconstruct(tuple, aggregateCtx);
			case GioviTuple tuple -> deconstruct(tuple, aggregateCtx);
			default -> throw new GioviException("Can't deconstruct " + aggregate);
		}
	}

	/**
	 * Deconstruct a tuple and declare the variables
	 * @param tuple
	 * @param aggregateCtx
	 */
	private void deconstruct(GioviTuple tuple, IteratorAggregateContext aggregateCtx) {
		throw new GioviException("Tuple deconstruct not implemented yet");
	}

	/**
	 * Deconstruct a record and declare the variables
	 * @param tuple
	 * @param aggregateCtx
	 */
	private void deconstruct(GioviRecord tuple, IteratorAggregateContext aggregateCtx) {
		throw new GioviException("Record deconstruct not implemented yet");
	}

	/**
	 * Deconstruct a struct and declare the variables
	 * @param struct
	 * @param aggregateCtx
	 */
	private void deconstruct(GioviStruct struct, IteratorAggregateContext aggregateCtx) {
		// TODO: iterate by identifiers or by field index?
		// TODO: should fields be by value?
		// TODO: handle concurrent modifications
		for (var fieldIteratorCtx : aggregateCtx.iterator()) {
			switch (fieldIteratorCtx) {

				case IteratorPlaceholderContext _ -> {
					// do nothing
				}

				case IteratorUnaryContext uCtx -> {
					String fieldIdentifier = uCtx.unaryIterator().identifier().getText();
					var field = struct.value().getField(fieldIdentifier)
						.orElseThrow(
							() -> new GioviException(
								"Can't deconstruct",
								new NoSuchField(fieldIdentifier)
							)
						);
					getCurrentScope().declare(fieldIdentifier, field);
				}

				case IteratorAggregateContext aCtx -> {
					if (aCtx.whole == null) {
						throw new GioviException(
							"Can't deconstruct nested aggregate without a whole identifier"
						);
					}
					String fieldIdentifier = aCtx.whole.getText();
					var field = struct.value().getField(fieldIdentifier)
						.orElseThrow(
							() -> new GioviException(
								"Can't deconstruct",
								new NoSuchField(fieldIdentifier)
							)
						);
					deconstruct(field, aCtx);
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
