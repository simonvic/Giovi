package it.simonvic.giovi;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import giovi.*;
import giovi.lang.*;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.delegates.SwitchStatementDelegate;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.TypeNotFound;
import it.simonvic.giovi.lang.*;
import it.simonvic.giovi.lang.annotation.Native;
import it.simonvic.giovi.util.GioviTypes;
import it.simonvic.giovi.util.TextProcessor;
import it.simonvic.giovi.util.VisitorUtils;

public class GioviVisitor extends GioviParserBaseVisitor<GioviParserVisitResult> {

	public static Set<Class<?>> findClasses(String packageName)
		throws URISyntaxException, IOException {
		String packageFolder = packageName.replaceAll("[.]", "/");
		URI packageURI = ClassLoader.getSystemClassLoader().getResource(packageFolder).toURI();
		Path packagePath = packageURI.getScheme().equals("file")
			? Path.of(packageURI)
			: FileSystems.newFileSystem(packageURI, Map.of()).getPath(packageFolder);
		return Files.walk(packagePath, 1)
			.map(path -> path.toString())
			.filter(pathName -> pathName.endsWith(".class"))
			.map(pathName -> {
				try {
					return toClass(pathName);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			})
			.collect(Collectors.toSet());
	}

	public static Class<?> toClass(String pathName) throws ClassNotFoundException {
		return Class.forName(pathName
			.replace(".class", "")
			.replaceFirst(".*target/classes/", "") // TODO: lol hack
			.replace("[$]", ".") // nested classes
			.replace("/", "."));
	}

	private static void scanAPIs()
		throws IllegalArgumentException, IllegalAccessException, URISyntaxException, IOException {
		var classes = findClasses("giovi");
		classes = classes.stream()
			.filter(c -> c.isAnnotationPresent(Native.Struct.class))
			// TODO: once we add include statements, we may add a @AutoInclude
			.collect(Collectors.toSet());
		for (var clazz : classes) {

			String typename = clazz.getAnnotation(Native.Struct.class).typename();
			if (typename.isEmpty()) {
				typename = clazz.getSimpleName();
			}

			// TODO: check if classField/method are static or final?
			var fields = new LinkedHashMap<String, VarD<?>>();
			var constants = new HashMap<String, GioviValue<?>>();
			for (Field classField : clazz.getFields()) {
				if (classField.isAnnotationPresent(Native.Struct.Field.class)) {
					fields.put(classField.getName(), (VarD<?>) classField.get(null));
				} else if (classField.isAnnotationPresent(Native.Const.class)) {
					constants.put(classField.getName(), (GioviValue<?>) classField.get(null));
				}
			}

			var functions = new HashMap<String, GioviFunction>();
			for (Method method : clazz.getMethods()) {
				if (method.isAnnotationPresent(Native.Func.class)) {
					functions.put(method.getName(), new GioviFunction(new JFunction(null, method)));
				}
			}
			GLOBAL_SCOPE.define(new Struct(typename, fields, constants, functions));

		}
	}

	private static final Scope GLOBAL_SCOPE = new Scope();

	static {
		try {
			scanAPIs();
		} catch (IllegalArgumentException | IllegalAccessException | URISyntaxException
			| IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected final Scanner scanner = new Scanner(System.in);
	private Scope currentScope = new Scope(GLOBAL_SCOPE);

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
				var type = visit(typeCheckCtx.type());
				// TODO: clafiry requirements. Should a type check be a type = type or a widening check?
				// A widening check could be useful for struct/tuple matching and destructing
				var castedSubject = subject.as(type);
				VarD<?> casted = switch (castedSubject) {
					case VarD<?> v -> v.copy();
					case Var<?> v -> new VarD<>(v.value());
					default -> new VarD<>(castedSubject.asGioviValue());
				};
				getCurrentScope().declare(typeCheckCtx.identifier().getText(), casted);
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

	// TODO: check if necessary to new VarD when deconstructing
	// TODO: check when declaring if it should be copied (by value) (e.g. iterating over a tuple)
	// TODO: for r Real in [1 2 3] do {} declare r first, then widen the entries

	@Override
	public GioviParserVisitResult visitForStatement(ForStatementContext ctx) {
		var iterator = ctx.iterator();
		var bodyCtx = ctx.statementOrBlock();
		var iterated = visit(ctx.iterable());
		// TODO: figure out a better way to handle vars
		if (iterated instanceof Var<?> v) {
			iterated = v.value();
		}
		switch (iterated) {

			case Range<?> range -> {
				var expanded = range.expandAsGioviList(); // TODO: workaround for range iteration
				switch (iterator) {

					case IteratorPlaceholderContext _ -> iterate(expanded, bodyCtx, _ -> {});

					case IteratorUnaryContext entryCtx -> iterate(expanded, bodyCtx, entry -> {
						getCurrentScope().declare(
							entryCtx.unaryIterator().identifier().getText(),
							new VarD<>(entry.value().copy()));
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
							new VarD<>(entry.value().copy()));
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
							new VarD<>(key.value().copy()));
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
		TriConsumer<StatementOrBlockContext, Var<?>, Var<?>> iterationBody;
		try {
			iterationBody = switch (keyIteratorCtx) {

				case IteratorPlaceholderContext _ -> switch (valueIteratorCtx) {

					case IteratorPlaceholderContext _ -> (body, _, _) -> visit(body);

					case IteratorUnaryContext valueCtx -> (body, _, v) -> {
						String valueIdentifier = valueCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(valueIdentifier, new VarD<>(v.value().copy()));
						visit(body);
					};

					case IteratorAggregateContext valueCtx -> (body, _, v) -> {
						deconstruct(v.copy(), valueCtx);
						visit(body);
					};

					case IteratorMapContext valueCtx -> (body, k, v) -> {
						if (v.value() instanceof GioviMap nestedMap) {
							iterate(nestedMap, body, valueCtx.key, valueCtx.value);
						}
					};

					default -> throw new GioviException("Unsupported map value");
				};

				case IteratorUnaryContext keyCtx -> {
					String keyIdentifier = keyCtx.unaryIterator().identifier().getText();

					yield switch (valueIteratorCtx) {
						case IteratorPlaceholderContext _ -> (body, k, _) -> {
							getCurrentScope().declare(keyIdentifier, new VarD<>(k.value().copy()));
							visit(body);
						};

						case IteratorUnaryContext valueCtx -> (body, k, v) -> {
							getCurrentScope().declare(keyIdentifier, new VarD<>(k.value().copy()));
							getCurrentScope().declare(
								valueCtx.unaryIterator().identifier().getText(),
								new VarD<>(v.value().copy()));
							visit(body);
						};

						case IteratorAggregateContext valueCtx -> (body, k, v) -> {
							getCurrentScope().declare(keyIdentifier, new VarD<>(k.value().copy()));
							deconstruct(v.copy(), valueCtx);
							visit(body);
						};

						case IteratorMapContext valueCtx -> (body, k, v) -> {
							getCurrentScope().declare(keyIdentifier, new VarD<>(k.value().copy()));
							if (v.value() instanceof GioviMap nestedMap) {
								iterate(nestedMap, body, valueCtx.key, valueCtx.value);
							}
						};

						default -> throw new GioviException("Unsupported map value");
					};
				}

				case IteratorAggregateContext keyCtx -> switch (valueIteratorCtx) {

					case IteratorPlaceholderContext _ -> (body, k, _) -> {
						deconstruct(k.copy(), keyCtx);
						visit(body);
					};

					case IteratorUnaryContext valueCtx -> (body, k, v) -> {
						deconstruct(k.copy(), keyCtx);
						String valueIdentifier = valueCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(valueIdentifier, new VarD<>(v.value().copy()));
						visit(body);
					};

					case IteratorAggregateContext valueCtx -> (body, k, v) -> {
						deconstruct(k.copy(), keyCtx);
						deconstruct(v.copy(), valueCtx);
						visit(body);
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
			getCurrentScope().declare(aggregateCtx.whole.identifier().getText(),
				new VarD<>(aggregate.value()));
		}
		switch (aggregate.value()) {
			case GioviStruct struct -> deconstruct(struct, aggregateCtx);
			case GioviTupleNamed tuple -> deconstruct(tuple, aggregateCtx);
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
	private void deconstruct(GioviTupleNamed tuple, IteratorAggregateContext aggregateCtx) {
		throw new GioviException("TupleNamed deconstruct not implemented yet");
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
						.orElseThrow(() -> new GioviException(
							"Can't deconstruct",
							new NoSuchField(fieldIdentifier)));
					getCurrentScope().declare(fieldIdentifier, field);
				}

				case IteratorAggregateContext aCtx -> {
					if (aCtx.whole == null) {
						throw new GioviException(
							"Can't deconstruct nested aggregate without a whole identifier");
					}
					String fieldIdentifier = aCtx.whole.identifier().getText();
					var field = struct.value().getField(fieldIdentifier)
						.orElseThrow(() -> new GioviException(
							"Can't deconstruct",
							new NoSuchField(fieldIdentifier)));
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
		Consumer<Var<?>> preamble) {
		for (var entry : list.value()) {
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
		BiConsumer<Var<?>, Var<?>> preamble) {
		for (var entry : map.value().entrySet()) {
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
		TriConsumer<StatementOrBlockContext, Var<?>, Var<?>> loopBody) {
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
		visit(ctx.varDeclaration()).redirection(visit(ctx.redirectionContent()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToVarDeclarationRTL(
		RedirectionToVarDeclarationRTLContext ctx) {
		visit(ctx.varDeclaration()).redirection(visit(ctx.redirectionContent()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitVarDeclaration(VarDeclarationContext ctx) {
		var var = spawnType(ctx.type(), ctx.defaultDefinition());
		getCurrentScope().declare(ctx.identifier().getText(), var);
		return var;
	}

	private VarD<?> spawnType(
		TypeContext typeCtx,
		DefaultDefinitionContext defaultDefinitionCtx) {
		return spawnType(visit(typeCtx).asGioviType(), defaultDefinitionCtx);
	}

	private VarD<?> spawnType(
		GioviType<?> type,
		DefaultDefinitionContext defaultDefinitionCtx) {
		if (defaultDefinitionCtx == null) {
			return new VarD<>(type.spawn(), type.spawn());
		}
		var defaultValue = visit(defaultDefinitionCtx).unwrapAsGioviValue();
		try {
			defaultValue = defaultValue.widenTo(type);
		} catch (NonWideneable e) {
			throw new GioviException(e, "Incompatible default value");
		}
		return new VarD<>(defaultValue.copy(), defaultValue);
	}

	@Override
	public GioviParserVisitResult visitConstantDeclaration(ConstantDeclarationContext ctx) {
		var constant = spawnType(ctx.type(), null);
		constant.setValueWithRedirection(visit(ctx.expression()));
		getCurrentScope().declareConstant(ctx.constantIdentifier().getText(), constant.value());
		return constant;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToIdentifier(RedirectionToIdentifierContext ctx) {
		VisitorUtils.redirection(
			visit(ctx.redirectionContent()),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.identifier()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToIdentifierRTL(
		RedirectionToIdentifierRTLContext ctx) {
		VisitorUtils.redirection(
			visit(ctx.redirectionContent()),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.identifier()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToConstantIdentifier(
		RedirectionToConstantIdentifierContext ctx) {
		throw new GioviException("Can't redirect to a constant");
		// TODO: redirecting to field still overrides the field 69 -> SOME_CONSTANT.someField
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
			return getCurrentScope()
				.getFunction(identifier)
				.orElseThrow(() -> new GioviException(
					"Can't reference non-existing function " + identifier));
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
		return new GFunction(signature, ctx.body);
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
			return lhs.dot(new MemberKey(ctx.anyIdentifier().getText()));
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
		var containment = VisitorUtils.containment(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1)));
		if (ctx.NOT() != null) {
			return containment.not();
		}
		return containment;
	}

	@Override
	public GioviParserVisitResult visitExpressionCast(ExpressionCastContext ctx) {
		return visit(ctx.expression()).as(visit(ctx.type()));
	}

	@Override
	public GioviParserVisitResult visitExpressionTypeCheck(ExpressionTypeCheckContext ctx) {
		var value = visit(ctx.expression()).unwrapAsGioviValue();
		var type = visit(ctx.type()).asGioviType();
		boolean equals = value.getGioviType().equals(type);
		if (ctx.NOT() != null) {
			return new GioviBool(!equals);
		}
		return new GioviBool(equals);
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
		String identifier = ctx.getText();
		var varOpt = getCurrentScope().getVariable(identifier);
		if (varOpt.isPresent()) {
			return varOpt.get();
		}
		var funcOpt = getCurrentScope().getFunction(identifier);
		if (funcOpt.isPresent()) {
			return funcOpt.get();
		}
		throw new GioviException("No variable nor function defined with name " + identifier);
	}

	@Override
	public GioviParserVisitResult visitConstantIdentifier(ConstantIdentifierContext ctx) {
		return getCurrentScope()
			.getConstantValue(ctx.getText())
			.orElseThrow(() -> new GioviException("Constant not declared: " + ctx.getText()));
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
			visit(ctx.start).unwrapAsGioviValue(),
			visit(ctx.end).unwrapAsGioviValue(),
			ctx.step == null ? null : visit(ctx.step));
	}

	// TODO: move type inference here instead of reiterating list/map

	@Override
	public GioviParserVisitResult visitListCreation(ListCreationContext ctx) {
		var entries = new ArrayList<Var<?>>();
		for (var entryCtx : ctx.listEntries().listEntry()) {
			var entry = visit(entryCtx);
			switch (entry) {
				case Range<?> range -> entries.addAll(range.expandAsVar());
				case GioviValue<?> v -> entries.add(new Var<>(v));
				case Var<?> v -> entries.add(new Var<>(v.value().copy()));
				default -> throw new GioviException(
					"Incompatible type for list creation: " + entry.getClass());
			}
		}
		var entryType = GioviTypes.inferGioviType(entries);
		return new GioviList(entryType, entries);
	}

	@Override
	public GioviParserVisitResult visitMapCreation(MapCreationContext ctx) {
		var map = new LinkedHashMap<Var<?>, Var<?>>();
		if (ctx.mapEntries() == null) {
			return new GioviMap(new GioviTypeVoid(), new GioviTypeVoid(), map);
		}
		for (var entryCtx : ctx.mapEntries().mapEntry()) {
			var key = visit(entryCtx.mapKey).unwrapAsGioviValue();
			var element = visit(entryCtx.mapValue).unwrapAsGioviValue();
			map.put(new Var<>(key), new Var<>(element));
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

		getCurrentScope().declare(new GFunction(signature, ctx.body));
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
		return doVisitFunctionCall(ctx.expression(), ctx.functionCallParameters());
	}

	@Override
	public GioviParserVisitResult visitExpressionFunctionCall(ExpressionFunctionCallContext ctx) {
		return doVisitFunctionCall(ctx.expression(), ctx.functionCallParameters());
	}

	private GioviParserVisitResult doVisitFunctionCall(
		ExpressionContext expressionCtx,
		FunctionCallParametersContext parametersCtx) {
		var visit = visit(expressionCtx);
		Function function = switch (visit) {
			case GioviFunction f -> f.value();
			case Function f -> f;
			case Var<?> v when v.value() instanceof GioviFunction f -> f.value();
			default -> throw new GioviException("Invokation fail " + visit);
		};
		enterFunctionScope();
		var returnValue = switch (function) {
			case JFunction jFunction -> {
				try {
					yield jFunction.invoke(buildFunctionParameters(parametersCtx));
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new GioviException("Invocation fail", e);
				}
			}
			case GFunction gFunction -> {
				setupFunctionParameters(parametersCtx, function.signature());
				yield callFunction(gFunction);
			}
		};
		exitScope();
		return returnValue;
	}

	private GioviValue<?>[] buildFunctionParameters(
		FunctionCallParametersContext callParamsCtx) {
		if (callParamsCtx == null) {
			return new GioviValue<?>[] {};
		} else if (callParamsCtx.functionCallParameter(0).identifier() == null) {
			return buildFunctionParametersIteratively(callParamsCtx.functionCallParameter());
		} else {
			return buildNamedFunctionParameters(callParamsCtx.functionCallParameter());
		}
	}

	private GioviValue<?>[] buildNamedFunctionParameters(
		List<FunctionCallParameterContext> actualParamCtxList) {
		throw new UnsupportedOperationException(
			"Unimplemented method 'buildNamedFunctionParameters'");
	}

	private GioviValue<?>[] buildFunctionParametersIteratively(
		List<FunctionCallParameterContext> actualParamCtxList) {
		var args = new GioviValue<?>[actualParamCtxList.size()];
		for (int i = 0; i < args.length; i++) {
			args[i] = visit(actualParamCtxList.get(i).expression()).unwrapAsGioviValue();
		}
		return args;
	}

	/**
	 * Visit function call parameters and declare actual parameters
	 * @param callParamsCtx Function call context
	 * @param signature Function signature
	 */
	private void setupFunctionParameters(
		FunctionCallParametersContext callParamsCtx,
		FunctionSignature signature) {
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

			// if no more actual params found, expecting remaining formal parameters to be optional
			if (i >= actualParamsCount) {
				for (int j = i; j < formalParamsCount; j++) {
					var actualParam = formalParam.spawn();
					formalParam = formalParamsArray.get(j);
					formalParamIdentifier = formalParam.identifier();
					if (!formalParam.isOptional()) {
						throw new GioviException(
							"Required parameter not specified: " + formalParamIdentifier);
					}
					getCurrentScope().declare(formalParamIdentifier, actualParam);
				}
				break;
			}

			var actualParam = formalParam.spawn();
			var actualParamExpression = visit(actualParamCtxList.get(i).expression());

			// TODO: eventually remove it and let the redirection handle the default
			if (actualParamExpression instanceof GioviDefault) { // TODO: use GioviPlaceholder
				getCurrentScope().declare(formalParamIdentifier, actualParam);
				continue;
			}

			actualParam.setValueWithRedirection(actualParamExpression);
			getCurrentScope().declare(formalParamIdentifier, actualParam);
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
			var actualParam = formalParam.spawn();

			var actualParamExpression = visit(actualParams.get(formalParamIdentifier).expression());
			if (actualParamExpression instanceof GioviDefault) { // TODO: use GioviPlaceholder
				getCurrentScope().declare(formalParamIdentifier, actualParam);
			} else {
				actualParam.setValueWithRedirection(actualParamExpression);
				getCurrentScope().declare(formalParamIdentifier, actualParam);
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
	private GioviValue<?> callFunction(GFunction func) {
		if (func.bodyCtx() == null) {
			if (func.signature().returnType().value() != GioviVoid.class) {
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
				if (returnType.value() == GioviVoid.class) {
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
		if (func.signature().returnType().value() != GioviVoid.class) {
			throw new GioviException("Missing return statement in function");
		}
		return new GioviVoid();
	}


	@Override
	public GioviParserVisitResult visitReturnStatement(ReturnStatementContext ctx) {
		var returnValue = ctx.expression() == null
			? new GioviVoid()
			: visit(ctx.expression()).unwrapAsGioviValue();
		getCurrentScope().doReturn(returnValue);
		return returnValue;
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// Structs

	@Override
	public GioviParserVisitResult visitStructDefinition(StructDefinitionContext ctx) {

		var fieldDefinitions = new LinkedHashMap<String, VarD<?>>();

		if (ctx.parentName != null) {
			String parentTypename = ctx.parentName.getText();
			var parentDefinition = getCurrentScope()
				.getDefinitionStruct(parentTypename)
				.orElseThrow(() -> new GioviException(
					"Can't inherit from " + parentTypename,
					new TypeNotFound(parentTypename)));
			parentDefinition.forEach(
				(identifier, field) -> fieldDefinitions.put(identifier, field.copy()));
		}

		for (var fieldCtx : ctx.structBodyDefinition().structFieldDefinition()) {
			fieldDefinitions.put(
				fieldCtx.identifier().getText(),
				spawnType(fieldCtx.type(), fieldCtx.defaultDefinition()));
			// TODO: maybe add warning about unimplemented recursive structs
		}

		var constants = new HashMap<String, GioviValue<?>>();
		var functions = new HashMap<String, GioviFunction>();
		// TODO: double check that we don't lose constants and functions definitions somewhere else
		getCurrentScope().define(new Struct(
			ctx.structName.getText(),
			fieldDefinitions,
			constants,
			functions));

		for (var constCtx : ctx.structBodyDefinition().constantDeclaration()) {
			var constant = spawnType(constCtx.type(), null);
			constant.setValueWithRedirection(visit(constCtx.expression()));
			constants.put(
				constCtx.constantIdentifier().getText(),
				constant.value());
		}

		for (var funcCtx : ctx.structBodyDefinition().functionDeclaration()) {
			// TODO: once formal and actual parameter parser rules have been refactored, remove this duplicated code
			FunctionSignature signature;
			String identifier = funcCtx.identifier().getText();
			GioviType<?> returnType = funcCtx.type() == null
				? new GioviTypeVoid()
				: visit(funcCtx.type()).asGioviType();
			if (funcCtx.formalParameters() == null) {
				signature = funcCtx.type() == null
					? new FunctionSignature(identifier)
					: new FunctionSignature(identifier, returnType);
			} else {
				var parameters = new LinkedHashMap<String, FunctionSignatureParam>();
				for (var formalParamCtx : funcCtx.formalParameters().formalParameter()) {
					var formalParameter = (FunctionSignatureParam) visit(formalParamCtx);
					parameters.put(formalParameter.identifier(), formalParameter);
				}
				signature = new FunctionSignature(identifier, parameters, returnType);
			}
			functions.put(identifier, new GioviFunction(new GFunction(signature, funcCtx.body)));
		}

		return null;
	}

	@Override
	public GioviParserVisitResult visitStructCreation(StructCreationContext ctx) {
		var structFieldsCtx = ctx.structFields();
		if (ctx.structType != null) {
			return createStruct(ctx.structType.getText(), structFieldsCtx);
		}
		if (structFieldsCtx == null || structFieldsCtx.namedStructField().isEmpty()) {
			return createTuple(structFieldsCtx);
		}
		return createTupleNamed(structFieldsCtx);
	}

	private GioviStruct createStruct(String typename, StructFieldsContext ctx) {
		var struct = getCurrentScope()
			.getDefinitionStruct(typename)
			.orElseThrow(() -> new GioviException(
				"Can't create struct " + typename,
				new TypeNotFound(typename)))
			.copy();
		var gioviStruct = new GioviStruct(struct);
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
			gioviStruct.value()
				.getField(fieldIdentifier).orElseThrow(() -> new NoSuchField(fieldIdentifier))
				.setValueWithRedirection(visit(fieldCtx.expression()));
		}
	}

	private void initStructUnnamed(GioviStruct gioviStruct, List<ExpressionContext> expressions) {
		int expressionsCount = expressions.size();
		if (expressionsCount != gioviStruct.value().getFieldsCount()) {
			throw new GioviException(
				"Wrong fields count. Expecting " + gioviStruct.value().getFieldsCount()
					+ " but instead got " + expressionsCount);
		}
		for (int i = 0; i < expressionsCount; i++) {
			final int j = i;
			gioviStruct.value()
				.getField(i).orElseThrow(() -> new NoSuchField(j))
				.setValueWithRedirection(visit(expressions.get(i)));
		}
	}

	private GioviTuple createTuple(StructFieldsContext ctx) {
		if (ctx == null) {
			return new GioviTuple(new Tuple());
		}
		var fields = new ArrayList<VarD<?>>();
		for (var expressionCtx : ctx.expression()) {
			var fieldValue = visit(expressionCtx).unwrapAsGioviValue();
			fields.add(new VarD<>(fieldValue));
		}
		return new GioviTuple(new Tuple(fields));

	}

	private GioviTupleNamed createTupleNamed(StructFieldsContext ctx) {
		if (ctx == null) {
			return new GioviTupleNamed(new TupleNamed());
		}
		var fields = new LinkedHashMap<String, VarD<?>>();
		for (var namedStructFieldCtx : ctx.namedStructField()) {
			var fieldValue = visit(namedStructFieldCtx.expression()).unwrapAsGioviValue();
			fields.put(
				namedStructFieldCtx.identifier().getText(),
				new VarD<>(fieldValue));
		}
		return new GioviTupleNamed(new TupleNamed(fields));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Type

	@Override
	public GioviParserVisitResult visitTypeIdentifier(TypeIdentifierContext ctx) {
		String typename = ctx.getText();
		return switch (typename) {
			case "Natural" -> new GioviTypeNatural();
			case "Int" -> new GioviTypeInt();
			case "Real" -> new GioviTypeReal();
			case "Bool" -> new GioviTypeBool();
			case "Text" -> new GioviTypeText();
			default -> new GioviTypeStruct(getCurrentScope()
				.getDefinitionStruct(typename)
				.orElseThrow(() -> new TypeNotFound(typename)));
		};
	}

	@Override
	public GioviParserVisitResult visitTypeList(TypeListContext ctx) {
		var entryType = visit(ctx.type()).asGioviType();
		if (ctx.initSize == null)
			return new GioviTypeList(entryType);
		return switch (visit(ctx.initSize).unwrapAsGioviValue()) {
			case GioviInt i -> new GioviTypeList(entryType, i.value());
			case GioviNatural i -> new GioviTypeList(entryType, i.value().intValue());
			default -> throw new GioviException("Invalid list init size");
		};
	}

	@Override
	public GioviParserVisitResult visitTypeMap(TypeMapContext ctx) {
		return new GioviTypeMap(
			visit(ctx.mapKeyType).asGioviType(),
			visit(ctx.mapValueType).asGioviType());
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
