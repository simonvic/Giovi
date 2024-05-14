package it.simonvic.giovi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import giovi.*;
import giovi.lang.*;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.delegates.SwitchStatementDelegate;
import it.simonvic.giovi.exceptions.*;
import it.simonvic.giovi.lang.*;
import it.simonvic.giovi.util.GioviTypes;
import it.simonvic.giovi.util.TextProcessor;
import it.simonvic.giovi.util.VisitorUtils;

public class GioviVisitor extends GioviParserBaseVisitor<GioviParserVisitResult> {

	protected final Scanner scanner = new Scanner(System.in);

	private final Scope globalScope = new Scope();
	private Scope currentScope = globalScope;

	public Scope getCurrentScope() {
		return currentScope;
	}

	private void setCurrentScope(Scope scope) {
		currentScope = scope;
	}

	private void enterScope() {
		setCurrentScope(new Scope(getCurrentScope()));
	}

	private void enterFunctionScope() {
		setCurrentScope(new FunctionScope(getCurrentScope()));
	}

	private void enterLoopScope() {
		setCurrentScope(new LoopScope(getCurrentScope()));
	}

	private void exitScope() {
		setCurrentScope(getCurrentScope().getParent());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// STATEMENTS

	@Override
	public GioviParserVisitResult visitIfStatement(IfStatementContext ctx) {
		if (visit(ctx.expression()).asNativeBool()) {
			enterScope();
			visit(ctx.ifBody);
			exitScope();
		} else if (ctx.elseBody != null) {
			enterScope();
			visit(ctx.elseBody);
			exitScope();
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitBlock(BlockContext ctx) {
		for (StatementContext statementContext : ctx.statement()) {
			visit(statementContext);
			Scope s = getCurrentScope();
			if (s.shouldBreak() || s.shouldReturn() || s.shouldSkip()) {
				break;
			}
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitStatementOrBlock(StatementOrBlockContext ctx) {
		return ctx.block() != null
			? visit(ctx.block())
			: visit(ctx.statement());
	}

	@Override
	public GioviParserVisitResult visitSwitchStatement(SwitchStatementContext ctx) {
		var subject = visit(ctx.expression());
		var matchedLabel = SwitchStatementDelegate.evaluate(this, ctx.switchBlock(), subject);
		if (matchedLabel.isPresent()) {
			enterScope();
			var matchedLabelCtx = matchedLabel.get();
			if (matchedLabelCtx.caseMatch() instanceof CaseMatchTypeCheckContext typeCheckCtx
				&& typeCheckCtx.identifier() != null) {
				getCurrentScope().declare(
					typeCheckCtx.identifier().getText(),
					// TODO: clafiry requirements. Should a type check a type = type or a widening check?
					// A widening check could be useful for struct/tuple matching and destructing
					subject.as(visit(typeCheckCtx.type())).asGioviValue());
			}
			visit(matchedLabelCtx.body);
			exitScope();
		} else if (ctx.elseBody != null) {
			enterScope();
			visit(ctx.elseBody);
			exitScope();
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitBreakStatement(BreakStatementContext ctx) {
		getCurrentScope().doBreak();
		return null;
	}

	@Override
	public GioviParserVisitResult visitSkipStatement(SkipStatementContext ctx) {
		getCurrentScope().doSkip();
		return null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Loops

	@Override
	public GioviParserVisitResult visitRepeatStatement(RepeatStatementContext ctx) {
		boolean infiniteRepeat = ctx.IF() == null;
		while (infiniteRepeat || visit(ctx.expression()).asNativeBool()) {
			enterLoopScope();
			visit(ctx.statementOrBlock());
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
		return null;
	}

	@Override
	public GioviParserVisitResult visitForStatement(ForStatementContext ctx) {
		var iterator = ctx.iterator();
		var iterated = visit(ctx.iterable());
		var bodyCtx = ctx.statementOrBlock();
		switch (iterated) {

			case Range<?> range -> {
				var expanded = range.expandAsGioviList(); // TODO: workaround for range iteration
				switch (iterator) {

					case IteratorPlaceholderContext _ -> iterate(expanded, bodyCtx, _ -> {});

					case IteratorUnaryContext entryCtx -> iterate(expanded, bodyCtx, entry -> {
						getCurrentScope().declare(
							entryCtx.unaryIterator().identifier().getText(),
							entry.copy());
					});

					default -> throw new GioviException(
						"Can't iterate over " + range + " with iterator " + iterator.getText());
				}
			}

			case GioviList list -> {
				switch (iterator) {

					case IteratorPlaceholderContext _ -> iterate(list, bodyCtx, _ -> {});

					case IteratorUnaryContext entryCtx -> iterate(list, bodyCtx, entry -> {
						getCurrentScope().declare(
							entryCtx.unaryIterator().identifier().getText(),
							entry.copy());
					});

					case IteratorAggregateContext entryCtx -> iterate(list, bodyCtx, entry -> {
						deconstruct(entry.copy(), entryCtx);
					});

					default -> throw new GioviException(
						"Can't iterate over " + list + " with iterator " + iterator.getText());
				}
			}

			case GioviMap map -> {
				switch (iterator) {

					case IteratorPlaceholderContext _ -> iterate(map, bodyCtx, (_, _) -> {});

					case IteratorUnaryContext keyCtx -> iterate(map, bodyCtx, (key, _) -> {
						getCurrentScope().declare(
							keyCtx.unaryIterator().identifier().getText(),
							key.copy());
					});

					case IteratorMapContext c -> iterate(map, bodyCtx, c.key, c.value);

					default -> throw new GioviException(
						"Can't iterate over " + map + " with iterator " + iterator.getText());
				}
			}

			default -> throw new GioviException(
				"Can't iterate over " + iterated + " with iterator " + iterator.getText());
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
		IteratorContext valueIteratorCtx) {
		TriConsumer<StatementOrBlockContext, GioviValue<?>, GioviValue<?>> iterationBody = switch (keyIteratorCtx) {

			case IteratorPlaceholderContext _ -> switch ( valueIteratorCtx) {

				case IteratorPlaceholderContext _ -> ( body, _, _) -> visit(body);

				case IteratorUnaryContext valueCtx -> (body, _, v) -> {
					String valueIdentifier = valueCtx.unaryIterator().identifier().getText();
					getCurrentScope().declare(valueIdentifier, v.copy());
					visit(body);
				};

				case IteratorAggregateContext valueCtx -> (body, _, v) -> {
					deconstruct(v.copy(), valueCtx);
					visit(body);
				};

				default -> throw new GioviException("Can't iterate over unsupported map value");
			};

			case IteratorUnaryContext keyCtx -> {
				String keyIdentifier = keyCtx.unaryIterator().identifier().getText();

				yield switch (valueIteratorCtx) {

					case IteratorPlaceholderContext _ -> ( body, k, _) -> {
						getCurrentScope().declare(keyIdentifier, k.copy());
						visit(body);
					};

					case IteratorUnaryContext valueCtx -> (body, k, v) -> {
						getCurrentScope().declare(keyIdentifier, k.copy());
						String valueIdentifier = valueCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(valueIdentifier, v.copy());
						visit(body);
					};

					case IteratorAggregateContext valueCtx -> (body, k, v) -> {
						getCurrentScope().declare(keyIdentifier, k.copy());
						deconstruct(v.copy(), valueCtx);
						visit(body);
					};

					case IteratorMapContext valueCtx -> (body, k, v) -> {
						getCurrentScope().declare(keyIdentifier, k.copy());
						if (v instanceof GioviMap nestedMap) {
							iterate(nestedMap, body, valueCtx.key, valueCtx.value);
						}
					};

					default -> throw new GioviException("Can't iterate over unsupported map value");
				};
			}

			case IteratorAggregateContext keyCtx -> switch (valueIteratorCtx) {

				case IteratorPlaceholderContext _ -> ( body, k, _) -> {
					deconstruct(k.copy(), keyCtx);
					visit(body);
				};

				case IteratorUnaryContext valueCtx -> (body, k, v) -> {
					deconstruct(k.copy(), keyCtx);
					String valueIdentifier = valueCtx.unaryIterator().identifier().getText();
					getCurrentScope().declare(valueIdentifier, v.copy());
					visit(body);
				};

				case IteratorAggregateContext valueCtx -> (body, k, v) -> {
					deconstruct(k.copy(), keyCtx);
					deconstruct(v.copy(), valueCtx);
					visit(body);
				};

				default -> throw new GioviException("Can't iterate over unsupported map value");
			};
			default -> throw new GioviException("Can't iterate over unsupported map key");
		};

		iterate(map, bodyCtx, iterationBody);
	}

	/**
	 * Deconstruct an aggregate value and declare the variables
	 * @param aggregate
	 * @param aggregateCtx
	 */
	private void deconstruct(GioviValue<?> aggregate, IteratorAggregateContext aggregateCtx) {
		if (aggregateCtx.whole != null) {
			getCurrentScope().declare(aggregateCtx.whole.identifier().getText(), aggregate);
		}
		switch (aggregate) {
			case GioviTuple tuple -> deconstruct(tuple, aggregateCtx);
			case GioviStruct struct -> deconstruct(struct, aggregateCtx);
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
	 * Deconstruct a struct and declare the variables
	 * @param struct
	 * @param aggregateCtx
	 */
	private void deconstruct(GioviStruct struct, IteratorAggregateContext aggregateCtx) {
		// TODO: iterate by identifiers or by field index?
		// TODO: should fields be by value?
		for (var fieldIteratorCtx : aggregateCtx.iterator()) {
			switch (fieldIteratorCtx) {

				case IteratorPlaceholderContext _ -> {
					// do nothing
				}

				case IteratorUnaryContext uCtx -> {
					try {
						String fieldIdentifier = uCtx.unaryIterator().identifier().getText();
						var field = struct.getField(fieldIdentifier);
						getCurrentScope().declare(fieldIdentifier, field);
					} catch (UnknownTupleField e) {
						throw new GioviException(e, "Can't deconstruct");
					}
				}

				case IteratorAggregateContext aCtx -> {
					if (aCtx.whole == null) {
						throw new GioviException(
							"Can't deconstruct nested aggregate without a whole identifier");
					}
					try {
						String fieldIdentifier = aCtx.whole.identifier().getText();
						var field = struct.getField(fieldIdentifier);
						deconstruct(field, aCtx);
					} catch (UnknownTupleField e) {
						throw new GioviException(e, "Can't deconstruct");
					}
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
		Consumer<GioviValue<?>> preamble) {
		for (var entry : list.unbox()) {
			enterLoopScope();
			preamble.accept(entry);
			visit(statementOrBlockCtx);
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
		BiConsumer<GioviValue<?>, GioviValue<?>> preamble) {
		for (var entry : map.unbox().entrySet()) {
			enterLoopScope();
			preamble.accept(entry.getKey(), entry.getValue());
			visit(statementOrBlockCtx);
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
		TriConsumer<StatementOrBlockContext, GioviValue<?>, GioviValue<?>> loopBody) {
		for (var entry : map.unbox().entrySet()) {
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

	//////////////////////////////////////////////////////////////////////////////////////////////// REDIRECTIONS

	@Override
	public GioviParserVisitResult visitRedirectionContent(RedirectionContentContext ctx) {
		return ctx.expression() != null
			? visit(ctx.expression())
			: visit(ctx.primitiveContent());
	}

	@Override
	public GioviParserVisitResult visitRedirectionToVarDeclaration(
		RedirectionToVarDeclarationContext ctx) {
		var content = visit(ctx.redirectionContent());
		var variable = visit(ctx.varDeclaration());
		variable.redirection(content);
		return null;
	}

	@Override
	public GioviParserVisitResult visitVarDeclaration(VarDeclarationContext ctx) {
		var variable = spawnType(ctx.type(), ctx.defaultDefinition());
		getCurrentScope().declare(ctx.identifier().getText(), variable);
		return variable;
	}

	private GioviValue<?> spawnType(
		TypeContext typeCtx,
		DefaultDefinitionContext defaultDefinitionCtx) {
		return spawnType(visit(typeCtx).asGioviType(), defaultDefinitionCtx);
	}

	private GioviValue<?> spawnType(
		GioviType<?> type,
		DefaultDefinitionContext defaultDefinitionCtx) {
		var value = type.spawn();
		if (defaultDefinitionCtx != null) {
			// TODO: check if type instanceof GioviDefault/GioviPlaceholder/GioviVoid
			// we need to copy to ensure we don't pass an existing reference
			// (e.g. foobar Foobar default someMap@someKey) which might change
			// TODO: eventually the copy should be done within GioviValues,
			// where they know if it's necessary
			try {
				// TODO: widen here or inside the boxing method?
				// We would need to override boxing methods in composite types though
				var defaultValue = visit(defaultDefinitionCtx).asGioviValue().widenTo(type);
				value.boxDefault(defaultValue.copy());
				// TODO: boxing an empty list (which will have type Void) will fucks up everything
				value.box(defaultValue.copy());
			} catch (ClassCastException | NonWideneable ex) {
				throw new GioviException("Incompatible default value type");
			}
		}
		return value;
	}

	@Override
	public GioviParserVisitResult visitConstantDeclaration(ConstantDeclarationContext ctx) {
		var constant = visit(ctx.expression()).asGioviValue();
		getCurrentScope().declareConstant(ctx.constantIdentifier().getText(), constant);
		return constant;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToIdentifier(RedirectionToIdentifierContext ctx) {
		VisitorUtils.redirection(
			visit(ctx.redirectionContent()),
			GioviToken.of(ctx.redirectionOperator().getText()),
			visit(ctx.identifier()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToConstantIdentifier(
		RedirectionToConstantIdentifierContext ctx) {
		throw new GioviException("Can't redirect to a constant");
	}

	@Override
	public GioviParserVisitResult visitRedirectionToExpression(RedirectionToExpressionContext ctx) {
		VisitorUtils.redirection(
			visit(ctx.redirectionContent()),
			GioviToken.of(ctx.redirectionOperator().getText()),
			visit(ctx.expression()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToVarDeclarationRTL(
		RedirectionToVarDeclarationRTLContext ctx) {
		var content = visit(ctx.redirectionContent());
		var variable = visit(ctx.varDeclaration());
		variable.redirection(content);
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToIdentifierRTL(
		RedirectionToIdentifierRTLContext ctx) {
		VisitorUtils.redirection(
			visit(ctx.redirectionContent()),
			GioviToken.of(ctx.redirectionOperatorRTL().getText()),
			visit(ctx.identifier()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToExpressionRTL(
		RedirectionToExpressionRTLContext ctx) {
		VisitorUtils.redirection(
			visit(ctx.redirectionContent()),
			GioviToken.of(ctx.redirectionOperatorRTL().getText()),
			visit(ctx.expression()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToPrimitiveDestination(
		RedirectionToPrimitiveDestinationContext ctx) {
		VisitorUtils.redirectionToPrimitive(
			visit(ctx.redirectionContent()),
			GioviToken.of(ctx.redirectionOperator().getText()),
			ctx.primitiveDestination().getText());
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToPrimitiveDestinationRTL(
		RedirectionToPrimitiveDestinationRTLContext ctx) {
		VisitorUtils.redirectionToPrimitive(
			visit(ctx.redirectionContent()),
			GioviToken.of(ctx.redirectionOperatorRTL().getText()),
			ctx.primitiveDestination().getText());
		return null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Expressions

	@Override
	public GioviParserVisitResult visitExpressionParenthesized(ExpressionParenthesizedContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public GioviParserVisitResult visitExpressionRef(ExpressionRefContext ctx) {
		return new GioviFunction(visit(ctx.referenceable()).asNativeFunction());
	}

	@Override
	public GioviParserVisitResult visitReferenceable(ReferenceableContext ctx) {
		String identifier;
		if (ctx.identifier() != null) {
			identifier = ctx.identifier().getText();
			try {
				return getCurrentScope().getFunction(identifier);
			} catch (FunctionNotDeclared e) {
				throw new GioviException("Can't reference non-existing function " + identifier);
			}
		}

		FunctionSignature signature;
		var parameters = new LinkedHashMap<String, FunctionSignatureParam>();
		if (ctx.formalParameters() != null) {
			for (var formalParamCtx : ctx.formalParameters().formalParameter()) {
				var formalParameter = (FunctionSignatureParam) visit(formalParamCtx);
				parameters.put(formalParameter.identifier(), formalParameter);
			}
		}

		GioviType<?> returnType = ctx.returnType != null
			? visit(ctx.returnType).asGioviType()
			: new GioviTypeVoid();

		signature = new FunctionSignature("", parameters, returnType);
		return new Function(signature, ctx.body);
	}

	@Override
	public GioviParserVisitResult visitExpressionPrefixed(ExpressionPrefixedContext ctx) {
		return VisitorUtils.prefixed(
			GioviToken.of(ctx.prefix.getText()),
			visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitExpressionAdditive(ExpressionAdditiveContext ctx) {
		return VisitorUtils.additive(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionMultiplicative(
		ExpressionMultiplicativeContext ctx) {
		return VisitorUtils.multiplicative(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionAccess(ExpressionAccessContext ctx) {
		return visit(ctx.expression()).access(visit(ctx.accessKey()));
	}

	@Override
	public GioviParserVisitResult visitExpressionDot(ExpressionDotContext ctx) {
		var lhs = visit(ctx.expression());
		if (ctx.anyIdentifier() != null) {
			return lhs.dot(new FieldKey(ctx.anyIdentifier().getText()));
		}
		if (ctx.DEFAULT() != null) {
			return lhs.dot(new GioviDefault());
		}
		return null;
		// TODO: struct functions
	}

	@Override
	public GioviParserVisitResult visitExpressionPower(ExpressionPowerContext ctx) {
		return visit(ctx.expression(0)).power(visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionRelational(ExpressionRelationalContext ctx) {
		return VisitorUtils.relational(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionNot(ExpressionNotContext ctx) {
		return visit(ctx.expression()).not();
	}

	@Override
	public GioviParserVisitResult visitExpressionBool(ExpressionBoolContext ctx) {
		return VisitorUtils.bool(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			() -> visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionEquality(ExpressionEqualityContext ctx) {
		return VisitorUtils.equality(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionContainment(ExpressionContainmentContext ctx) {
		return VisitorUtils.containment(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionCast(ExpressionCastContext ctx) {
		return visit(ctx.expression()).as(visit(ctx.type()));
	}

	@Override
	public GioviParserVisitResult visitExpressionTypeCheck(ExpressionTypeCheckContext ctx) {
		var expression = visit(ctx.expression()).asGioviValue();
		var type = visit(ctx.type()).asGioviType();
		return new GioviBool(expression.getGioviType().equals(type));
	}

	@Override
	public GioviParserVisitResult visitAccess(AccessContext ctx) {
		return getCurrentScope()
			.getVariableValue(ctx.anyIdentifier().getText())
			.access(visit(ctx.accessKey()));
	}

	@Override
	public GioviParserVisitResult visitAccessKeySpecial(AccessKeySpecialContext ctx) {
		return AccessKey.of(ctx.key.getText());
	}

	@Override
	public GioviParserVisitResult visitAccessKeyLiteral(AccessKeyLiteralContext ctx) {
		return visit(ctx.literal());
	}

	@Override
	public GioviParserVisitResult visitAccessKeyParenthesized(AccessKeyParenthesizedContext ctx) {
		return ctx.accessKey() != null ? visit(ctx.accessKey()) : visit(ctx.expression());
	}

	@Override
	public GioviParserVisitResult visitAccessKeyRange(AccessKeyRangeContext ctx) {
		return visit(ctx.range());
	}

	@Override
	public GioviParserVisitResult visitIdentifier(IdentifierContext ctx) {
		return getCurrentScope().getVariableValue(ctx.getText());
	}

	@Override
	public GioviParserVisitResult visitConstantIdentifier(ConstantIdentifierContext ctx) {
		return getCurrentScope().getConstantValue(ctx.getText());
	}

	@Override
	public GioviParserVisitResult visitIterable(IterableContext ctx) {
		return ctx.range() != null
			? visit(ctx.range())
			: visit(ctx.expression());
	}

	@Override
	public GioviParserVisitResult visitRange(RangeContext ctx) {
		return Range.of(
			visit(ctx.start),
			visit(ctx.end),
			ctx.step == null ? null : visit(ctx.step));
	}

	// TODO: move type inference here instead of reiterating list/map

	@Override
	public GioviParserVisitResult visitListCreation(ListCreationContext ctx) {
		var entries = new ArrayList<GioviValue<?>>();
		for (var entryCtx : ctx.listEntries().listEntry()) {
			var entry = visit(entryCtx);
			switch (entry) {
				case Range<?> range -> entries.addAll(range.expandAsGioviValues());
				case GioviValue<?> v -> entries.add(v);
				default -> throw new GioviException(
					"Incompatible type for list creation: " + entry.getClass());
			}
		}
		var entryType = GioviTypes.inferGioviType(entries);
		return new GioviList(entryType, entries);
	}

	@Override
	public GioviParserVisitResult visitMapCreation(MapCreationContext ctx) {
		var map = new LinkedHashMap<GioviValue<?>, GioviValue<?>>();
		for (var entryCtx : ctx.mapEntries().mapEntry()) {
			map.put(visit(entryCtx.mapKey).asGioviValue(), visit(entryCtx.mapValue).asGioviValue());
		}
		var keyType = GioviTypes.inferGioviType(map.keySet());
		var elementType = GioviTypes.inferGioviType(map.values());
		return new GioviMap(keyType, elementType, map);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// FUNCTIONS

	@Override
	public GioviParserVisitResult visitFunctionDeclaration(FunctionDeclarationContext ctx) {
		FunctionSignature signature;

		String identifier = ctx.identifier().getText();

		var returnType = ctx.type() == null
			? new GioviTypeVoid()
			: visit(ctx.type()).asGioviType();

		if (ctx.formalParameters() == null) {
			signature = ctx.type() == null
				? new FunctionSignature(identifier)
				: new FunctionSignature(identifier, returnType);
		} else {
			var parameters = new LinkedHashMap<String, FunctionSignatureParam>();
			for (var formalParamCtx : ctx.formalParameters().formalParameter()) {
				var formalParameter = (FunctionSignatureParam) visit(formalParamCtx);
				parameters.put(formalParameter.identifier(), formalParameter);
			}
			signature = new FunctionSignature(identifier, parameters, returnType);
		}

		getCurrentScope().declare(new Function(signature, ctx.body));
		return signature;
	}

	@Override
	public GioviParserVisitResult visitFormalParameter(FormalParameterContext ctx) {
		String identifier = ctx.identifier().getText();
		var type = visit(ctx.type()).asGioviType();
		var param = spawnType(type, ctx.defaultDefinition());
		boolean isOptional = ctx.defaultDefinition() != null;
		return new FunctionSignatureParam(identifier, type, param, isOptional);
	}

	@Override
	public GioviParserVisitResult visitFunctionCall(FunctionCallContext ctx) {
		Function function;
		String identifier = ctx.identifier().getText();
		try {
			function = getCurrentScope().getFunction(identifier);
		} catch (FunctionNotDeclared e) {
			var reference = getCurrentScope().getVariableValue(identifier);
			if (reference instanceof GioviFunction gioviFunction) {
				function = gioviFunction.unbox();
			} else {
				throw new GioviException(e, "Can't call " + identifier);
			}
		}
		enterFunctionScope();
		setupFunctionParameters(ctx, function.signature());
		var returnValue = callFunction(function);
		exitScope();
		return returnValue;
	}

	/**
	 * Visit function call parameters and declare actual parameters
	 * @param ctx Function call context
	 * @param signature Function signature
	 */
	private void setupFunctionParameters(
		FunctionCallContext ctx,
		FunctionSignature signature) {
		var callParamsCtx = ctx.functionCallParameters();
		// NOTE: if parameters are either positional or named depends on the first callParameter
		// If no parameters have been specified, positional approach is used
		if (callParamsCtx == null) {
			setupFunctionParametersIteratively(List.of(), signature);
		} else if (callParamsCtx.functionCallParameter(0).identifier() == null) {
			setupFunctionParametersIteratively(callParamsCtx.functionCallParameter(), signature);
		} else {
			setupNamedFunctionParameters(callParamsCtx.functionCallParameter(), signature);
		}

	}

	/**
	 * Visit function call positional parameters and declare actual parameters
	 * @param actualParamCtxList Function call context
	 * @param signature Function signature
	 */
	private void setupFunctionParametersIteratively(
		List<FunctionCallParameterContext> actualParamCtxList,
		FunctionSignature signature) {
		var formalParamsArray = signature.getParametersList();
		int formalParamsCount = formalParamsArray.size();
		int actualParamsCount = actualParamCtxList.size();
		if (actualParamsCount > formalParamsCount) {
			throw new GioviException(
				"Expecting at most " + formalParamsCount + " but instead got " + actualParamsCount);
		}

		for (int i = 0; i < formalParamsCount; i++) {
			var formalParam = formalParamsArray.get(i);
			var formalParamIdentifier = formalParam.identifier();

			if (i >= actualParamsCount) {
				// no more actual params found, expecting remaining formal parameters to be optional
				for (int j = i; j < formalParamsCount; j++) {
					formalParam = formalParamsArray.get(j);
					formalParamIdentifier = formalParam.identifier();
					if (!formalParam.isOptional()) {
						throw new GioviException(
							"Required parameter not specified: " + formalParamIdentifier);
					}
					getCurrentScope().declare(
						formalParamIdentifier,
						formalParam.spawn());
				}
				break;
			}

			var actualParamExpression = visit(actualParamCtxList.get(i).expression());
			if (actualParamExpression instanceof GioviDefault) { // TODO: use GioviPlaceholder
				getCurrentScope().declare(
					formalParamIdentifier,
					formalParam.spawn());
				continue;
			}
			var actualParamValue = actualParamExpression.asGioviValue();
			try {
				getCurrentScope().declare(
					formalParamIdentifier,
					actualParamValue.widenTo(formalParam.type()));
			} catch (NonWideneable ex) {
				throw new GioviException(ex, "Wrong parameter type. Expecting " + formalParam.type()
					+ " but instead got " + actualParamValue.getGioviType());
			}
		}

	}

	/**
	 * Visit function call named parameters and declare actual parameters
	 * @param actualParamCtxList Function call context
	 * @param signature Function signature
	 */
	private void setupNamedFunctionParameters(
		List<FunctionCallParameterContext> actualParamCtxList,
		FunctionSignature signature) {

		// cache actual params mapped by their identifiers
		var actualParams = new LinkedHashMap<String, FunctionCallParameterContext>();
		for (var actualParamCtx : actualParamCtxList) {
			actualParams.put(actualParamCtx.identifier().getText(), actualParamCtx);
		}

		var undeclaredParameters = new LinkedHashMap<>(signature.parameters());
		actualParams.forEach((actualParamIdentifier, actualParamCtx) -> {
			if (!signature.parameters().containsKey(actualParamIdentifier)) {
				throw new GioviException(
					"Unknown parameter " + actualParamIdentifier + " in " + signature);
			}
			if (!undeclaredParameters.containsKey(actualParamIdentifier)) {
				throw new GioviException("Parameter " + actualParamIdentifier + " already defined");
			}
			var formalParam = signature.parameters().get(actualParamIdentifier);
			var formalParamIdentifier = formalParam.identifier();
			var actualParamExpression = visit(actualParams.get(formalParamIdentifier).expression());

			if (actualParamExpression instanceof GioviDefault) { // TODO: use GioviPlaceholder
				getCurrentScope().declare(formalParamIdentifier, formalParam.spawn());
			} else {
				var actualParamValue = actualParamExpression.asGioviValue();
				try {
					getCurrentScope().declare(
						formalParamIdentifier,
						actualParamValue.widenTo(formalParam.type()));
				} catch (NonWideneable ex) {
					throw new GioviException(ex, "Wrong parameter type. Expecting "
						+ formalParam.type() + " but instead got "
						+ actualParamValue.getGioviType());
				}
			}
			undeclaredParameters.remove(formalParamIdentifier);
		});

		// no more actual params found, expecting remaining formal parameters to be optional
		undeclaredParameters.forEach((formalParamIdentifier, formalParam) -> {
			if (!formalParam.isOptional()) {
				throw new GioviException(
					"Required parameter not specified: " + formalParamIdentifier);
			}
			getCurrentScope().declare(formalParamIdentifier, formalParam.spawn());
		});

	}

	/**
	 * Actually perform function invocation by visiting its body
	 * @param func function to invocate
	 * @return returned value or GioviVoid
	 */
	private GioviValue<?> callFunction(Function func) {
		if (func.bodyCtx() == null) {
			if (func.signature().returnType().unbox() != GioviVoid.class) {
				// returning returnType default value
				return func.signature().returnType().spawn();
			}
			return new GioviVoid();
		}
		for (StatementContext statement : func.bodyCtx().statement()) {
			visit(statement);
			if (getCurrentScope().shouldReturn()) {
				var returnType = func.signature().returnType();
				var returnedValue = getCurrentScope().getReturnValue();
				if (returnType.unbox() == GioviVoid.class) {
					throw new GioviException(
						"Can't return " + returnedValue + " in a function with no return type");
				}
				try {
					return returnedValue.widenTo(returnType);
				} catch (NonWideneable ex) {
					throw new GioviException(ex, "Wrong return type, expecting " + returnType
						+ " but got " + returnedValue.getGioviType());
				}
			}
		}
		if (func.signature().returnType().unbox() != GioviVoid.class) {
			throw new GioviException("Missing return statement in function");
		}
		return new GioviVoid();
	}


	@Override
	public GioviParserVisitResult visitReturnStatement(ReturnStatementContext ctx) {
		var returnValue = ctx.expression() == null
			? new GioviVoid()
			: visit(ctx.expression()).asGioviValue();
		getCurrentScope().doReturn(returnValue);
		return returnValue;
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// Structs

	@Override
	public GioviParserVisitResult visitStructDefinition(StructDefinitionContext ctx) {

		var fieldDefinitions = new LinkedHashMap<String, GioviValue<?>>();

		if (ctx.parentName != null) {
			var parentDefinition = getDefinitionStruct(ctx.parentName.getText());
			parentDefinition
				.forEach((identifier, field) -> fieldDefinitions.put(identifier, field.copy()));
		}

		for (var fieldCtx : ctx.structBodyDefinition().structFieldDefinition()) {
			fieldDefinitions.put(
				fieldCtx.identifier().getText(),
				spawnType(fieldCtx.type(), fieldCtx.defaultDefinition()));
		}

		getCurrentScope().define(new Struct(ctx.structName.getText(), fieldDefinitions));

		return null;
	}

	@Override
	public GioviParserVisitResult visitStructCreation(StructCreationContext ctx) {
		var structFieldsCtx = ctx.structFields();
		return ctx.structType != null
			? createStruct(ctx.structType.getText(), structFieldsCtx)
			: createTuple(structFieldsCtx);
	}

	private GioviStruct createStruct(String typename, StructFieldsContext ctx) {
		var gioviStruct = new GioviStruct(getDefinitionStruct(typename).copy());
		if (ctx == null) {
			return gioviStruct;
		}
		if (ctx.namedStructField().isEmpty()) {
			initStructUnnamed(gioviStruct, ctx.expression());
		} else {
			initStructNamed(gioviStruct, ctx.namedStructField());
		}
		return gioviStruct;
	}

	private void initStructNamed(GioviStruct gioviStruct, List<NamedStructFieldContext> fieldsCtx) {
		for (var fieldCtx : fieldsCtx) {
			String fieldIdentifier = fieldCtx.identifier().getText();
			gioviStruct.boxField(
				fieldIdentifier,
				visit(fieldCtx.expression()).asGioviValue());
		}
	}

	private void initStructUnnamed(GioviStruct gioviStruct, List<ExpressionContext> expressions) {
		int expressionsCount = expressions.size();
		if (expressionsCount != gioviStruct.getFieldsCount()) {
			throw new GioviException("Wrong fields count. Expecting " + gioviStruct.getFieldsCount()
				+ " but instead got " + expressionsCount);
		}
		for (int i = 0; i < expressionsCount; i++) {
			gioviStruct.boxField(i, visit(expressions.get(i)).asGioviValue());
		}
	}

	private GioviTuple createTuple(StructFieldsContext ctx) {
		if (ctx == null) {
			return new GioviTuple(new Tuple());
		}
		if (ctx.namedStructField().isEmpty()) {
			var fields = new ArrayList<GioviValue<?>>();
			for (var expressionCtx : ctx.expression()) {
				fields.add(visit(expressionCtx).asGioviValue());
			}
			return new GioviTuple(new Tuple(fields));
		} else {
			var fields = new LinkedHashMap<String, GioviValue<?>>();
			for (var namedStructFieldCtx : ctx.namedStructField()) {
				fields.put(
					namedStructFieldCtx.identifier().getText(),
					visit(namedStructFieldCtx.expression()).asGioviValue());
			}
			return new GioviTuple(new TupleNamed(fields));
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Type

	@Override
	public GioviParserVisitResult visitTypeIdentifier(TypeIdentifierContext ctx) {
		return switch (ctx.getText()) {
			case "Natural" -> new GioviTypeNatural();
			case "Int" -> new GioviTypeInt();
			case "Real" -> new GioviTypeReal();
			case "Bool" -> new GioviTypeBool();
			case "Text" -> new GioviTypeText();
			default -> new GioviTypeStruct(getDefinitionStruct(ctx.getText()));
		};
	}

	@Override
	public GioviParserVisitResult visitTypeList(TypeListContext ctx) {
		var entryType = visit(ctx.type()).asGioviType();
		if (ctx.initSize == null) return new GioviTypeList(entryType);
		return switch (visit(ctx.initSize)) {
			case GioviInt i -> new GioviTypeList(entryType, i.unbox());
			case GioviNatural i -> new GioviTypeList(entryType, i.unbox().intValue());
			default -> throw new GioviException("Invalid list init size");
		};
	}

	@Override
	public GioviParserVisitResult visitTypeMap(TypeMapContext ctx) {
		return new GioviTypeMap(
			visit(ctx.mapKeyType).asGioviType(),
			visit(ctx.mapValueType).asGioviType());
	}

	private Struct getDefinitionStruct(String structType) {
		if (!getCurrentScope().isDefinedStruct(structType)) {
			throw new TypeNotFound(structType);
		}
		return getCurrentScope().getDefinitionStruct(structType);
	}

	@Override
	public GioviParserVisitResult visitTypeFunctionRef(TypeFunctionRefContext ctx) {
		// TODO: move formal parameters visit to a static method
		var parameters = new LinkedHashMap<String, FunctionSignatureParam>();
		if (ctx.formalParameters() != null) {
			for (var formalParamCtx : ctx.formalParameters().formalParameter()) {
				var formalParameter = (FunctionSignatureParam) visit(formalParamCtx);
				parameters.put(formalParameter.identifier(), formalParameter);
			}
		}

		GioviType<?> returnType = ctx.returnType != null
			? visit(ctx.returnType).asGioviType()
			: new GioviTypeVoid();

		return new GioviTypeFunction(new FunctionSignature("", parameters, returnType));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Literals
	@Override
	public GioviParserVisitResult visitPrimitiveContent(PrimitiveContentContext ctx) {
		return new GioviText(scanner.nextLine());
	}

	@Override
	public GioviParserVisitResult visitLiteralInteger(LiteralIntegerContext ctx) {
		return GioviInt.valueOf(ctx.getText());
	}

	@Override
	public GioviParserVisitResult visitLiteralNatural(LiteralNaturalContext ctx) {
		return GioviNatural.valueOf(ctx.getText());
	}

	@Override
	public GioviParserVisitResult visitLiteralReal(LiteralRealContext ctx) {
		return GioviReal.valueOf(ctx.getText());
	}

	@Override
	public GioviParserVisitResult visitLiteralBool(LiteralBoolContext ctx) {
		return GioviBool.valueOf(ctx.getText());
	}

	@Override
	public GioviParserVisitResult visitLiteralText(LiteralTextContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.unescapeEscapes()
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralTextBlock(LiteralTextBlockContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.unescapeEscapes()
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.trimIncidentalWhitespaces()
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralExactText(LiteralExactTextContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.unescapeEscapes()
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralExactTextBlock(LiteralExactTextBlockContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.trimIncidentalWhitespaces()
			.unescapeEscapes()
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralDefault(LiteralDefaultContext ctx) {
		return new GioviDefault();
	}

	@Override
	public GioviParserVisitResult visitLiteralPlaceholder(LiteralPlaceholderContext ctx) {
		return new GioviVoid();
	}

}
