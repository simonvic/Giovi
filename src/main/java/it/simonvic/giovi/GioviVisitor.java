package it.simonvic.giovi;

import java.util.*;

import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.delegates.SwitchStatementDelegate;
import it.simonvic.giovi.exceptions.*;
import it.simonvic.giovi.lang.*;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.lang.values.natives.Tuple;
import it.simonvic.giovi.lang.values.natives.Tuple.Named;
import it.simonvic.giovi.util.GioviTypes;
import it.simonvic.giovi.util.OperationEvaluator;
import it.simonvic.giovi.util.TextProcessor;

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

	// TODO: change return type and invoke appropriate method instead of generic visit() and casting

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
					subject.asGioviValue().castTo(visit(typeCheckCtx.type()).asGioviType()));
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
	public GioviParserVisitResult visitForStatement(ForStatementContext ctx) {
		var iterators = ctx.iterators().identifier();
		var iterated = visit(ctx.rangeOrExpression());
		var statementOrBlock = ctx.statementOrBlock();
		switch (iterated) {
			case Range<?> range -> forLoop(iterators, range, statementOrBlock);
			case GioviList list -> forLoop(iterators, list, statementOrBlock);
			case GioviMap map -> forLoop(iterators, map, statementOrBlock);
			default -> throw new OperatorNotSupported("for");
		}
		return null;
	}

	private void forLoop(
		List<IdentifierContext> iterators,
		Range<?> range,
		StatementOrBlockContext statementOrBlock) {

		// workaround
		List<GioviValue<?>> entries = new ArrayList<>(range.expandAsGioviValues());
		forLoop(iterators, new GioviList(entries), statementOrBlock);

		// TODO: what about skip and breaks?
		// if (iterators.size() != 1) {
		// 	throw new GioviException("Wrong number of iterators");
		// }
		// String iteratorName = iterators.get(0).getText();
		// range.iterate(value -> {
		// 	enterLoopScope();
		// 	getCurrentScope().declare(iteratorName, value);
		// 	visit(statementOrBlock);
		// 	exitScope();
		// });
	}


	private void forLoop(
		List<IdentifierContext> iterators,
		GioviList gioviList,
		StatementOrBlockContext statementOrBlock) {

		if (iterators.size() != 1)
			throw new GioviException("Wrong number of iterators");
		if (gioviList.unbox().isEmpty())
			return;

		for (var gioviValue : gioviList.unbox()) {

			enterLoopScope();

			// TODO: update when structs are implemented
			String iteratorName = iterators.get(0).getText();
			getCurrentScope().declare(iteratorName, gioviValue); // TODO: iterator should be by value?
			// GioviValue<?> iterator = getCurrentScope().getVariableValue(iteratorName);
			// iterator.box(gioviValue);

			visit(statementOrBlock);

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

	private void forLoop(
		List<IdentifierContext> iterators,
		GioviMap map,
		StatementOrBlockContext statementOrBlock) {

		if (iterators.size() != 2)
			throw new GioviException("Wrong number of iterators");
		if (map.unbox().isEmpty())
			return;

		for (var entry : map.unbox().entrySet()) {

			enterLoopScope();

			// TODO: update when structs are implemented
			String keyIteratorName = iterators.get(0).getText();
			String elementIteratorName = iterators.get(1).getText();

			// TODO: iterators should be by value?
			getCurrentScope().declare(keyIteratorName, entry.getKey());
			getCurrentScope().getVariableValue(keyIteratorName).box(entry.getKey());
			getCurrentScope().declare(elementIteratorName, entry.getValue());
			getCurrentScope().getVariableValue(elementIteratorName).box(entry.getValue());

			visit(statementOrBlock);

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
		// TODO: improve this. Declare variable with value already set
		String bop = ctx.bop.getText();
		var variable = visit(ctx.varDeclaration());
		switch (bop) {
			case "->" -> variable.redirection(visit(ctx.redirectionContent()));
			default -> throw new RedirectionException("Unknown redirection operator: " + bop);
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitVarDeclaration(VarDeclarationContext ctx) {
		var variable = spawnType(ctx.type(), Optional.ofNullable(ctx.defaultDefinition()));
		getCurrentScope().declare(ctx.identifier().getText(), variable);
		return variable;
	}

	@Override
	public GioviParserVisitResult visitConstantDeclaration(ConstantDeclarationContext ctx) {
		var constant = visit(ctx.expression()).asGioviValue();
		getCurrentScope().declareConstant(ctx.constantIdentifier().getText(), constant);
		return constant;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToIdentifier(RedirectionToIdentifierContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperator().getText(),
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
		OperationEvaluator.redirection(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperator().getText(),
			visit(ctx.expression()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToVarDeclarationRTL(
		RedirectionToVarDeclarationRTLContext ctx) {
		// TODO: improve this. Declare variable with value already set
		String bop = ctx.bop.getText();
		var variable = visit(ctx.varDeclaration());
		switch (bop) {
			case "<-" -> variable.redirection(visit(ctx.redirectionContent()));
			default -> throw new RedirectionException("Unknown redirection operator: " + bop);
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToIdentifierRTL(
		RedirectionToIdentifierRTLContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperatorRTL().getText(),
			visit(ctx.identifier()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToExpressionRTL(
		RedirectionToExpressionRTLContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperatorRTL().getText(),
			visit(ctx.expression()));
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToPrimitiveDestination(
		RedirectionToPrimitiveDestinationContext ctx) {
		OperationEvaluator.redirectionToPrimitive(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperator().getText(),
			ctx.primitiveDestination().getText());
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToPrimitiveDestinationRTL(
		RedirectionToPrimitiveDestinationRTLContext ctx) {
		OperationEvaluator.redirectionToPrimitive(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperatorRTL().getText(),
			ctx.primitiveDestination().getText());
		return null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Expressions

	@Override
	public GioviParserVisitResult visitExpressionParenthesized(ExpressionParenthesizedContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public GioviParserVisitResult visitExpressionPrefixed(ExpressionPrefixedContext ctx) {
		return OperationEvaluator.prefixed(
			ctx.prefix.getText(),
			visit(ctx.expression()));
	}

	@Override
	public GioviParserVisitResult visitExpressionAdditive(ExpressionAdditiveContext ctx) {
		return OperationEvaluator.additive(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionMultiplicative(
		ExpressionMultiplicativeContext ctx) {
		return OperationEvaluator.multiplicative(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
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
			return lhs.dot(new AccessKey(ctx.anyIdentifier().getText()));
		}
		String functionIdentifier = ctx.functionCall().identifier().getText();
		try {
			return lhs.dot(getCurrentScope().getFunctionSignature(functionIdentifier));
		} catch (FunctionNotDeclared ex) {
			throw new GioviException("Can't invoke function " + functionIdentifier, ex);
		}
	}

	@Override
	public GioviParserVisitResult visitExpressionPower(ExpressionPowerContext ctx) {
		return visit(ctx.expression(0)).power(visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionRelational(ExpressionRelationalContext ctx) {
		return OperationEvaluator.relational(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionNot(ExpressionNotContext ctx) {
		return visit(ctx.expression()).not();
	}

	@Override
	public GioviParserVisitResult visitExpressionBool(ExpressionBoolContext ctx) {
		return OperationEvaluator.bool(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionEquality(ExpressionEqualityContext ctx) {
		return OperationEvaluator.equality(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionContainment(ExpressionContainmentContext ctx) {
		return OperationEvaluator.containment(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviParserVisitResult visitExpressionCast(ExpressionCastContext ctx) {
		// TODO: add as operator in GioviObject
		return visit(ctx.expression()).asGioviValue().castTo(visit(ctx.type()).asGioviType());
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
		return new AccessKey(ctx.key.getText());
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
	public GioviParserVisitResult visitRangeOrExpression(RangeOrExpressionContext ctx) {
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

	@Override
	public GioviParserVisitResult visitListCreation(ListCreationContext ctx) {
		List<GioviValue<?>> entries = new ArrayList<>();
		for (ListEntryContext entryCtx : ctx.listEntries().listEntry()) {
			GioviParserVisitResult entry = visit(entryCtx);
			switch (entry) {
				case Range<?> range -> entries.addAll(range.expandAsGioviValues());
				case GioviValue<?> v -> entries.add(v);
				default -> throw new GioviException(
					"Incompatible type for list creation: " + entry.getClass());
			}
		}
		return new GioviList(entries);
	}

	@Override
	public GioviParserVisitResult visitMapCreation(MapCreationContext ctx) {
		LinkedHashMap<GioviValue<?>, GioviValue<?>> map = new LinkedHashMap<>();
		for (MapEntryContext entryCtx : ctx.mapEntries().mapEntry()) {
			map.put(visit(entryCtx.mapKey).asGioviValue(), visit(entryCtx.mapValue).asGioviValue());
		}
		return new GioviMap(map);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// FUNCTIONS

	@Override
	public GioviParserVisitResult visitFunctionDeclaration(FunctionDeclarationContext ctx) {
		GioviFunctionSignature signature;

		String identifier = ctx.identifier().getText();

		var returnType = ctx.type() == null
			? GioviTypes.of(GioviVoid.class)
			: visit(ctx.type()).asGioviType();

		if (ctx.formalParameters() == null) {
			signature = ctx.type() == null
				? new GioviFunctionSignature(identifier, ctx.body)
				: new GioviFunctionSignature(identifier, returnType, ctx.body);
		} else {
			var parameters = new LinkedHashMap<String, GioviFunctionSignatureParameter>();
			for (var formalParamCtx : ctx.formalParameters().formalParameter()) {
				var formalParameter = (GioviFunctionSignatureParameter) visit(formalParamCtx);
				parameters.put(formalParameter.identifier(), formalParameter);
			}
			signature = new GioviFunctionSignature(identifier, ctx.body, returnType, parameters);
		}

		getCurrentScope().declare(signature);
		return signature;
	}

	@Override
	public GioviParserVisitResult visitFormalParameter(FormalParameterContext ctx) {
		String identifier = ctx.identifier().getText();
		var type = visit(ctx.type()).asGioviType();
		if (ctx.defaultDefinition() == null) {
			return new GioviFunctionSignatureParameter(identifier, type);
		}
		var defaultExpression = visit(ctx.defaultDefinition().expression());
		if (defaultExpression instanceof GioviDefault) { // TODO use GioviPlaceholder (underscore) instead
			return new GioviFunctionSignatureParameter(identifier, type, type.spawn(), true);
		}
		var defaultValue = defaultExpression.asGioviValue();
		var defaultValueType = defaultValue.getGioviType();
		if (defaultValueType.equals(type)) {
			return new GioviFunctionSignatureParameter(identifier, type, defaultValue);
		}
		if (defaultValueType.canUpcast(type)) {
			return new GioviFunctionSignatureParameter(identifier, type, defaultValue.castTo(type));
		}
		throw new GioviException("Incompatible default value. Expecting a " + type
			+ " but instead got " + defaultValueType);
	}

	private GioviValue<?> spawnType(
		TypeContext typeCtx,
		Optional<DefaultDefinitionContext> defaultDefinitionCtx) {

		var type = visit(typeCtx).asGioviType();
		var value = type.spawn();

		if (defaultDefinitionCtx.isPresent()) {
			var defaultValue = visit(defaultDefinitionCtx.get()).asGioviValue();
			value.setDefaultValue(defaultValue);
			value.boxDefault();
		}
		return value;
	}

	@Override
	public GioviParserVisitResult visitFunctionCall(FunctionCallContext ctx) {
		GioviFunctionSignature signature;
		try {
			signature = getCurrentScope().getFunctionSignature(ctx.identifier().getText());
		} catch (FunctionNotDeclared e) {
			throw new GioviException("Can't call " + ctx.identifier().getText(), e);
		}
		enterFunctionScope();
		setupFunctionParameters(ctx, signature);
		var returnValue = callFunction(signature);
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
		GioviFunctionSignature signature) {

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
	 * @param ctx Function call context
	 * @param signature Function signature
	 */
	private void setupFunctionParametersIteratively(
		List<FunctionCallParameterContext> actualParamCtxList,
		GioviFunctionSignature signature) {
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
						formalParam.defaultValue());
				}
				break;
			}

			var actualParamExpression = visit(actualParamCtxList.get(i).expression());
			if (actualParamExpression instanceof GioviDefault) { // TODO: use GioviPlaceholder
				getCurrentScope().declare(
					formalParamIdentifier,
					formalParam.defaultValue());
				continue;
			}

			var actualParamValue = actualParamExpression.asGioviValue();
			var actualParamType = actualParamValue.getGioviType();
			var formalParamType = formalParam.type();
			if (actualParamType.equals(formalParamType)) {
				getCurrentScope().declare(
					formalParamIdentifier,
					actualParamValue);
			} else if (actualParamType.canUpcast(formalParamType)) {
				getCurrentScope().declare(
					formalParamIdentifier,
					actualParamValue.castTo(formalParamType));
			} else {
				throw new GioviException("Wrong parameter type. Expecting " + formalParamType
					+ " but instead got " + actualParamType);
			}
		}

	}

	/**
	 * Visit function call named parameters and declare actual parameters
	 * @param ctx Function call context
	 * @param signature Function signature
	 */
	private void setupNamedFunctionParameters(
		List<FunctionCallParameterContext> actualParamCtxList,
		GioviFunctionSignature signature) {

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
			var actualParamExpression = visit(actualParams
				.get(formalParamIdentifier)
				.expression());
			if (actualParamExpression instanceof GioviDefault) { // TODO: use GioviPlaceholder
				getCurrentScope().declare(formalParamIdentifier, formalParam.defaultValue());
			} else {
				var actualParamValue = actualParamExpression.asGioviValue();
				var actualParamType = actualParamValue.getGioviType();
				var formalParamType = formalParam.type();
				if (actualParamType.equals(formalParamType)) {
					getCurrentScope().declare(
						formalParamIdentifier,
						actualParamValue);
				} else if (actualParamType.canUpcast(formalParamType)) {
					getCurrentScope().declare(
						formalParamIdentifier,
						actualParamValue.castTo(formalParamType));
				} else {
					throw new GioviException("Wrong parameter type. Expecting "
						+ formalParamType + " but instead got " + actualParamType);
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
			getCurrentScope().declare(formalParamIdentifier, formalParam.defaultValue());
		});

	}

	/** Actually perform function invocation by visiting its body
	 * @param func function to invocate
	 * @return returned value or GioviVoid
	 */
	private GioviValue<?> callFunction(GioviFunctionSignature func) {
		for (StatementContext statement : func.body().statement()) {
			visit(statement);
			if (getCurrentScope().shouldReturn()) {
				var returnType = func.returnType();
				var returnedValue = getCurrentScope().getReturnValue();
				var returnedType = returnedValue.getGioviType();
				if (returnType.unbox() == GioviVoid.class
					&& returnedType.unbox() != GioviVoid.class) {
					throw new GioviException(
						"Can't return " + returnedValue + " in a function with no return type");
				}
				if (returnedType.equals(returnType)) {
					return returnedValue;
				}
				if (returnedType.canUpcast(returnType)) {
					return returnedValue.castTo(returnType);
				}
				throw new GioviException(
					"Wrong return type, expecting " + returnType + " but got " + returnedType);
			}
		}
		if (func.returnType().unbox() != GioviVoid.class) {
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
		String typename = ctx.structName.getText();

		var fieldDefinitions = new LinkedHashMap<String, GioviFieldDefinition>();
		if (ctx.parentName != null) {
			var parentDefinition = getDefinitionStruct(ctx.parentName.getText());
			fieldDefinitions.putAll(parentDefinition.fields());
		}

		for (var fieldCtx : ctx.structBodyDefinition().structFieldDefinition()) {
			String identifier = fieldCtx.identifier().getText();
			var fieldType = visit(fieldCtx.type()).asGioviType();
			GioviFieldDefinition fieldDefinition = fieldCtx.defaultDefinition() == null
				? fieldDefinition = new GioviFieldDefinition(identifier, fieldType)
				: new GioviFieldDefinition(identifier, fieldType,
					visit(fieldCtx.defaultDefinition()).asGioviValue());
			fieldDefinitions.put(identifier, fieldDefinition);
		}

		var definition = new GioviStructDefinition(typename, fieldDefinitions);
		getCurrentScope().define(definition);
		return definition;
	}

	@Override
	public GioviParserVisitResult visitStructCreation(StructCreationContext ctx) {
		var structFieldsCtx = ctx.structFields();
		return ctx.structType != null
			? createStruct(ctx.structType.getText(), structFieldsCtx)
			: createTuple(structFieldsCtx);
	}

	private GioviStruct createStruct(String typename, StructFieldsContext ctx) {
		var gioviStruct = getDefinitionStruct(typename).spawn();
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
			if (!gioviStruct.containsField(fieldIdentifier)) {
				throw new GioviException("Unexpected field " + fieldIdentifier + " for type "
					+ gioviStruct.getGioviType().getSimpleName());
			}
			gioviStruct.setField(
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
			gioviStruct.setField(i, visit(expressions.get(i)).asGioviValue());
		}
	}

	private GioviTuple createTuple(StructFieldsContext ctx) {
		if (ctx == null) {
			return new GioviTuple(new Tuple.Unnamed());
		}
		if (ctx.namedStructField().isEmpty()) {
			var struct = new Tuple.Unnamed();
			for (var expressionCtx : ctx.expression()) {
				struct.put(visit(expressionCtx).asGioviValue());
			}
			return new GioviTuple(struct);
		} else {
			var struct = new Named();
			for (var namedStructFieldCtx : ctx.namedStructField()) {
				struct.put(
					namedStructFieldCtx.identifier().getText(),
					visit(namedStructFieldCtx.expression()).asGioviValue());
			}
			return new GioviTuple(struct);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Type

	@Override
	public GioviParserVisitResult visitTypeIdentifier(TypeIdentifierContext ctx) {
		return switch (ctx.getText()) {
			case "Natural" -> GioviTypes.of(GioviNatural.class);
			case "Int" -> GioviTypes.of(GioviInt.class);
			case "Real" -> GioviTypes.of(GioviReal.class);
			case "Bool" -> GioviTypes.of(GioviBool.class);
			case "Text" -> GioviTypes.of(GioviText.class);
			default -> GioviTypes.of(getDefinitionStruct(ctx.getText()));
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

	private GioviStructDefinition getDefinitionStruct(String structType) {
		if (!getCurrentScope().isDefinedStruct(structType)) {
			throw new TypeNotFound(structType);
		}
		return getCurrentScope().getDefinitionStruct(structType);
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
		return new GioviDefault();
	}

}
