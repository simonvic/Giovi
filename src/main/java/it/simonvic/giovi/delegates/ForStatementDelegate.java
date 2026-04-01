package it.simonvic.giovi.delegates;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import giovi.GioviParserVisitResult;
import giovi.Range;
import giovi.lang.*;
import it.simonvic.giovi.*;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NoSuchField;

public class ForStatementDelegate extends GioviVisitorDelegate {

	public ForStatementDelegate(GioviVisitor gioviVisitor) {
		super(gioviVisitor);
	}

	// TODO: check if necessary to new VarD when deconstructing
	// TODO: check when declaring if it should be copied (by value) (e.g. iterating over a tuple)
	// TODO: for r Real in [1 2 3] do {} declare r first, then widen the entries

	@Override
	public GioviParserVisitResult visitForStatement(ForStatementContext ctx) {
		var iterator = ctx.iterator();
		var bodyCtx = ctx.statementOrBlock();
		var iterated = gioviVisitor.visit(ctx.iterable());
		// TODO: figure out a better way to handle vars
		if (iterated instanceof Var<?> v) {
			iterated = v.unwrap();
		}
		switch (iterated) {

			case Range<?> range -> {
				var expanded = range.expandAsGioviList(); // TODO: workaround for range iteration
				switch (iterator) {

					case IteratorPlaceholderContext _ -> iterate(expanded, bodyCtx, _ -> {});

					case IteratorUnaryContext entryCtx -> iterate(expanded, bodyCtx, entry -> {
						String identifier = entryCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(
							identifier,
							new VarD<>(entry.unwrap().copy())
						);
					});

					default -> throw new GioviException(
						"Can't iterate over " + range + " with iterator " + iterator.getText()
					);
				}
			}

			case GioviList list -> {
				switch (iterator) {

					case IteratorPlaceholderContext _ -> iterate(list, bodyCtx, _ -> {});

					case IteratorUnaryContext entryCtx -> iterate(list, bodyCtx, entry -> {
						String identifier = entryCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(
							identifier,
							new VarD<>(entry.unwrap().copy())
						);
					});

					case IteratorAggregateContext entryCtx -> iterate(list, bodyCtx, entry -> {
						deconstruct(entry.copy(), entryCtx);
					});

					default -> throw new GioviException(
						"Can't iterate over " + list + " with iterator " + iterator.getText()
					);
				}
			}

			case GioviMap map -> {
				switch (iterator) {

					case IteratorPlaceholderContext _ -> iterate(map, bodyCtx, (_, _) -> {});

					case IteratorUnaryContext keyCtx -> iterate(map, bodyCtx, (key, _) -> {
						String keyIdentifier = keyCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(
							keyIdentifier,
							new VarD<>(key.unwrap().copy())
						);
					});

					case IteratorMapContext c -> iterate(map, bodyCtx, c.key, c.value);

					default -> throw new GioviException(
						"Can't iterate over " + map + " with iterator " + iterator.getText()
					);
				}
			}

			default -> throw new GioviException(
				"Can't iterate over " + iterated + " with iterator " + iterator.getText()
			);
		}
		return null;
	}

	// TODO: should iterators be by value?

