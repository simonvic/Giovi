package it.simonvic.giovi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import giovi.*;
import giovi.Module;
import giovi.Module.FileModule;
import giovi.Module.InnerModule;
import giovi.Module.NativeModule;
import giovi.Record;
import giovi.lang.*;
import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
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
import it.simonvic.giovi.util.Natives;
import it.simonvic.giovi.util.TextProcessor;
import it.simonvic.giovi.util.VisitorUtils;

public class GioviVisitor extends GioviParserBaseVisitor<GioviParserVisitResult> {

	private static Module scanGioviModule() throws URISyntaxException, IOException {
		var gioviModule = new NativeModule("giovi", ROOT_MODULE);

		URI packageURI = ClassLoader.getSystemClassLoader()
			.getResource(gioviModule.identifier())
			.toURI();

		Path gioviPackagePath = (packageURI.getScheme().equals("file"))
			? Path.of(packageURI)
			: FileSystems.newFileSystem(packageURI, Map.of()).getPath(gioviModule.identifier());

		var gioviModuleClasses = Files.walk(gioviPackagePath, 69)
			.map(path -> path.toString())
			.filter(pathName -> pathName.endsWith(".class"))
			.<Class<?>>map(pathName -> {
				try {
					return Class.forName(pathName
						.replace(".class", "")
						.replaceFirst(".*target/classes/", "") // TODO: lol hack
						.replace("[$]", ".") // nested classes
						.replace("/", "."));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			})
			.filter(c -> c.isAnnotationPresent(Native.Module.class))
			// .distinct() // TODO: needed?
			.toList();

		for (var gioviModuleClass : gioviModuleClasses) {
			Module parentModule = gioviModule;
			String[] moduleIdentifiers = gioviModuleClass.getName().split("(\\.|\\$)"); // NOTE: $ is used for nested classes
			for (int i = 1; i < moduleIdentifiers.length; i++) { // NOTE: first module ("giovi") is skipped
				String moduleIdentifier = moduleIdentifiers[i];
				var moduleOpt = parentModule.scope().getModule(moduleIdentifier);
				if (moduleOpt.isPresent()) {
					parentModule = moduleOpt.orElseThrow();
				} else {
					var newModule = new NativeModule(moduleIdentifier, parentModule);
					parentModule.scope().declare(newModule);
					parentModule = newModule;
				}
			}
			Natives.scanModule(parentModule, gioviModuleClass);
		}
		return gioviModule;
	}

	private static final Module ROOT_MODULE = new NativeModule("");
	private static final Module GIOVI_MODULE;

	static {
		try {
			GIOVI_MODULE = scanGioviModule();
			ROOT_MODULE.scope().declare(GIOVI_MODULE);
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected final Scanner scanner = new Scanner(System.in);
	private Scope currentScope;
	private final Module module;

	public GioviVisitor(String moduleIdentifier) {
		this(new NativeModule(moduleIdentifier, ROOT_MODULE));
	}

	public GioviVisitor(Module module) {
		this.module = module;
		this.currentScope = module.scope();
		this.currentScope.declare(GIOVI_MODULE);
	}

	public Module getModule() {
		return module;
	}

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
	public GioviParserVisitResult visitImportStatement(ImportStatementContext ctx) {
		for (var importEntryCtx : ctx.importEntry()) {
			visit(importEntryCtx);
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitImportEntry(ImportEntryContext ctx) {
		// TODO: lookup local modules first, then file modules

		var moduleURI = URI.create(ctx.identifier()
			.stream()
			.filter(identifierCtx -> !identifierCtx.equals(ctx.alias))
			.map(IdentifierContext::getText)
			.collect(Collectors.joining("/"))
			.concat(".giovi"));
		try {
			var lexer = new GioviLexer(CharStreams.fromFileName(moduleURI.toString()));
			var parser = new GioviParser(new CommonTokenStream(lexer));
			var tree = parser.begin();
			String moduleIdentifier = ctx.identifier().getLast().getText();
			var module = new FileModule(moduleIdentifier, ROOT_MODULE, moduleURI);
			var visitor = new GioviVisitor(module);
			visitor.visit(tree);
			this.getCurrentScope().declare(module);
		} catch (Exception ex) {
			throw new GioviException(ex);
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitModuleDeclaration(ModuleDeclarationContext ctx) {
		// FIXME: cycle
		String moduleIdentifier = ctx.identifier().getText();
		var module = new InnerModule(moduleIdentifier, this.module, ctx.body);
		getCurrentScope().declare(module);
		return module;
	}

	@Override
	public GioviParserVisitResult visitIfStatement(IfStatementContext ctx) {
		if (visit(ctx.expression()).unwrapAsGioviValue().asNativeBool()) {
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
		while (infiniteRepeat || visit(ctx.expression()).unwrapAsGioviValue().asNativeBool()) {
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
						String identifier = entryCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(
							identifier,
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
						String identifier = entryCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(
							identifier,
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
						String keyIdentifier = keyCtx.unaryIterator().identifier().getText();
						getCurrentScope().declare(
							keyIdentifier,
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
		IteratorContext valueIteratorCtx
	) {
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
							String valueIdentifier = valueCtx.unaryIterator().identifier()
								.getText();
							getCurrentScope().declare(
								valueIdentifier,
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
			getCurrentScope().declare(
				aggregateCtx.whole.getText(),
				new VarD<>(aggregate.value()));
		}
		switch (aggregate.value()) {
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
					String fieldIdentifier = aCtx.whole.getText();
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
		Consumer<Var<?>> preamble
	) {
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
		BiConsumer<Var<?>, Var<?>> preamble
	) {
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

	@Override
	public GioviParserVisitResult visitInvocationStatementSimple(
		InvocationStatementSimpleContext ctx
	) {
		if (ctx.expression() == null) {
			return visit(ctx.invocation());
		}
		var instance = visit(ctx.expression());
		var member = instance.dot(new MemberKey(ctx.invocation().identifier().getText()));
		return doVisitInvocation(instance, member, ctx.invocation().actualParameters());
	}

	@Override
	public GioviParserVisitResult visitInvocationStatementOfExpression(
		InvocationStatementOfExpressionContext ctx
	) {
		return doVisitInvocation(visit(ctx.expression()), ctx.actualParameters());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// REDIRECTIONS

	@Override
	public GioviParserVisitResult visitRedirectionToVarDeclaration(
		RedirectionToVarDeclarationContext ctx
	) {
		visit(ctx.dest).redirection(visit(ctx.content));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToVarDeclarationRTL(
		RedirectionToVarDeclarationRTLContext ctx
	) {
		visit(ctx.dest).redirection(visit(ctx.content));
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
		DefaultDefinitionContext defaultDefinitionCtx
	) {
		return spawnType(visit(typeCtx).asGioviType(), defaultDefinitionCtx);
	}

	private VarD<?> spawnType(
		GioviType<?> type,
		DefaultDefinitionContext defaultDefinitionCtx
	) {
		if (defaultDefinitionCtx == null) {
			return new VarD<>(type.spawn(), type.spawn());
		}
		var defaultValue = visit(defaultDefinitionCtx).unwrapAsGioviValue();
		try {
			defaultValue = type.widen(defaultValue);
		} catch (NonWideneable e) {
			throw new GioviException(e, "Incompatible default value");
		}
		return new VarD<>(defaultValue.copy(), defaultValue);
	}

	@Override
	public GioviParserVisitResult visitConstantDeclaration(ConstantDeclarationContext ctx) {
		var constant = spawnType(ctx.type(), null);
		constant.setValueWithRedirection(visit(ctx.expression()));
		getCurrentScope().declareConstant(ctx.identifier().getText(), constant.value());
		return constant;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToIdentifier(RedirectionToIdentifierContext ctx) {
		VisitorUtils.redirection(
			visit(ctx.content),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.dest));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToIdentifierRTL(
		RedirectionToIdentifierRTLContext ctx
	) {
		VisitorUtils.redirection(
			visit(ctx.content),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.dest));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToExpression(RedirectionToExpressionContext ctx) {
		VisitorUtils.redirection(
			visit(ctx.content),
			GioviToken.of(ctx.redirectionOperator().getText()),
			visit(ctx.dest));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToExpressionRTL(
		RedirectionToExpressionRTLContext ctx
	) {
		VisitorUtils.redirection(
			visit(ctx.content),
			GioviToken.of(ctx.redirectionOperatorRTL().getText()),
			visit(ctx.dest));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToReservedIdentifier(
		RedirectionToReservedIdentifierContext ctx
	) {
		VisitorUtils.redirectionToPrimitive(
			visit(ctx.content),
			GioviToken.of(ctx.redirectionOperator().getText()),
			ctx.reservedIdentifier().getText());
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToReservedIdentifierRTL(
		RedirectionToReservedIdentifierRTLContext ctx
	) {
		VisitorUtils.redirectionToPrimitive(
			visit(ctx.content),
			GioviToken.of(ctx.redirectionOperatorRTL().getText()),
			ctx.reservedIdentifier().getText());
		return null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Expressions

	@Override
	public GioviParserVisitResult visitExpressionParenthesized(ExpressionParenthesizedContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public GioviParserVisitResult visitExpressionRef(ExpressionRefContext ctx) {
		var referenced = visit(ctx.referenceable()).unwrapAsGioviValue();
		if (referenced instanceof GioviFunction f) {
			return f;
		}
		throw new GioviException("Can't reference non-function " + referenced);
	}

	@Override
	public GioviParserVisitResult visitReferenceable(ReferenceableContext ctx) {
		if (ctx.anonFunction() != null) {
			return visit(ctx.anonFunction());
		}
		String identifier = ctx.identifier().getText();
		if (ctx.expression() != null) {
			var lhs = visit(ctx.expression());
			var member = lhs.dot(new MemberKey(identifier));
			if (member instanceof GioviFunction gioviFunction) {
				return gioviFunction;
			}
			throw new GioviException(
				"Can't reference non-existing member function " + identifier + " in " + lhs);
		}
		Function function = getCurrentScope()
			.getFunction(identifier)
			.orElseThrow(() -> new GioviException(
				"Can't reference non-existing function " + identifier));
		return new GioviFunction(function);
	}

	@Override
	public GioviParserVisitResult visitAnonFunction(AnonFunctionContext ctx) {
		FunctionSignature signature = ctx.anonFunctionSignature() == null
			? FunctionSignature.EMPTY
			: (FunctionSignature) visit(ctx.anonFunctionSignature());
		return new GioviFunction(new GFunction(signature, ctx.body));
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
		ExpressionMultiplicativeContext ctx
	) {
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
		if (ctx.identifier() != null) {
			return lhs.dot(new MemberKey(ctx.identifier().getText()));
		}
		if (ctx.DEFAULT() != null) {
			return lhs.dot(new GioviDefault());
		}
		return doVisitInvocation(
			lhs,
			lhs.dot(new MemberKey(ctx.invocation().identifier().getText())),
			ctx.invocation().actualParameters());
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
		return getCurrentScope()
			.getSymbol(identifier)
			.orElseThrow(() -> new GioviException(
				"No variable, constant, function, type or module found with name " + identifier));
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
		var entries = new ArrayList<Var<?>>(ctx.listEntries().listEntry().size());
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
		var signature = (FunctionSignature) visit(ctx.functionSignature());
		var function = new GFunction(signature, ctx.body);
		getCurrentScope().declare(function);
		return signature;
	}

	@Override
	public GioviParserVisitResult visitFunctionSignature(FunctionSignatureContext ctx) {
		String identifier = ctx.name.getText();
		var signature = (FunctionSignature) visit(ctx.anonFunctionSignature());
		return new FunctionSignature(identifier, signature.parameters(), signature.returnType());
	}

	@Override
	public GioviParserVisitResult visitAnonFunctionSignature(AnonFunctionSignatureContext ctx) {
		var parameters = (FormalParameters) visit(ctx.formalParameters());

		GioviType<?> returnType = ctx.returnType != null
			? visit(ctx.returnType).asGioviType()
			: new GioviTypeVoid();

		return new FunctionSignature("", parameters, returnType);
	}

	@Override
	public GioviParserVisitResult visitFormalParameters(FormalParametersContext ctx) {
		int size = ctx.formalParameter().size();
		var parameters = new FormalParameter[size];
		for (int i = 0; i < size; i++) {
			parameters[i] = (FormalParameter) visit(ctx.formalParameter(i));
		}
		return new FormalParameters(parameters);
	}

	@Override
	public GioviParserVisitResult visitFormalParameter(FormalParameterContext ctx) {
		String identifier = ctx.name.getText();
		String label = ctx.label != null
			? ctx.label.getText()
			: identifier;
		var type = visit(ctx.type()).asGioviType();
		var param = spawnType(type, ctx.defaultDefinition());
		boolean isOptional = ctx.defaultDefinition() != null;
		return new FormalParameter(label, identifier, type, param, isOptional);
	}

	@Override
	public GioviParserVisitResult visitInvocation(InvocationContext ctx) {
		return doVisitInvocation(visit(ctx.identifier()), ctx.actualParameters());
	}

	@Override
	public GioviParserVisitResult visitExpressionInvocation(ExpressionInvocationContext ctx) {
		return doVisitInvocation(visit(ctx.expression()), ctx.actualParameters());
	}

	private GioviParserVisitResult doVisitInvocation(
		GioviParserVisitResult invoked,
		ActualParametersContext actualParameters
	) {
		return doVisitInvocation(null, invoked, actualParameters);
	}

	private GioviParserVisitResult doVisitInvocation(
		GioviParserVisitResult instance,
		GioviParserVisitResult invoked,
		ActualParametersContext parametersCtx
	) {
		if (!(invoked.unwrapAsGioviValue() instanceof GioviFunction gioviFunction)) {
			throw new GioviException("Invokation error: " + invoked);
		}
		Function function = gioviFunction.value();
		enterFunctionScope();
		LinkedHashMap<String, VarD<?>> actualParams = setupFunctionParameters(
			parametersCtx,
			function.signature());
		if (function.signature().hasParameter("self")) {
			// TODO: passing by reference yields much better performance, but
			// it's different from other params getting passed by value.
			// Clarify requirements
			actualParams.putFirst("self", new VarD<>(instance.unwrapAsGioviValue()));
		}
		var returnValue = switch (function) {
			case JFunction jFunction -> {
				var args = new GioviValue<?>[actualParams.size()];
				int i = 0;
				for (var actualParam : actualParams.entrySet()) {
					args[i++] = actualParam.getValue().value();
				}
				try {
					yield jFunction.invoke(args);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new GioviException("Invocation fail", e);
				}
			}
			case GFunction gFunction -> {
				for (var actualParam : actualParams.entrySet()) {
					getCurrentScope().declare(actualParam.getKey(), actualParam.getValue());
				}
				yield doInvoke(gFunction);
			}
		};
		if (returnValue instanceof GioviVoid
			&& !(function.signature().returnType() instanceof GioviTypeVoid)) {
			throw new GioviException("Missing return statement in function");
		}
		exitScope();
		return returnValue;
	}

	/**
	 * Visit function call parameters and declare actual parameters
	 * @param actualParamsContext Function call context
	 * @param signature Function signature
	 */
	private LinkedHashMap<String, VarD<?>> setupFunctionParameters(
		ActualParametersContext actualParamsContext,
		FunctionSignature signature
	) {
		// NOTE: if parameters are either positional or named depends on the first callParameter
		// If no parameters have been specified, positional approach is used
		List<ActualParameterContext> actualParameters = new ArrayList<>(
			actualParamsContext.actualParameter());
		if (signature.hasParameter("self")) {
			// TODO: lol hack
			actualParameters.addFirst(new ActualParameterContext(null, -1));
		}
		if (actualParameters.isEmpty() || actualParameters.getFirst().identifier() == null) {
			return setupFunctionParametersIteratively(actualParameters, signature);
		} else {
			return setupNamedFunctionParameters(actualParameters, signature);
		}

	}

	/**
	 * Visit function call positional parameters and declare actual parameters
	 * @param actualParamCtxList Function call context
	 * @param signature Function signature
	 */
	private LinkedHashMap<String, VarD<?>> setupFunctionParametersIteratively(
		List<ActualParameterContext> actualParamCtxList,
		FunctionSignature signature
	) {
		var formalParamsArray = signature.getParametersList();
		int formalParamsCount = formalParamsArray.size();
		int actualParamsCount = actualParamCtxList.size();
		if (actualParamsCount > formalParamsCount) {
			throw new GioviException(
				"Expecting at most " + formalParamsCount + " but instead got " + actualParamsCount);
		}
		var params = new LinkedHashMap<String, VarD<?>>();
		for (int i = 0; i < formalParamsCount; i++) {
			var formalParam = formalParamsArray.get(i);
			var formalParamIdentifier = formalParam.identifier();
			if (formalParamIdentifier.equals("self")) {
				continue;
			}

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
					params.put(formalParamIdentifier, actualParam);
				}
				break;
			}

			var actualParam = formalParam.spawn();
			var actualParamExpression = visit(actualParamCtxList.get(i).expression());

			// TODO: eventually remove it and let the redirection handle the default
			if (actualParamExpression instanceof GioviDefault) { // TODO: use GioviPlaceholder
				params.put(formalParamIdentifier, actualParam);
				continue;
			}

			actualParam.setValueWithRedirection(actualParamExpression);
			params.put(formalParamIdentifier, actualParam);
		}
		return params;
	}

	/**
	 * Visit function call named parameters and declare actual parameters
	 * @param actualParamCtxList Function call context
	 * @param signature Function signature
	 */
	private LinkedHashMap<String, VarD<?>> setupNamedFunctionParameters(
		List<ActualParameterContext> actualParamCtxList,
		FunctionSignature signature
	) {

		// cache actual params contexts mapped by their invocation label
		var cachedCtxs = new LinkedHashMap<String, ActualParameterContext>();
		for (var actualParamCtx : actualParamCtxList) {
			cachedCtxs.put(actualParamCtx.label.getText(), actualParamCtx);
		}

		var params = new LinkedHashMap<String, VarD<?>>();
		var undeclaredParameters = new LinkedHashMap<>(signature.parameters().byLabel());
		cachedCtxs.forEach((actualParamLabel, actualParamCtx) -> {
			if (!signature.hasParameterLabeled(actualParamLabel)) {
				throw new GioviException(
					"Unknown parameter labeled " + actualParamLabel + " in " + signature);
			}
			if (!undeclaredParameters.containsKey(actualParamLabel)) {
				throw new GioviException("Parameter " + actualParamLabel + " already defined");
			}
			var formalParam = signature.getParameterLabeled(actualParamLabel);
			var formalParamIdentifier = formalParam.identifier();
			var actualParam = formalParam.spawn();

			var actualParamExpression = visit(actualParamCtx.expression());
			if (actualParamExpression instanceof GioviDefault) { // TODO: use GioviPlaceholder
				params.put(formalParamIdentifier, actualParam);
			} else {
				actualParam.setValueWithRedirection(actualParamExpression);
				params.put(formalParamIdentifier, actualParam);
			}
			undeclaredParameters.remove(formalParam.label());
		});

		// no more actual params found, expecting remaining formal parameters to be optional
		undeclaredParameters.forEach((formalParamIdentifier, formalParam) -> {
			if (!formalParam.isOptional()) {
				throw new GioviException(
					"Required parameter not specified: " + formalParamIdentifier);
			}
			params.put(formalParam.identifier(), formalParam.spawn());
		});
		return params;
	}

	/**
	 * Actually perform function invocation by visiting its body
	 * @param func function to invocate
	 * @return returned value or GioviVoid
	 */
	private GioviValue<?> doInvoke(GFunction func) {
		if (func.bodyCtx() == null) {
			// returning returnType default value
			return func.signature().returnType().spawn();
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
					return returnType.widen(returnedValue);
				} catch (NonWideneable ex) {
					throw new GioviException(ex, "Wrong return type, expecting " + returnType
						+ " but got " + returnedValue.getGioviType());
				}
			}
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

		var fields = new LinkedHashMap<String, RecordFieldSignature>();
		String typename = ctx.typename.getText();

		if (ctx.parentTypename != null) {
			String parentTypename = ctx.parentTypename.getText();
			var parentSignature = getCurrentScope()
				.getStructSignature(parentTypename)
				.orElseThrow(() -> new GioviException(
					"Can't inherit from " + parentTypename,
					new TypeNotFound(parentTypename)));
			for (var inheritedField : parentSignature.fields().values()) {
				fields.put(inheritedField.identifier(), inheritedField);
			}
		}

		for (var fieldCtx : ctx.structBodyDefinition().structFieldDefinition()) {
			var field = (RecordFieldSignature) visit(fieldCtx);
			if (fields.containsKey(field.identifier())) {
				var inheritedField = fields.get(field.identifier());
				if (!inheritedField.type().equals(field.type())) {
					throw new GioviException(
						"Can't override field " + field.identifier() + " with type " + field.type()
							+ " because inherited with type " + inheritedField.type());
				}
			}
			fields.put(field.identifier(), field);
			// TODO: maybe add warning about unimplemented recursive structs
		}

		var constants = new HashMap<String, GioviValue<?>>();
		var functions = new HashMap<String, GioviFunction>();
		// TODO: double check that we don't lose constants and functions definitions somewhere else
		var definition = new StructSignature(
			typename,
			fields,
			constants,
			functions);
		getCurrentScope().define(definition);

		// TODO: constants and functions referring to the type we're currently
		// declaring won't have the same constants and functions
		// Shall we allow cyclic references?
		// Or maybe move constants and functions outside of struct?
		for (var constCtx : ctx.structBodyDefinition().constantDeclaration()) {
			var constant = spawnType(constCtx.type(), null);
			constant.setValueWithRedirection(visit(constCtx.expression()));
			constants.put(
				constCtx.identifier().getText(),
				constant.value());
		}

		for (var funcCtx : ctx.structBodyDefinition().functionDeclaration()) {
			var signature = (FunctionSignature) visit(funcCtx.functionSignature());
			functions.put(
				signature.identifier(),
				new GioviFunction(new GFunction(signature, funcCtx.body)));
		}

		// TODO: eventually replace with an incomplete type (GioviTypeSelf or GioviTypeVoid)
		var selfType = new GioviTypeStruct(definition);
		for (var funcCtx : ctx.structBodyDefinition().selfFunctionDeclaration()) {
			var signatureCtx = funcCtx.selfFunctionSignature();

			String identifier = signatureCtx.name.getText();

			var parameters = (FormalParameters) visit(signatureCtx.formalParameters());
			if (funcCtx.selfFunctionSignature().self != null) {
				parameters.parameters().putFirst("self", new FormalParameter("self", selfType));
			}

			GioviType<?> returnType;
			if (signatureCtx.returnType != null) {
				returnType = visit(signatureCtx.returnType).asGioviType();
			} else if (signatureCtx.returnSelf != null) {
				returnType = selfType;
			} else {
				returnType = new GioviTypeVoid();
			}

			var signature = new FunctionSignature(identifier, parameters, returnType);
			functions.put(identifier, new GioviFunction(new GFunction(signature, funcCtx.body)));
		}

		return null;
	}

	@Override
	public GioviParserVisitResult visitStructFieldDefinition(StructFieldDefinitionContext ctx) {
		String identifier = ctx.identifier().getText();
		var type = visit(ctx.type()).asGioviType();
		GioviValue<?> defaultValue;
		try {
			defaultValue = ctx.defaultDefinition() == null
				? type.spawn()
				: type.widen(visit(ctx.defaultDefinition()).asGioviValue());
		} catch (NonWideneable e) {
			throw new GioviException(e, "Incompatible default definition");
		}
		return new RecordFieldSignature(identifier, type, defaultValue);
	}

	@Override
	public GioviParserVisitResult visitStructCreation(StructCreationContext ctx) {
		StructSignature signature;
		if (ctx.typeQualified() != null) {
			signature = ((GioviTypeStruct) visit(ctx.typeQualified())).signature();
		} else {
			String typename = ctx.identifier().getText();
			signature = getCurrentScope()
				.getStructSignature(typename)
				.orElseThrow(() -> new GioviException(
					"Can't create struct " + typename, new TypeNotFound(typename)));
		}
		var struct = signature.spawn();
		if (ctx.tupleCreation() != null) {
			initStruct(struct, ctx.tupleCreation());
		} else {
			initStruct(struct, ctx.recordCreation());
		}
		return new GioviStruct(signature, struct);
	}

	private void initStruct(Struct struct, RecordCreationContext fieldsCtx) {
		for (var fieldCtx : fieldsCtx.recordEntry()) {
			String fieldIdentifier = fieldCtx.identifier().getText();
			var fieldValue = visit(fieldCtx.expression());
			struct.getField(fieldIdentifier).orElseThrow(() -> new NoSuchField(fieldIdentifier))
				.setValueWithRedirection(fieldValue);
		}
	}

	private void initStruct(Struct struct, TupleCreationContext ctx) {
		List<ExpressionContext> expressions = ctx.expression();
		if (expressions == null || expressions.isEmpty()) {
			return;
		}
		int expressionsCount = expressions.size();
		if (expressionsCount != struct.getFieldsCount()) {
			throw new GioviException(
				"Wrong fields count. Expecting " + struct.getFieldsCount()
					+ " but instead got " + expressionsCount);
		}
		for (int i = 0; i < expressionsCount; i++) {
			final int j = i;
			var fieldValue = visit(expressions.get(i));
			struct.getField(i).orElseThrow(() -> new NoSuchField(j))
				.setValueWithRedirection(fieldValue);
		}
	}

	@Override
	public GioviParserVisitResult visitTupleCreation(TupleCreationContext ctx) {
		int size = ctx.expression().size();
		var fields = new ArrayList<VarD<?>>(size);
		var fieldsSignature = new ArrayList<TupleFieldSignature>(size);
		for (var expressionCtx : ctx.expression()) {
			var fieldValue = visit(expressionCtx).unwrapAsGioviValue();
			var fieldType = fieldValue.getGioviType();
			fields.add(new VarD<>(fieldValue, fieldType.spawn()));
			fieldsSignature.add(new TupleFieldSignature(fieldType));
		}
		return new GioviTuple(
			new TupleSignature(fieldsSignature),
			new Tuple(fields));

	}

	@Override
	public GioviParserVisitResult visitRecordCreation(RecordCreationContext ctx) {
		int size = ctx.recordEntry().size();
		var fields = new LinkedHashMap<String, VarD<?>>(size);
		var fieldsSignature = new LinkedHashMap<String, RecordFieldSignature>(size);
		for (var entryCtx : ctx.recordEntry()) {
			var fieldValue = visit(entryCtx.expression()).unwrapAsGioviValue();
			var fieldType = fieldValue.getGioviType();
			String identifier = entryCtx.identifier().getText();
			fields.put(identifier, new VarD<>(fieldValue, fieldType.spawn()));
			fieldsSignature.put(identifier, new RecordFieldSignature(identifier, fieldType));
		}
		return new GioviRecord(
			new RecordSignature(fieldsSignature),
			new Record(fields));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Type

	@Override
	public GioviParserVisitResult visitType(TypeContext ctx) {
		if (ctx.identifier() != null) {
			String typename = ctx.identifier().getText();
			return getCurrentScope()
				.getType(typename)
				.orElseThrow(() -> new TypeNotFound(typename));
		}
		return visitChildren(ctx);
	}

	@Override
	public GioviParserVisitResult visitTypeQualified(TypeQualifiedContext ctx) {
		Module parentModule = ROOT_MODULE;
		for (int i = 0; i < ctx.identifier().size() - 1; i++) { // NOTE: last identifier is the typename
			var moduleIdentifierCtx = ctx.identifier(i);
			String moduleIdentifier = moduleIdentifierCtx.getText();
			parentModule = parentModule.scope().getModule(moduleIdentifier)
				.orElseThrow(
					() -> new GioviException("Can't find find module " + moduleIdentifier));
		}
		String typename = ctx.typename.getText();
		// TODO: should we allow primitive types too? giovi.Int, giovi.Range, giovi.Tuple
		return new GioviTypeStruct(parentModule.scope()
			.getStructSignature(typename)
			.orElseThrow(() -> new TypeNotFound(typename)));
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
		var functionSignature = (FunctionSignature) visit(ctx.anonFunctionSignature());
		return new GioviTypeFunction(functionSignature);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Literals
	@Override
	public GioviParserVisitResult visitReservedIdentifier(ReservedIdentifierContext ctx) {
		return switch (ctx.getText()) {
			case "_in" -> new GioviText(scanner.nextLine());
			default -> throw new GioviException("Unkown reserved identifier " + ctx.getText());
		};
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

	@Override
	public GioviParserVisitResult visitLiteralSelf(LiteralSelfContext ctx) {
		return getCurrentScope().getVariable("self")
			.orElseThrow(() -> new GioviException("Variable self not defined"));
	}

}
