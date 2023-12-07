package it.simonvic.giovi;

import java.util.*;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.delegates.SwitchStatementDelegate;
import it.simonvic.giovi.exceptions.*;
import it.simonvic.giovi.lang.*;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.lang.values.natives.Struct;
import it.simonvic.giovi.lang.values.natives.Struct.Named;
import it.simonvic.giovi.util.GioviTypes;
import it.simonvic.giovi.util.OperationEvaluator;
import it.simonvic.giovi.util.TextProcessor;

public class GioviVisitor extends GioviParserBaseVisitor<GioviParserVisitResult> {

	public static GioviParser parserOf(String code) {
		var lexer = new GioviLexer(CharStreams.fromString(code));
		var tokens = new CommonTokenStream(lexer);
		return new GioviParser(tokens);
	}

	public static GioviVisitor visit(String code) {
		return visit(new GioviVisitor(), code);
	}

	public static GioviParserVisitResult visitExpression(String code) {
		return visitExpression(new GioviVisitor(), code);
	}

	public static GioviVisitor visit(GioviVisitor visitor, String code) {
		var lexer = new GioviLexer(CharStreams.fromString(code));
		var tokens = new CommonTokenStream(lexer);
		visitor.visit(new GioviParser(tokens).begin());
		return visitor;
	}

	public static GioviParserVisitResult visitExpression(GioviVisitor visitor, String code) {
		return visitor.visit(parserOf(code).expression());
	}

	public static GioviFunctionSignature visitFunctionDeclaration(String code) {
		return visitFunctionDeclaration(new GioviVisitor(), code);
	}

