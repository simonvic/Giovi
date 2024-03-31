package it.simonvic.giovi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

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
	public GioviParserVisitResult visitForStatement(ForStatementContext ctx) {
		var iterator = ctx.iterator();
		var iterated = visit(ctx.iterable());
		var statementOrBlock = ctx.statementOrBlock();
		switch (iterated) {
			case Range<?> range -> {
				switch (iterator) {
					case IteratorUnaryContext i -> forLoop(i, range, statementOrBlock);
					default -> throw new GioviException("Unexpected iterator" + iterator.getText());
				}
			}
			case GioviList list -> {
				switch (iterator) {
					case IteratorUnaryContext i -> forLoop(i, list, statementOrBlock);
					case IteratorStructContext i -> {
						var entryType = list.getEntryType();
						if (entryType instanceof GioviTypeTuple
							|| entryType instanceof GioviTypeStruct) {
							forLoop(i, list, statementOrBlock);
						} else {
							throw new GioviException(
								"Can't deconstruct " + entryType + " into a tuple iterator");
						}
					}
					default -> throw new GioviException(
						"Can't iterate range with " + iterator.getText());
				}
			}
			case GioviMap map -> {
				switch (iterator) {
					case IteratorUnaryContext i -> forLoop(i, map, statementOrBlock);
					case IteratorMapContext i -> forLoop(i, map, statementOrBlock);
					default -> throw new GioviException("Unexpected iterator" + iterator.getText());
				}
			}
			default -> throw new OperatorNotSupported(GioviToken.FOR);
		}
		return null;
	}

	/**
	 * @param iteratorCtx
	 * @param range
	 * @param statementOrBlock
	 */
	private void forLoop(
		IteratorUnaryContext iteratorCtx,
		Range<?> range,
		StatementOrBlockContext statementOrBlock) {
		// TODO: workaround
		forLoop(iteratorCtx, range.expandAsGioviList(), statementOrBlock);
	}

	// TODO: iterators should be by value?

	/**
	 * Iterate over a list
	 * @param iteratorCtx
	 * @param gioviList
	 * @param statementOrBlockCtx
	 */
	private void forLoop(
		IteratorUnaryContext iteratorCtx,
		GioviList gioviList,
		StatementOrBlockContext statementOrBlockCtx) {
		for (var gioviValue : gioviList.unbox()) {
			enterLoopScope();
			getCurrentScope().declare(iteratorCtx.identifier().getText(), gioviValue);
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
	 * Iterate over a list of structs by deconstructing them
	 * @param iteratorCtx
	 * @param gioviList
	 * @param statementOrBlockCtx
	 */
	private void forLoop(
		IteratorStructContext iteratorCtx,
		GioviList gioviList,
		StatementOrBlockContext statementOrBlockCtx) {
		if (gioviList.unbox().isEmpty()) return;
		for (var gioviValue : gioviList.unbox()) {
			enterLoopScope();
			for (var fieldIdentifierCtx : iteratorCtx.identifier()) {
				if (fieldIdentifierCtx == iteratorCtx.struct) {
					getCurrentScope().declare(iteratorCtx.struct.getText(), gioviValue);
				} else {
					String fieldIdentifier = fieldIdentifierCtx.getText();
					if (!(gioviValue.unbox() instanceof TupleNamed tuple)) {
						throw new GioviException("Can't deconstruct " + gioviValue);
					}
					if (!tuple.containsField(fieldIdentifier)) {
						throw new GioviException("Can't deconstruct " + gioviValue
							+ " because a field " + fieldIdentifier + " doesn't exist");
					}
					getCurrentScope().declare(fieldIdentifier, tuple.getField(fieldIdentifier));
				}
			}
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
	 * Iterate over map keys
	 * @param iteratorCtx
	 * @param map
	 * @param statementOrBlockCtx
	 */
	private void forLoop(
		IteratorUnaryContext iteratorCtx,
		GioviMap map,
		StatementOrBlockContext statementOrBlockCtx) {
		for (var key : map.unbox().keySet()) {
			enterLoopScope();
			getCurrentScope().declare(iteratorCtx.identifier().getText(), key.copy());
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
	 * Iterate over map key-value pair
	 * @param iteratorCtx
	 * @param map
	 * @param statementOrBlockCtx
	 */
	private void forLoop(
		IteratorMapContext iteratorCtx,
		GioviMap map,
		StatementOrBlockContext statementOrBlockCtx) {
		for (var entry : map.unbox().entrySet()) {
			enterLoopScope();
			String keyIteratorName = iteratorCtx.key.getText();
			String elementIteratorName = iteratorCtx.value.getText();
			getCurrentScope().declare(keyIteratorName, entry.getKey().copy());
			getCurrentScope().declare(elementIteratorName, entry.getValue());
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
		var bop = GioviToken.of(ctx.bop.getText());
		var variable = visit(ctx.varDeclaration());
		switch (bop) {
			case REDIR -> variable.redirection(visit(ctx.redirectionContent()));
			default -> throw new RedirectionException("Unknown redirection operator: " + bop);
		}
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
		var type = visit(typeCtx).asGioviType();
		var value = type.spawn();
		if (defaultDefinitionCtx != null) {
			var defaultValue = visit(defaultDefinitionCtx).asGioviValue();
			value.setDefaultValue(defaultValue);
			value.boxDefault();
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
		// TODO: improve this. Declare variable with value already set
		var bop = GioviToken.of(ctx.bop.getText());
		var variable = visit(ctx.varDeclaration());
		switch (bop) {
			case REDIR_RTL -> variable.redirection(visit(ctx.redirectionContent()));
			default -> throw new RedirectionException("Unknown redirection operator: " + bop);
		}
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
			? GioviTypes.of(GioviVoid.class)
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
		if (ctx.defaultDefinition() == null) {
			return new FunctionSignatureParam(identifier, type);
		}
		var defaultExpression = visit(ctx.defaultDefinition().expression());
		if (defaultExpression instanceof GioviDefault) { // TODO use GioviPlaceholder (underscore) instead
			return new FunctionSignatureParam(identifier, type, type.spawn(), true);
		}
		var defaultValue = defaultExpression.asGioviValue();
		var defaultValueType = defaultValue.getGioviType();
		if (defaultValueType.equals(type)) {
			return new FunctionSignatureParam(identifier, type, defaultValue);
		}
		if (defaultValueType.canWidenTo(type)) {
			return new FunctionSignatureParam(identifier, type,
				defaultValue.as(type).asGioviValue());
		}
		throw new GioviException("Incompatible default value. Expecting a " + type
			+ " but instead got " + defaultValueType);
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
				throw new GioviException("Can't call " + identifier, e);
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
			} else if (actualParamType.canWidenTo(formalParamType)) {
				getCurrentScope().declare(
					formalParamIdentifier,
					actualParamValue.as(formalParamType).asGioviValue());
			} else {
				throw new GioviException("Wrong parameter type. Expecting " + formalParamType
					+ " but instead got " + actualParamType);
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
				} else if (actualParamType.canWidenTo(formalParamType)) {
					getCurrentScope().declare(
						formalParamIdentifier,
						actualParamValue.as(formalParamType).asGioviValue());
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
				var returnedType = returnedValue.getGioviType();
				if (returnType.unbox() == GioviVoid.class
					&& returnedType.unbox() != GioviVoid.class) {
					throw new GioviException(
						"Can't return " + returnedValue + " in a function with no return type");
				}
				if (returnedType.equals(returnType)) {
					return returnedValue;
				}
				if (returnedType.canWidenTo(returnType)) {
					return returnedValue.as(returnType).asGioviValue();
				}
				throw new GioviException(
					"Wrong return type, expecting " + returnType + " but got " + returnedType);
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
				? new GioviFieldDefinition(identifier, fieldType)
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
		return new GioviDefault();
	}

}
