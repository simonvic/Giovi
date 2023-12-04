package it.simonvic.giovi;

import java.util.*;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.exceptions.*;
import it.simonvic.giovi.lang.*;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.lang.values.natives.Struct;
import it.simonvic.giovi.lang.values.natives.Struct.Named;
import it.simonvic.giovi.util.GioviTypes;
import it.simonvic.giovi.util.OperationEvaluator;
import it.simonvic.giovi.util.TextProcessor;
import it.simonvic.giovi.visitors.SwitchHelperVisitor;

public class GioviVisitor extends GioviParserBaseVisitor<GioviObject> {

	public static GioviParser parserOf(String code) {
		GioviLexer lexer = new GioviLexer(CharStreams.fromString(code));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new GioviParser(tokens);
	}

	public static GioviVisitor visit(String code) {
		return visit(new GioviVisitor(), code);
	}

	public static GioviObject runExpression(String code) {
		return runExpression(new GioviVisitor(), code);
	}

	public static GioviObject run(String code) {
		return run(new GioviVisitor(), code);
	}

	public static GioviVisitor visit(GioviVisitor visitor, String code) {
		var lexer = new GioviLexer(CharStreams.fromString(code));
		var tokens = new CommonTokenStream(lexer);
		visitor.visit(new GioviParser(tokens).begin());
		return visitor;
	}

	public static GioviObject runExpression(GioviVisitor visitor, String code) {
		return visitor.visit(parserOf(code).expression());
	}

	public static GioviObject run(GioviVisitor visitor, String code) {
		return visitor.visit(parserOf(code).begin());
	}

	protected final Scanner scanner = new Scanner(System.in);

	private final Scope globalScope = new Scope();
	private Scope currentScope = globalScope;

	public Scope getCurrentScope() {
		return currentScope;
	}

	public void setCurrentScope(Scope scope) {
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
		setCurrentScope(getCurrentScope().getParentScope());
	}