	public static GioviFunctionSignature visitFunctionDeclaration(
		GioviVisitor visitor,
		String code) {
		return (GioviFunctionSignature) visitor.visit(parserOf(code).functionDeclaration());
	}

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
		if (visit(ctx.expression()).asBoolean()) {
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

		for (Map.Entry<? extends GioviValue<?>, ? extends GioviValue<?>> entry : map.unbox()
			.entrySet()) {

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
		enterLoopScope(); // TODO: wtf?
		boolean infiniteRepeat = ctx.IF() == null;
		while (infiniteRepeat || visit(ctx.expression()).asBoolean()) {
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
		var formalParameters = new LinkedHashMap<String, GioviFunctionSignatureParameter>();
		if (ctx.formalParameters() != null) {
			boolean expectParamWithDefault = false;
			for (var formalParamCtx : ctx.formalParameters().formalParameter()) {
				var paramValue = spawnType(
					formalParamCtx.type(),
					Optional.ofNullable(formalParamCtx.defaultDefinition()));
				String formalParamIdentifier = formalParamCtx.identifier().getText();
				if (formalParamCtx.defaultDefinition() == null) {
					if (expectParamWithDefault) {
						throw new GioviException("Expecting default definition for parameter: "
							+ formalParamIdentifier);
					}
				} else {
					var defaultValue = visit(formalParamCtx.defaultDefinition().expression());
					if (defaultValue instanceof GioviDefault) {
						paramValue.boxDefault();
					} else {
						paramValue.setDefaultValue(defaultValue.asGioviValue());
					}
					expectParamWithDefault = true;
				}
				formalParameters.put(
					formalParamIdentifier,
					new GioviFunctionSignatureParameter(
						paramValue,
						expectParamWithDefault));
			}
		}
		var signature = new GioviFunctionSignature(
			ctx.identifier().getText(),
			ctx.block(),
			ctx.type() == null ? GioviTypes.of(GioviVoid.class) : visit(ctx.type()).asGioviType(),
			formalParameters);
		getCurrentScope().declare(signature);
		return signature;
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
	 * @param func
	 * @return
	 */
	private GioviValue<?> callFunction(GioviFunctionSignature func) {
		for (StatementContext statement : func.body().statement()) {
			visit(statement);
			if (getCurrentScope().shouldReturn()) {
				var returnType = func.returnType();
				if (returnType.unbox().getClazz() == GioviVoid.class) {
					throw new GioviException("Returning in function with no return value");
				}
				var returnValue = getCurrentScope().getReturnValue();
				var returnedType = returnValue.getGioviType();
				if (returnedType.equals(returnType)) {
					return returnValue;
				}
				if (returnedType.canUpcast(returnType)) {
					return returnValue.castTo(returnType);
				}
				throw new GioviException(
					"Wrong return type, expecting " + returnType + " but got " + returnedType);
			}
		}
		if (func.returnType().unbox().getClazz() != GioviVoid.class) {
			throw new GioviException("Missing return statement in function");
		}
		return new GioviVoid();
	}

	private void setupFunctionParameters(FunctionCallContext ctx, GioviFunctionSignature func) {
		int functionCallParamCount = ctx.functionCallParameters() == null ? 0
			: ctx.functionCallParameters().functionCallParameter().size();
		if (functionCallParamCount > func.parameters().size()) {
			throw new GioviException(
				"Wrong number of parameters for function: " + ctx.identifier().getText());
		}
		if (ctx.functionCallParameters() == null
			|| ctx.functionCallParameters().functionCallParameter(0).identifier() == null) {
			setupFunctionParametersIteratively(ctx, func);
		} else {
			setupNamedFunctionParameters(ctx, func);
		}
	}

	// TODO: rework functions completely
	private void setupFunctionParametersIteratively(FunctionCallContext ctx,
		GioviFunctionSignature func) {
		List<FunctionCallParameterContext> funcCallParams = ctx.functionCallParameters() == null
			? List.of()
			: ctx.functionCallParameters().functionCallParameter();
		int funcCallParamsCount = funcCallParams.size();
		var paramsIterator = func.parameters().entrySet().iterator();
		int i = 0;
		while (paramsIterator.hasNext()) {
			var entry = paramsIterator.next();
			String formalParameterIdentifier = entry.getKey();
			GioviFunctionSignatureParameter formalValue = entry.getValue();
			if (i < funcCallParamsCount) {
				FunctionCallParameterContext funcParamCtx = funcCallParams.get(i);
				if (funcParamCtx.identifier() != null) {
					throw new GioviException(
						"Unexpected named parameter: " + funcParamCtx.identifier().getText());
				}
				formalValue.value().box(visit(funcParamCtx.expression()).asGioviValue());
			} else if (formalValue.hasDefault()) {
				formalValue.value().boxDefault();
			} else {
				throw new GioviException("Wrong number of parameters");
			}
			getCurrentScope().declare(formalParameterIdentifier, formalValue.value().copy());
			i++;
		}
	}

	private void setupNamedFunctionParameters(FunctionCallContext ctx,
		GioviFunctionSignature func) {
		List<FunctionCallParameterContext> funcCallParams = ctx.functionCallParameters() == null
			? List.of()
			: ctx.functionCallParameters().functionCallParameter();
		var funcParams = func.parameters();
		var funcCallParamsMap = new HashMap<String, GioviValue<?>>();
		for (FunctionCallParameterContext funcCallParamCtx : funcCallParams) {
			String callParamName = funcCallParamCtx.identifier().getText();
			if (funcCallParamsMap.containsKey(callParamName)) {
				throw new GioviException(
					"Duplicate parameter name in function call: " + callParamName);
			}
			if (!funcParams.containsKey(callParamName)) {
				throw new GioviException("Unknown parameter name: " + callParamName);
			}
			funcCallParamsMap.put(callParamName,
				visit(funcCallParamCtx.expression()).asGioviValue());
		}

		funcParams.forEach((formalIdentifier, formalParameter) -> {
			GioviValue<?> callParamValue = funcCallParamsMap.get(formalIdentifier);
			if (callParamValue != null) {
				formalParameter.value().box(callParamValue);
			} else if (formalParameter.hasDefault()) {
				formalParameter.value().boxDefault();
			} else {
				throw new GioviException("Missing parameter in function call: " + formalIdentifier);
			}
			getCurrentScope().declare(formalIdentifier, formalParameter.value());
		});
	}


	@Override
	public GioviParserVisitResult visitReturnStatement(ReturnStatementContext ctx) {
		GioviValue<?> returnValue = visit(ctx.expression()).asGioviValue();
		getCurrentScope().doReturn(returnValue);
		return returnValue;
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// Structs

	@Override
	public GioviParserVisitResult visitStructDefinition(StructDefinitionContext ctx) {
		var struct = new Struct.Named();
		if (ctx.parentName != null) {
			var parentDefinition = getDefinitionStruct(ctx.parentName.getText());
			struct.putAll(parentDefinition.struct());
		}
		// TODO: nested struct definition
		// for (var structDefinitionCtx : ctx.structBodyDefinition().structDefinition()) {
		// 	s.put(structDefinitionCtx.structName.getText(),
		// 		visit(structDefinitionCtx).asGioviStruct());
		// }
		for (var fieldCtx : ctx.structBodyDefinition().structFieldDefinition()) {
			var defaultDefinition = Optional.ofNullable(fieldCtx.defaultDefinition());
			var fieldDefinition = spawnType(fieldCtx.type(), defaultDefinition);
			defaultDefinition.ifPresent(defaultDefinitionCtx -> fieldDefinition
				.setDefaultValue(visit(defaultDefinitionCtx).asGioviValue()));
			struct.put(fieldCtx.identifier().getText(), fieldDefinition);
		}
		// TODO: function definition
		// for (var functionCtx : ctx.structBodyDefinition().functionDeclaration()) {
		// 	s.put(functionCtx.identifier().getText(), visit(functionCtx).asGioviFunction());
		// }

		String typename = ctx.structName.getText();
		getCurrentScope().define(typename, new GioviStructDefinition(typename, struct));
		return null;
	}

	@Override
	public GioviParserVisitResult visitStructCreation(StructCreationContext ctx) {
		var structFieldsCtx = ctx.structFields();
		return ctx.structType != null
			? createTypedStruct(ctx.structType.getText(), structFieldsCtx)
			: createAnonStruct(structFieldsCtx);
	}

	private GioviStructTyped createTypedStruct(String typename, StructFieldsContext ctx) {
		GioviStructDefinition definition = getDefinitionStruct(typename);
		if (ctx == null) {
			return new GioviStructTyped(definition);
		}
		var struct = definition.struct().copy();
		if (!ctx.namedStructField().isEmpty()) {
			for (var namedStructFieldCtx : ctx.namedStructField()) {
				String fieldName = namedStructFieldCtx.identifier().getText();
				if (!struct.contains(fieldName)) {
					throw new IncompatibleStructs(
						"Struct " + typename + " doesn't have a field " + fieldName);
				}
				struct.put(
					fieldName,
					visit(namedStructFieldCtx.expression()).asGioviValue());
			}
			return new GioviStructTyped(struct, definition);
		} else {
			int fieldsCount = ctx.expression().size();
			if (fieldsCount != struct.getFieldsCount()) {
				throw new IncompatibleStructs(typename + " has " + struct.getFieldsCount()
					+ " but " + fieldsCount + " were provided");
			}
			for (int i = 0; i < fieldsCount; i++) {
				struct.getField(i).box(visit(ctx.expression(i)).asGioviValue());
			}
			return new GioviStructTyped(struct, definition);
		}
	}

	private GioviStructTuple createAnonStruct(StructFieldsContext ctx) {
		if (ctx == null) {
			return new GioviStructTuple(new Struct.Unnamed());
		}
		if (ctx.namedStructField().isEmpty()) {
			var struct = new Struct.Unnamed();
			for (var expressionCtx : ctx.expression()) {
				struct.put(visit(expressionCtx).asGioviValue());
			}
			return new GioviStructTuple(struct);
		} else {
			var struct = new Named();
			for (var namedStructFieldCtx : ctx.namedStructField()) {
				struct.put(
					namedStructFieldCtx.identifier().getText(),
					visit(namedStructFieldCtx.expression()).asGioviValue());
			}
			return new GioviStructTuple(struct);
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
			default -> new GioviTypeStructTyped(getDefinitionStruct(ctx.getText()));
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
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralTextBlock(LiteralTextBlockContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.trimIncidentalWhitespaces()
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralExactText(LiteralExactTextContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralExactTextBlock(LiteralExactTextBlockContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.trimIncidentalWhitespaces()
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviParserVisitResult visitLiteralNull(LiteralNullContext ctx) {
		return null;
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