	/**
	 * Iterate over a map
	 * @param map
	 * @param bodyCtx
	 * @param keyIteratorCtx
	 * @param valueIteratorCtx
	 */
	private void iterate(
		GioviMap map,
		StatementOrBlockContext bodyCtx,
		IteratorContext keyIteratorCtx,
		IteratorContext valueIteratorCtx
	) {
		TriConsumer<StatementOrBlockContext, Var<?>, Var<?>> iterationBody;
		try {
			iterationBody = switch (keyIteratorCtx) {

				case IteratorPlaceholderContext _ -> switch (valueIteratorCtx) {

					case IteratorPlaceholderContext _ -> (body, _, _) -> gioviVisitor.visit(body);

					case IteratorUnaryContext valueCtx -> (body, _, v) -> {
						String valueIdentifier = valueCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(valueIdentifier, new VarD<>(v.unwrap().copy()));
						gioviVisitor.visit(body);
					};

					case IteratorAggregateContext valueCtx -> (body, _, v) -> {
						deconstruct(v.copy(), valueCtx);
						gioviVisitor.visit(body);
					};

					case IteratorMapContext valueCtx -> (body, k, v) -> {
						if (v.unwrap() instanceof GioviMap nestedMap) {
							iterate(nestedMap, body, valueCtx.key, valueCtx.value);
						}
					};

					default -> throw new GioviException("Unsupported map value");
				};

				case IteratorUnaryContext keyCtx -> {
					String keyIdentifier = keyCtx.unaryIterator().identifier().getText();

					yield switch (valueIteratorCtx) {
						case IteratorPlaceholderContext _ -> (body, k, _) -> {
							getCurrentScope().declare(keyIdentifier, new VarD<>(k.unwrap().copy()));
							gioviVisitor.visit(body);
						};

						case IteratorUnaryContext valueCtx -> (body, k, v) -> {
							getCurrentScope().declare(keyIdentifier, new VarD<>(k.unwrap().copy()));
							String valueIdentifier = valueCtx.unaryIterator().identifier()
								.getText();
							getCurrentScope().declare(
								valueIdentifier,
								new VarD<>(v.unwrap().copy())
							);
							gioviVisitor.visit(body);
						};

						case IteratorAggregateContext valueCtx -> (body, k, v) -> {
							getCurrentScope().declare(keyIdentifier, new VarD<>(k.unwrap().copy()));
							deconstruct(v.copy(), valueCtx);
							gioviVisitor.visit(body);
						};

						case IteratorMapContext valueCtx -> (body, k, v) -> {
							getCurrentScope().declare(keyIdentifier, new VarD<>(k.unwrap().copy()));
							if (v.unwrap() instanceof GioviMap nestedMap) {
								iterate(nestedMap, body, valueCtx.key, valueCtx.value);
							}
						};

						default -> throw new GioviException("Unsupported map value");
					};
				}

				case IteratorAggregateContext keyCtx -> switch (valueIteratorCtx) {

					case IteratorPlaceholderContext _ -> (body, k, _) -> {
						deconstruct(k.copy(), keyCtx);
						gioviVisitor.visit(body);
					};

					case IteratorUnaryContext valueCtx -> (body, k, v) -> {
						deconstruct(k.copy(), keyCtx);
						String valueIdentifier = valueCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(valueIdentifier, new VarD<>(v.unwrap().copy()));
						gioviVisitor.visit(body);
					};

					case IteratorAggregateContext valueCtx -> (body, k, v) -> {
						deconstruct(k.copy(), keyCtx);
						deconstruct(v.copy(), valueCtx);
						gioviVisitor.visit(body);
					};

					default -> throw new GioviException("Unsupported map value");
				};
				default -> throw new GioviException("Unsupported map key");
			};
		} catch (GioviException e) {
			throw new GioviException(e, "Can't iterate over map");
		}

		iterate(map, bodyCtx, iterationBody);
	}

	/**
	 * Deconstruct an aggregate value and declare the variables
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
	 * Deconstruct a tuple and declare the variables
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
	 * @param list
	 * @param statementOrBlockCtx
	 * @param preamble
	 */
	private void iterate(
		GioviList list,
		StatementOrBlockContext statementOrBlockCtx,
		Consumer<Var<?>> preamble
	) {
		for (var entry : list.value()) {
			enterLoopScope();
			preamble.accept(entry);
			gioviVisitor.visit(statementOrBlockCtx);
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
	 * @param statementOrBlockCtx
	 * @param preamble
	 */
	private void iterate(
		GioviMap map,
		StatementOrBlockContext statementOrBlockCtx,
		BiConsumer<Var<?>, Var<?>> preamble
	) {
		for (var entry : map.value().entrySet()) {
			enterLoopScope();
			preamble.accept(entry.getKey(), entry.getValue());
			gioviVisitor.visit(statementOrBlockCtx);
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
	 * @param statementOrBlockCtx
	 * @param loopBody
	 */
	private void iterate(
		GioviMap map,
		StatementOrBlockContext statementOrBlockCtx,
		TriConsumer<StatementOrBlockContext, Var<?>, Var<?>> loopBody
	) {
		for (var entry : map.value().entrySet()) {
			enterLoopScope();
			loopBody.accept(statementOrBlockCtx, entry.getKey(), entry.getValue());
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