	@Override
	public GioviObject visitIfStatement(IfStatementContext ctx) {
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
	public GioviObject visitBlock(BlockContext ctx) {
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
	public GioviObject visitStatementOrBlock(StatementOrBlockContext ctx) {
		return ctx.block() != null
			? visit(ctx.block())
			: visit(ctx.statement());
	}

	@Override
	public GioviObject visitSwitchStatement(SwitchStatementContext ctx) {
		var subject = visit(ctx.expression());
		var matchedLabel = SwitchHelperVisitor.evaluate(this, ctx.switchBlock(), subject);
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
	public GioviObject visitBreakStatement(BreakStatementContext ctx) {
		getCurrentScope().doBreak();
		return null;
	}

	@Override
	public GioviObject visitSkipStatement(SkipStatementContext ctx) {
		getCurrentScope().doSkip();
		return null;
	}

	@Override
	public GioviObject visitForStatement(ForStatementContext ctx) {
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
	public GioviObject visitRepeatStatement(RepeatStatementContext ctx) {
		enterLoopScope();
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


	@Override
	public GioviObject visitRedirectionContent(RedirectionContentContext ctx) {
		return ctx.expression() != null
			? visit(ctx.expression())
			: visit(ctx.primitiveContent());
	}

	@Override
	public GioviObject visitRedirectionToVarDeclaration(RedirectionToVarDeclarationContext ctx) {
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
	public GioviObject visitVarDeclaration(VarDeclarationContext ctx) {
		var variable = spawnType(ctx.type(), Optional.ofNullable(ctx.defaultDefinition()));
		getCurrentScope().declare(ctx.identifier().getText(), variable);
		return variable;
	}

	@Override
	public GioviObject visitConstantDeclaration(ConstantDeclarationContext ctx) {
		var constant = visit(ctx.expression()).asGioviValue();
		getCurrentScope().declareConstant(ctx.constantIdentifier().getText(), constant);
		return constant;
	}

	@Override
	public GioviObject visitRedirectionToIdentifier(RedirectionToIdentifierContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperator().getText(),
			visit(ctx.identifier()));
		return null;
	}

	@Override
	public GioviObject visitRedirectionToConstantIdentifier(
		RedirectionToConstantIdentifierContext ctx) {
		throw new GioviException("Can't redirect to a constant");
	}

	@Override
	public GioviObject visitRedirectionToExpression(RedirectionToExpressionContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperator().getText(),
			visit(ctx.expression()));
		return null;
	}

	@Override
	public GioviObject visitRedirectionToVarDeclarationRTL(
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
	public GioviObject visitRedirectionToIdentifierRTL(RedirectionToIdentifierRTLContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperatorRTL().getText(),
			visit(ctx.identifier()));
		return null;
	}

	@Override
	public GioviObject visitRedirectionToExpressionRTL(RedirectionToExpressionRTLContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperatorRTL().getText(),
			visit(ctx.expression()));
		return null;
	}

	@Override
	public GioviObject visitRedirectionToPrimitiveDestination(
		RedirectionToPrimitiveDestinationContext ctx) {
		OperationEvaluator.redirectionToPrimitive(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperator().getText(),
			ctx.primitiveDestination().getText());
		return null;
	}

	@Override
	public GioviObject visitRedirectionToPrimitiveDestinationRTL(
		RedirectionToPrimitiveDestinationRTLContext ctx) {
		OperationEvaluator.redirectionToPrimitive(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperatorRTL().getText(),
			ctx.primitiveDestination().getText());
		return null;
	}

	@Override
	public GioviObject visitExpressionParenthesized(ExpressionParenthesizedContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public GioviObject visitExpressionPrefixed(ExpressionPrefixedContext ctx) {
		return OperationEvaluator.prefixed(
			ctx.prefix.getText(),
			visit(ctx.expression()));
	}

	@Override
	public GioviObject visitExpressionAdditive(ExpressionAdditiveContext ctx) {
		return OperationEvaluator.additive(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitExpressionMultiplicative(ExpressionMultiplicativeContext ctx) {
		return OperationEvaluator.multiplicative(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitExpressionAccess(ExpressionAccessContext ctx) {
		return visit(ctx.expression()).access(visit(ctx.accessKey()));
	}

	@Override
	public GioviObject visitExpressionDot(ExpressionDotContext ctx) {
		var lhs = visit(ctx.expression());
		return ctx.anyIdentifier() != null
			? lhs.dot(new AccessKey(ctx.anyIdentifier().getText()))
			: lhs.dot(
				getCurrentScope().getDefinitionFunction(ctx.functionCall().identifier().getText()));
	}

	@Override
	public GioviObject visitExpressionPower(ExpressionPowerContext ctx) {
		return visit(ctx.expression(0)).power(visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitExpressionRelational(ExpressionRelationalContext ctx) {
		return OperationEvaluator.relational(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitExpressionNot(ExpressionNotContext ctx) {
		return visit(ctx.expression()).not();
	}

	@Override
	public GioviObject visitExpressionBool(ExpressionBoolContext ctx) {
		return OperationEvaluator.bool(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitExpressionEquality(ExpressionEqualityContext ctx) {
		return OperationEvaluator.equality(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitExpressionContainment(ExpressionContainmentContext ctx) {
		return OperationEvaluator.containment(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitExpressionCast(ExpressionCastContext ctx) {
		// TODO: add as operator in GioviObject
		return visit(ctx.expression()).asGioviValue().castTo(visit(ctx.type()).asGioviType());
	}

	@Override
	public GioviObject visitExpressionTypeCheck(ExpressionTypeCheckContext ctx) {
		var expression = visit(ctx.expression()).asGioviValue();
		var type = visit(ctx.type()).asGioviType();
		return new GioviBool(expression.getGioviType().equals(type));
	}

	@Override
	public GioviObject visitAccess(AccessContext ctx) {
		return getCurrentScope()
			.getVariableValue(ctx.anyIdentifier().getText())
			.access(visit(ctx.accessKey()));
	}

	@Override
	public GioviObject visitAccessKeySpecial(AccessKeySpecialContext ctx) {
		return new AccessKey(ctx.key.getText());
	}

	@Override
	public GioviObject visitAccessKeyLiteral(AccessKeyLiteralContext ctx) {
		return visit(ctx.literal());
	}

	@Override
	public GioviObject visitAccessKeyParenthesized(AccessKeyParenthesizedContext ctx) {
		return ctx.accessKey() != null ? visit(ctx.accessKey()) : visit(ctx.expression());
	}

	@Override
	public GioviObject visitAccessKeyRange(AccessKeyRangeContext ctx) {
		return visit(ctx.range());
	}

	@Override
	public GioviObject visitIdentifier(IdentifierContext ctx) {
		return getCurrentScope().getVariableValue(ctx.getText());
	}

	@Override
	public GioviObject visitConstantIdentifier(ConstantIdentifierContext ctx) {
		return getCurrentScope().getConstantValue(ctx.getText());
	}

	@Override
	public GioviObject visitRangeOrExpression(RangeOrExpressionContext ctx) {
		return ctx.range() != null
			? visit(ctx.range())
			: visit(ctx.expression());
	}

	@Override
	public GioviObject visitRange(RangeContext ctx) {
		return Range.of(
			visit(ctx.start),
			visit(ctx.end),
			ctx.step == null ? null : visit(ctx.step));
	}

	@Override
	public GioviObject visitListCreation(ListCreationContext ctx) {
		List<GioviValue<?>> entries = new ArrayList<>();
		for (ListEntryContext entryCtx : ctx.listEntries().listEntry()) {
			GioviObject entry = visit(entryCtx);
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
	public GioviObject visitMapCreation(MapCreationContext ctx) {
		LinkedHashMap<GioviValue<?>, GioviValue<?>> map = new LinkedHashMap<>();
		for (MapEntryContext entryCtx : ctx.mapEntries().mapEntry()) {
			map.put(visit(entryCtx.mapKey).asGioviValue(), visit(entryCtx.mapValue).asGioviValue());
		}
		return new GioviMap(map);
	}

	@Override
	public GioviObject visitFunctionDeclaration(FunctionDeclarationContext ctx) {
		LinkedHashMap<String, GioviFunctionParameter> formalParameters = new LinkedHashMap<>();
		if (ctx.formalParameters() != null) {
			boolean expectParamWithDefault = false;
			for (FormalParameterDefinitionContext formalParamCtx : ctx.formalParameters()
				.formalParameterDefinition()) {
				GioviValue<?> paramValue = spawnType(formalParamCtx.type(),
					Optional.ofNullable(formalParamCtx.defaultDefinition()));
				if (formalParamCtx.defaultDefinition() == null) {
					if (expectParamWithDefault) {
						throw new GioviException("Expecting default definition for parameter: "
							+ formalParamCtx.identifier().getText());
					}
				} else {
					GioviObject defaultValue = visit(
						formalParamCtx.defaultDefinition().expression());
					if (defaultValue instanceof GioviDefault) {
						paramValue.boxDefault();
					} else {
						paramValue.setDefaultValue(defaultValue.asGioviValue());
					}
					expectParamWithDefault = true;
				}
				formalParameters.put(
					formalParamCtx.identifier().getText(),
					new GioviFunctionParameter(paramValue, expectParamWithDefault));
			}
		}
		getCurrentScope().define(ctx.identifier().getText(), new GioviFunction(
			ctx.block(),
			ctx.type() == null ? null : visit(ctx.type()).asGioviType(),
			formalParameters));
		return null;
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
	public GioviObject visitFunctionCall(FunctionCallContext ctx) {
		GioviFunction func = getCurrentScope().getDefinitionFunction(ctx.identifier().getText());
		enterFunctionScope();
		setupFunctionParameters(ctx, func);
		GioviValue<?> returnValue = visitFunction(func);
		exitScope();
		return returnValue;
	}

	private GioviValue<?> visitFunction(GioviFunction func) {
		for (StatementContext statement : func.getBody().statement()) {
			visit(statement);
			if (getCurrentScope().shouldReturn()) {
				GioviType<?> returnType = func
					.getReturnType()
					.orElseThrow(
						() -> new GioviException("Returning in function with no return value"));
				GioviValue<?> returnValue = getCurrentScope().getReturnValue();
				var returnedType = returnValue.getGioviType();
				if (!returnedType.equals(returnType)) {
					throw new GioviException(
						"Wrong return type, expecting " + returnType + " but got " + returnedType);
				}
				return returnValue;
			}
		}
		if (func.getReturnType().isPresent()) {
			throw new GioviException("Missing return statement in function");
		}
		return null;
	}

	private void setupFunctionParameters(FunctionCallContext ctx, GioviFunction func) {
		int functionCallParamCount = ctx.functionParameterList() == null ? 0
			: ctx.functionParameterList().functionParameter().size();
		if (functionCallParamCount > func.getParameters().size()) {
			throw new GioviException(
				"Wrong number of parameters for function: " + ctx.identifier().getText());
		}
		if (ctx.functionParameterList() == null
			|| ctx.functionParameterList().functionParameter(0).identifier() == null) {
			setupFunctionParametersIteratively(ctx, func);
		} else {
			setupNamedFunctionParameters(ctx, func);
		}
	}

	// TODO: rework functions completely
	private void setupFunctionParametersIteratively(FunctionCallContext ctx, GioviFunction func) {
		List<FunctionParameterContext> funcCallParams = ctx.functionParameterList() == null
			? List.of()
			: ctx.functionParameterList().functionParameter();
		int funcCallParamsCount = funcCallParams.size();
		Iterator<Map.Entry<String, GioviFunctionParameter>> funcIterator = func.getParameters()
			.entrySet().iterator();
		int i = 0;
		while (funcIterator.hasNext()) {
			Map.Entry<String, GioviFunctionParameter> entry = funcIterator.next();
			String formalParameterIdentifier = entry.getKey();
			GioviFunctionParameter formalValue = entry.getValue();
			if (i < funcCallParamsCount) {
				FunctionParameterContext funcParamCtx = funcCallParams.get(i);
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

	private void setupNamedFunctionParameters(FunctionCallContext ctx, GioviFunction func) {
		List<FunctionParameterContext> funcCallParams = ctx.functionParameterList() == null
			? List.of()
			: ctx.functionParameterList().functionParameter();
		LinkedHashMap<String, GioviFunctionParameter> funcParams = func.getParameters();

		Map<String, GioviValue<?>> funcCallParamsMap = new HashMap<>();
		for (FunctionParameterContext funcCallParamCtx : funcCallParams) {
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
	public GioviObject visitReturnStatement(ReturnStatementContext ctx) {
		GioviValue<?> returnValue = visit(ctx.expression()).asGioviValue();
		getCurrentScope().doReturn(returnValue);
		return returnValue;
	}


	@Override
	public GioviObject visitStructDefinition(StructDefinitionContext ctx) {
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
	public GioviObject visitStructCreation(StructCreationContext ctx) {
		var structFieldsCtx = ctx.structFields();
		return ctx.structType != null
			? createTypedStruct(ctx.structType.getText(), structFieldsCtx)
			: createAnonStruct(structFieldsCtx);
	}

	private GioviStruct createTypedStruct(String typename, StructFieldsContext ctx) {
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

	private GioviStruct createAnonStruct(StructFieldsContext ctx) {
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

	private GioviStructDefinition getDefinitionStruct(String structType) {
		if (!getCurrentScope().isDefinedStruct(structType)) {
			throw new TypeNotFound(structType);
		}
		return getCurrentScope().getDefinitionStruct(structType);
	}

	@Override
	public GioviObject visitType(TypeContext ctx) {
		if (ctx.typeComposite() != null) {
			return visit(ctx.typeComposite());
		}

		String typeIdentifier = ctx.typeIdentifier().getText();
		return switch (typeIdentifier) {
			case "Natural" -> GioviTypes.of(GioviNatural.class);
			case "Int" -> GioviTypes.of(GioviInt.class);
			case "Real" -> GioviTypes.of(GioviReal.class);
			case "Bool" -> GioviTypes.of(GioviBool.class);
			case "Text" -> GioviTypes.of(GioviText.class);
			default -> new GioviTypeStructTyped(getDefinitionStruct(typeIdentifier));
		};
	}

	@Override
	public GioviObject visitTypeList(TypeListContext ctx) {
		var entryType = visit(ctx.type()).asGioviType();
		if (ctx.initSize == null) return new GioviTypeList(entryType);
		return switch (visit(ctx.initSize)) {
			case GioviInt i -> new GioviTypeList(entryType, i.unbox());
			case GioviNatural i -> new GioviTypeList(entryType, i.unbox().intValue());
			default -> throw new GioviException("Invalid list init size");
		};
	}

	@Override
	public GioviObject visitTypeMap(TypeMapContext ctx) {
		return new GioviTypeMap(
			visit(ctx.mapKeyType).asGioviType(),
			visit(ctx.mapValueType).asGioviType());
	}

	@Override
	public GioviObject visitPrimitiveContent(PrimitiveContentContext ctx) {
		return new GioviText(scanner.nextLine());
	}

	@Override
	public GioviObject visitLiteralInteger(LiteralIntegerContext ctx) {
		return GioviInt.valueOf(ctx.getText());
	}

	@Override
	public GioviObject visitLiteralNatural(LiteralNaturalContext ctx) {
		return GioviNatural.valueOf(ctx.getText());
	}

	@Override
	public GioviObject visitLiteralReal(LiteralRealContext ctx) {
		return GioviReal.valueOf(ctx.getText());
	}

	@Override
	public GioviObject visitLiteralBool(LiteralBoolContext ctx) {
		return GioviBool.valueOf(ctx.getText());
	}

	@Override
	public GioviObject visitLiteralText(LiteralTextContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviObject visitLiteralTextBlock(LiteralTextBlockContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.trimIncidentalWhitespaces()
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviObject visitLiteralExactText(LiteralExactTextContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviObject visitLiteralExactTextBlock(LiteralExactTextBlockContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.trimIncidentalWhitespaces()
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviObject visitLiteralNull(LiteralNullContext ctx) {
		return null;
	}

	@Override
	public GioviObject visitLiteralDefault(LiteralDefaultContext ctx) {
		return new GioviDefault();
	}

	@Override
	public GioviObject visitLiteralPlaceholder(LiteralPlaceholderContext ctx) {
		return new GioviDefault();
	}

}
