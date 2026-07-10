package it.simonvic.giovi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import giovi.*;
import giovi.Module;
import giovi.Module.InnerModule;
import giovi.Module.NativeModule;
import giovi.lang.*;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.delegates.ForStatementDelegate;
import it.simonvic.giovi.delegates.ImportStatementDelegate;
import it.simonvic.giovi.delegates.MultiIfStatementDelegate;
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

	private static NativeModule scanGioviModule() throws URISyntaxException, IOException {
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
					return Class.forName(
						pathName.replace(".class", "")
							.replaceFirst(".*target/classes/", "") // TODO: lol hack
							.replace("[$]", ".") // nested classes
							.replace("/", ".")
					);
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

	public static final NativeModule ROOT_MODULE = new NativeModule("");
	private static final NativeModule GIOVI_MODULE;

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

	protected void setCurrentScope(Scope scope) {
		currentScope = scope;
	}

	protected void enterScope() {
		setCurrentScope(new Scope(getCurrentScope()));
	}

	protected void enterFunctionScope() {
		setCurrentScope(new FunctionScope(getCurrentScope()));
	}

	protected void enterLoopScope() {
		setCurrentScope(new LoopScope(getCurrentScope()));
	}

	protected void exitScope() {
		setCurrentScope(getCurrentScope().getParent());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// STATEMENTS

	@Override
	public GioviParserVisitResult visitImportStatement(ImportStatementContext ctx) {
		return new ImportStatementDelegate(this).visit(ctx);
	}

	@Override
	public GioviParserVisitResult visitModuleDeclaration(ModuleDeclarationContext ctx) {
		// FIXME: cycle
		String moduleIdentifier = ctx.identifier().getText();
		var module = new InnerModule(this.module, moduleIdentifier, ctx.body);
		getCurrentScope().declare(module);
		scanInnerModules(module, true);
		return module;
	}

	public static void scanInnerModules(InnerModule parentModule, boolean recurse) {
		var innerModulesCtx = parentModule.bodyCtx().statement().stream()
			.filter(statement -> statement.moduleDeclaration() != null)
			.map(statement -> statement.moduleDeclaration())
			.toList();
		for (var innerModuleDeclarationCtx : innerModulesCtx) {
			var innerModule = new InnerModule(
				parentModule,
				innerModuleDeclarationCtx.identifier().getText(),
				innerModuleDeclarationCtx.body
			);
			parentModule.scope().declare(innerModule);
			if (recurse) {
				scanInnerModules(innerModule, recurse);
			}
		}
	}

	@Override
	public GioviParserVisitResult visitAliasStatement(AliasStatementContext ctx) {
		String alias = ctx.alias.getText();
		var expression = visit(ctx.expression());
		var scope = getCurrentScope();
		switch (expression) {
			case GioviFunction function -> scope.declare(alias, function.value());
			case GioviTypeStruct typeStruct -> scope.define(alias, typeStruct);
			// TODO: this could create some funny behavior (e.g. with struct fields)
			// case VarD<?> variable -> scope.declare(alias, variable);
			case GioviValue<?> constant -> scope.declareConstant(alias, constant);
			case Module module -> scope.declare(alias, module);
			default -> throw new GioviException("Can't create alias for " + expression);
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitIfStatement(IfStatementContext ctx) {
		enterScope();
		if (visit(ctx.expression()).unwrapAsGioviValue().asNativeBool()) {
			visit(ctx.consequence);
			exitScope();
			return null;
		}
		exitScope();
		if (ctx.alternative != null) {
			enterScope();
			visit(ctx.alternative);
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
	public GioviParserVisitResult visitMultiIfStatementSubjected(
		MultiIfStatementSubjectedContext ctx
	) {
		var subject = visit(ctx.subject);
		var matchedLabel = MultiIfStatementDelegate.evaluate(this, ctx.multiIfBody(), subject);
		if (matchedLabel.isPresent()) {
			enterScope();
			visit(matchedLabel.orElseThrow().consequence);
			exitScope();
		} else if (ctx.alternative != null) {
			enterScope();
			visit(ctx.alternative);
			exitScope();
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitMultiIfStatementSubjectless(
		MultiIfStatementSubjectlessContext ctx
	) {
		// TODO: enterScope here so that `is` statement can declare
		for (var conditionCtx : ctx.subjectlessMultiIfBody().subjectlessMultiIfCondition()) {
			if (visit(conditionCtx.condition).asNativeBool()) {
				if (conditionCtx.guard == null || visit(conditionCtx.guard).asNativeBool()) {
					enterScope();
					visit(conditionCtx.consequence);
					exitScope();
					return null;
				}
			}
		}
		if (ctx.alternative != null) {
			enterScope();
			visit(ctx.alternative);
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

	@Override
	public GioviParserVisitResult visitForStatement(ForStatementContext ctx) {
		return ForStatementDelegate.doVisit(this, ctx);
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
		GioviValue<?> constant;
		try {
			constant = doVisitConstant(ctx);
		} catch (NonWideneable e) {
			throw new GioviException(e, "Incompatible constant type");
		}
		getCurrentScope().declareConstant(ctx.identifier().getText(), constant);
		// FIXME: copy! Otherwise we might mutate some other variable (e.g. FOO.someField <- 69)
		return constant;
	}

	private GioviValue<?> doVisitConstant(ConstantDeclarationContext ctx) throws NonWideneable {
		var constant = visit(ctx.expression()).unwrapAsGioviValue();
		if (ctx.type() != null) {
			var type = visit(ctx.type()).asGioviType();
			constant = constant.widenTo(type);
		}
		return constant;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToIdentifier(RedirectionToIdentifierContext ctx) {
		VisitorUtils.redirection(
			visit(ctx.content),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.dest)
		);
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToIdentifierRTL(
		RedirectionToIdentifierRTLContext ctx
	) {
		VisitorUtils.redirection(
			visit(ctx.content),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.dest)
		);
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToExpression(RedirectionToExpressionContext ctx) {
		VisitorUtils.redirection(
			visit(ctx.content),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.dest)
		);
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToExpressionRTL(
		RedirectionToExpressionRTLContext ctx
	) {
		VisitorUtils.redirection(
			visit(ctx.content),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.dest)
		);
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToReservedIdentifier(
		RedirectionToReservedIdentifierContext ctx
	) {
		VisitorUtils.redirectionToPrimitive(
			visit(ctx.content),
			GioviToken.of(ctx.bop.getText()),
			ctx.reservedIdentifier().getText()
		);
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToReservedIdentifierRTL(
		RedirectionToReservedIdentifierRTLContext ctx
	) {
		VisitorUtils.redirectionToPrimitive(
			visit(ctx.content),
			GioviToken.of(ctx.bop.getText()),
			ctx.reservedIdentifier().getText()
		);
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToPattern(RedirectionToPatternContext ctx) {
		VisitorUtils.redirectionToPattern(
			this,
			visit(ctx.content),
			GioviToken.of(ctx.bop.getText()),
			ctx.dest
		);
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToPatternRTL(RedirectionToPatternRTLContext ctx) {
		VisitorUtils.redirectionToPattern(
			this,
			visit(ctx.content),
			GioviToken.of(ctx.bop.getText()),
			ctx.dest
		);
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
	public GioviParserVisitResult visitExpressionDotRef(ExpressionDotRefContext ctx) {
		return visit(ctx.expression())
			.dotRef(new MemberKey(ctx.identifier().getText()));
	}

	@Override
	public GioviParserVisitResult visitReferenceable(ReferenceableContext ctx) {
		if (ctx.anonFunction() != null) {
			return visit(ctx.anonFunction());
		}
		String identifier = ctx.identifier().getText();
		Function function = getCurrentScope()
			.getFunction(identifier)
			.orElseThrow(
				() -> new GioviException(
					"Can't reference non-existing function " + identifier
				)
			);
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
	public GioviParserVisitResult visitExpressionBitwiseBool(ExpressionBitwiseBoolContext ctx) {
		return VisitorUtils.bitwiseBool(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1))
		);
	}

	@Override
	public GioviParserVisitResult visitExpressionBitwiseNot(ExpressionBitwiseNotContext ctx) {
		return VisitorUtils.bitwiseNot(visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitExpressionBitwiseShift(ExpressionBitwiseShiftContext ctx) {
		return VisitorUtils.bitwiseShift(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1))
		);
	}

	@Override
	public GioviParserVisitResult visitExpressionPrefixed(ExpressionPrefixedContext ctx) {
		return VisitorUtils.prefixed(
			GioviToken.of(ctx.prefix.getText()),
			visit(ctx.expression())
		);
	}

	@Override
	public GioviParserVisitResult visitExpressionAdditive(ExpressionAdditiveContext ctx) {
		return VisitorUtils.additive(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1))
		);
	}

	@Override
	public GioviParserVisitResult visitExpressionMultiplicative(
		ExpressionMultiplicativeContext ctx
	) {
		return VisitorUtils.multiplicative(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1))
		);
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
			ctx.invocation().actualParameters()
		);
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
			visit(ctx.expression(1))
		);
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
			() -> visit(ctx.expression(1))
		);
	}

	@Override
	public GioviParserVisitResult visitExpressionEquality(ExpressionEqualityContext ctx) {
		return VisitorUtils.equality(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1))
		);
	}

	@Override
	public GioviParserVisitResult visitExpressionContainment(ExpressionContainmentContext ctx) {
		var containment = VisitorUtils.containment(
			visit(ctx.expression(0)),
			GioviToken.of(ctx.bop.getText()),
			visit(ctx.expression(1))
		);
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
	public GioviParserVisitResult visitExpressionPatternMatching(
		ExpressionPatternMatchingContext ctx
	) {
		var value = visit(ctx.expression()).unwrapAsGioviValue();
		boolean matched;
		// TODO: clean up this mess. Make deconstruct() return temporary scope
		var targetScope = new Scope();
		try {
			VisitorUtils.deconstruct(this, targetScope, value, ctx.pattern());
			matched = true;
		} catch (Exception e) {
			matched = false;
		}
		if (ctx.NOT() != null) {
			matched = !matched;
		} else if (matched) {
			getCurrentScope().merge(targetScope);
		}
		return GioviBool.of(matched);
	}

	@Override
	public GioviParserVisitResult visitExpressionTypeCheck(ExpressionTypeCheckContext ctx) {
		var value = visit(ctx.expression()).unwrapAsGioviValue();
		var type = visit(ctx.type()).asGioviType();
		boolean equals = value.getGioviType().equals(type);
		if (ctx.NOT() != null) {
			return GioviBool.of(!equals);
		}
		return GioviBool.of(equals);
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
			.orElseThrow(
				() -> new GioviException(
					"No variable, constant, function, type or module found with name " + identifier
				)
			);
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
			ctx.step == null ? null : visit(ctx.step)
		);
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
				case Var<?> v -> entries.add(new Var<>(v.unwrap().copy()));
				default -> throw new GioviException(
					"Incompatible type for list creation: " + entry.getClass()
				);
			}
		}
		var entryType = GioviTypes.inferGioviType(entries);
		return new GioviList(new GioviTypeList(entryType), entries);
	}

	@Override
	public GioviParserVisitResult visitMapCreation(MapCreationContext ctx) {
		var map = new LinkedHashMap<Var<?>, Var<?>>();
		if (ctx.mapEntries() == null) {
			return new GioviMap(new GioviTypeMap(GioviType.VOID, GioviType.VOID), map);
		}
		for (var entryCtx : ctx.mapEntries().mapEntry()) {
			var key = visit(entryCtx.mapKey).unwrapAsGioviValue();
			var element = visit(entryCtx.mapValue).unwrapAsGioviValue();
			map.put(new Var<>(key), new Var<>(element));
		}
		var keyType = GioviTypes.inferGioviType(map.keySet());
		var elementType = GioviTypes.inferGioviType(map.values());
		return new GioviMap(new GioviTypeMap(keyType, elementType), map);
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
			: GioviType.VOID;

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
		LinkedHashMap<String, VarD<?>> actualParams;
		try {
			actualParams = setupFunctionParameters(
				parametersCtx,
				function.signature()
			);
		} catch (NonWideneable e) {
			throw new GioviException(e, "Invokation error");
		}
		if (function.signature().hasParameter("self")) {
			// TODO: passing by reference yields much better performance, but
			// it's different from other params getting passed by value.
			// Clarify requirements
			// TODO: self can be null when invoking a self function via alias. Eventually spawn self
			// type default value (once the self type wraps the struct type)
			actualParams.putFirst("self", new VarD<>(instance.unwrapAsGioviValue()));
		}
		var returnValue = switch (function) {
			case JFunction jFunction -> {
				var args = new GioviValue<?>[actualParams.size()];
				int i = 0;
				for (var actualParam : actualParams.entrySet()) {
					args[i++] = actualParam.getValue().unwrap();
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
	 * @throws NonWideneable
	 */
	private LinkedHashMap<String, VarD<?>> setupFunctionParameters(
		ActualParametersContext actualParamsContext,
		FunctionSignature signature
	) throws NonWideneable {
		// NOTE: if parameters are either positional or named depends on the first callParameter
		// If no parameters have been specified, positional approach is used
		List<ActualParameterContext> actualParameters = new ArrayList<>(
			actualParamsContext.actualParameter()
		);
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
	 * @throws NonWideneable
	 */
	private LinkedHashMap<String, VarD<?>> setupFunctionParametersIteratively(
		List<ActualParameterContext> actualParamCtxList,
		FunctionSignature signature
	) throws NonWideneable {
		var formalParamsArray = signature.getParametersList();
		int formalParamsCount = formalParamsArray.size();
		int actualParamsCount = actualParamCtxList.size();
		if (actualParamsCount > formalParamsCount) {
			throw new GioviException(
				"Expecting at most " + formalParamsCount + " but instead got " + actualParamsCount
			);
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
							"Required parameter not specified: " + formalParamIdentifier
						);
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

			actualParam.setValueWithWidening(actualParamExpression.unwrapAsGioviValue());
			params.put(formalParamIdentifier, actualParam);
		}
		return params;
	}

	/**
	 * Visit function call named parameters and declare actual parameters
	 * @param actualParamCtxList Function call context
	 * @param signature Function signature
	 * @throws NonWideneable
	 */
	private LinkedHashMap<String, VarD<?>> setupNamedFunctionParameters(
		List<ActualParameterContext> actualParamCtxList,
		FunctionSignature signature
	) throws NonWideneable {

		// cache actual params contexts mapped by their invocation label
		var cachedCtxs = new LinkedHashMap<String, ActualParameterContext>();
		for (var actualParamCtx : actualParamCtxList) {
			cachedCtxs.put(actualParamCtx.label.getText(), actualParamCtx);
		}

		var params = new LinkedHashMap<String, VarD<?>>();
		var undeclaredParameters = new LinkedHashMap<>(signature.parameters().byLabel());
		for (var cachedCtxEntry : cachedCtxs.entrySet()) {
			var actualParamLabel = cachedCtxEntry.getKey();
			var actualParamCtx = cachedCtxEntry.getValue();
			if (!signature.hasParameterLabeled(actualParamLabel)) {
				throw new GioviException(
					"Unknown parameter labeled " + actualParamLabel + " in " + signature
				);
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
				actualParam.setValueWithWidening(actualParamExpression.unwrapAsGioviValue());
				params.put(formalParamIdentifier, actualParam);
			}
			undeclaredParameters.remove(formalParam.label());
		}

		// no more actual params found, expecting remaining formal parameters to be optional
		undeclaredParameters.forEach((formalParamIdentifier, formalParam) -> {
			if (!formalParam.isOptional()) {
				throw new GioviException(
					"Required parameter not specified: " + formalParamIdentifier
				);
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
				if (returnType instanceof GioviTypeSelf) {
					// HACK: is there a more elegant way of retrieving self type?
					returnType = getCurrentScope().getVariable("self").orElseThrow().getGioviType();
				}
				if (!(returnedValue instanceof GioviVoid)
					&& returnType instanceof GioviTypeVoid) {
					throw new GioviException(
						"Can't return " + returnedValue + " in a function with no return type"
					);
				}
				try {
					return returnType.widen(returnedValue);
				} catch (NonWideneable ex) {
					throw new GioviException(
						ex, "Wrong return type, expecting " + returnType
							+ " but got " + returnedValue.getGioviType()
					);
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

		if (ctx.parentType != null) {
			var parentType = visit(ctx.parentType);
			if (parentType instanceof GioviTypeStruct parentTypeStruct) {
				for (var inheritedField : parentTypeStruct.signature().fields().values()) {
					fields.put(inheritedField.identifier(), inheritedField);
				}
			} else {
				throw new GioviException("Can't inherit from " + parentType);
			}
		}

		for (var fieldCtx : ctx.structBodyDefinition().fieldDeclaration()) {
			var field = (RecordFieldSignature) visit(fieldCtx);
			if (fields.containsKey(field.identifier())) {
				var inheritedField = fields.get(field.identifier());
				if (!inheritedField.type().equals(field.type())) {
					throw new GioviException(
						"Can't override field " + field.identifier() + " with type " + field.type()
							+ " because inherited with type " + inheritedField.type()
					);
				}
			}
			fields.put(field.identifier(), field);
		}

		// TODO: define before fields visit, but define it as larval type so all future references can be replaced by a Self
		var constants = new HashMap<String, GioviValue<?>>();
		var functions = new HashMap<String, GioviFunction>();
		// TODO: double check that we don't lose constants and functions definitions somewhere else
		var structSignature = new StructSignature(
			typename,
			fields,
			constants,
			functions
		);
		var structType = new GioviTypeStruct(structSignature);
		getCurrentScope().define(structType);

		for (var constCtx : ctx.structBodyDefinition().constantDeclaration()) {
			GioviValue<?> constant;
			try {
				constant = doVisitConstant(constCtx);
			} catch (NonWideneable e) {
				throw new GioviException(e, "Incompatible constant type");
			}
			constants.put(constCtx.identifier().getText(), constant);
		}

		for (var funcCtx : ctx.structBodyDefinition().functionDeclaration()) {
			var signature = (FunctionSignature) visit(funcCtx.functionSignature());
			functions.put(
				signature.identifier(),
				new GioviFunction(new GFunction(signature, funcCtx.body))
			);
		}

		for (var funcCtx : ctx.structBodyDefinition().selfFunctionDeclaration()) {
			var signatureCtx = funcCtx.selfFunctionSignature();

			String identifier = signatureCtx.name.getText();

			var parameters = (FormalParameters) visit(signatureCtx.formalParameters());
			if (funcCtx.selfFunctionSignature().self != null) {
				parameters.parameters()
					.putFirst("self", new FormalParameter("self", GioviType.SELF));
			}

			GioviType<?> returnType;
			if (signatureCtx.returnType != null) {
				returnType = visit(signatureCtx.returnType).asGioviType();
			} else if (signatureCtx.returnSelf != null) {
				returnType = GioviType.SELF;
			} else {
				returnType = GioviType.VOID;
			}

			var signature = new FunctionSignature(identifier, parameters, returnType);
			functions.put(identifier, new GioviFunction(new GFunction(signature, funcCtx.body)));
		}

		return null;
	}

	@Override
	public GioviParserVisitResult visitFieldDeclaration(FieldDeclarationContext ctx) {
		String identifier = ctx.identifier().getText();
		if (ctx.SELF() != null) {
			return new RecordFieldSignature(identifier, GioviType.SELF);
		}
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
		var type = visit(ctx.typeQualified());
		if (!(type instanceof GioviTypeStruct typeStruct)) {
			throw new GioviException("Can't create struct of non-struct type " + type);
		}
		var struct = typeStruct.signature().spawn();
		try {
			if (ctx.tupleCreationBody() != null) {
				initStruct(typeStruct, struct, ctx.tupleCreationBody());
			} else {
				initStruct(typeStruct, struct, ctx.recordCreationBody());
			}
		} catch (NonWideneable e) {
			throw new GioviException(e, "Can't create struct");
		}
		return new GioviStruct(typeStruct, struct);
	}

	private void initStruct(
		GioviTypeStruct structType,
		GStruct struct,
		RecordCreationBodyContext fieldsCtx
	) throws NonWideneable {
		for (var fieldCtx : fieldsCtx.recordEntry()) {
			String fieldIdentifier = fieldCtx.identifier().getText();
			var newFieldValue = visit(fieldCtx.expression());
			var field = struct.getField(fieldIdentifier)
				.orElseThrow(() -> new NoSuchField(fieldIdentifier));
			doInitStructField(structType, field, newFieldValue);
		}
	}

	private void initStruct(
		GioviTypeStruct structType,
		GStruct struct,
		TupleCreationBodyContext ctx
	) throws NonWideneable {
		List<ExpressionContext> expressions = ctx.expression();
		if (expressions == null || expressions.isEmpty()) {
			return;
		}
		int expressionsCount = expressions.size();
		if (expressionsCount != struct.getFieldsCount()) {
			throw new GioviException(
				"Wrong fields count. Expecting " + struct.getFieldsCount()
					+ " but instead got " + expressionsCount
			);
		}
		for (int i = 0; i < expressionsCount; i++) {
			final int j = i;
			var newFieldValue = visit(expressions.get(i));
			var field = struct.getField(i).orElseThrow(() -> new NoSuchField(j));
			doInitStructField(structType, field, newFieldValue);
		}
	}

	private void doInitStructField(
		GioviTypeStruct structType,
		VarD<?> field,
		GioviParserVisitResult newFieldValue
	) throws NonWideneable {
		if (field.unwrap() instanceof GioviSelf) {
			// initialize recursive field
			field.setValueUnsafe(structType.spawn());
		}
		if (newFieldValue instanceof GioviDefault) {
			field.setValueWithRedirection(newFieldValue);
		} else {
			field.setValueWithWidening(newFieldValue.unwrapAsGioviValue());
		}
	}

	@Override
	public GioviParserVisitResult visitTupleCreationBody(TupleCreationBodyContext ctx) {
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
			new GioviTypeTuple(new TupleSignature(fieldsSignature)),
			new GTuple(fields)
		);

	}

	@Override
	public GioviParserVisitResult visitRecordCreationBody(RecordCreationBodyContext ctx) {
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
			new GioviTypeRecord(new RecordSignature(fieldsSignature)),
			new GRecord(fields)
		);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Type

	@Override
	public GioviParserVisitResult visitExpressionTypeCreation(ExpressionTypeCreationContext ctx) {
		return visit(ctx.type()).asGioviType();
	}

	@Override
	public GioviParserVisitResult visitTypeQualified(TypeQualifiedContext ctx) {
		var scope = getCurrentScope();
		for (int i = 0; i < ctx.identifier().size() - 1; i++) { // NOTE: last identifier is the typename
			var moduleIdentifierCtx = ctx.identifier(i);
			String moduleIdentifier = moduleIdentifierCtx.getText();
			scope = scope.getModule(moduleIdentifier)
				.orElseThrow(() -> new GioviException("Can't find find module " + moduleIdentifier))
				.scope();
		}
		String typename = ctx.typename.getText();
		return scope.getType(typename)
			.orElseThrow(() -> new TypeNotFound(typename));
	}

	@Override
	public GioviParserVisitResult visitTypeTuple(TypeTupleContext ctx) {
		var fields = new ArrayList<TupleFieldSignature>();
		for (var fieldCtx : ctx.typeTupleEntry()) {
			fields.add((TupleFieldSignature) visit(fieldCtx));
		}
		var signature = new TupleSignature(fields);
		return new GioviTypeTuple(signature);
	}

	@Override
	public GioviParserVisitResult visitTypeTupleEntry(TypeTupleEntryContext ctx) {
		var type = visit(ctx.type()).asGioviType();
		return (ctx.defaultDefinition() == null)
			? new TupleFieldSignature(type)
			: new TupleFieldSignature(
				type,
				visit(ctx.defaultDefinition().expression()).asGioviValue()
			);
	}

	@Override
	public GioviParserVisitResult visitTypeRecord(TypeRecordContext ctx) {
		int fieldsCount = ctx.fieldDeclaration().size();
		var fields = new RecordFieldSignature[fieldsCount];
		for (int i = 0; i < fieldsCount; i++) {
			fields[i] = (RecordFieldSignature) visit(ctx.fieldDeclaration(i));
		}
		var signature = new RecordSignature(fields);
		return new GioviTypeRecord(signature);
	}

	@Override
	public GioviParserVisitResult visitTypeList(TypeListContext ctx) {
		var entryType = visit(ctx.type()).asGioviType();
		if (ctx.initSize == null) {
			return new GioviTypeList(entryType);
		}
		int initSize = switch (visit(ctx.initSize).unwrapAsGioviValue()) {
			case GioviInt i -> i.value();
			case GioviNatural i -> i.value().intValue();
			default -> throw new GioviException("Invalid list init size");
		};
		return new GioviTypeList(entryType, initSize);
	}

	@Override
	public GioviParserVisitResult visitTypeMap(TypeMapContext ctx) {
		return new GioviTypeMap(
			visit(ctx.mapKeyType).asGioviType(),
			visit(ctx.mapValueType).asGioviType()
		);
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
