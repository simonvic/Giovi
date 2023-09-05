package it.simonvic.giovi;

import java.util.*;

import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.exceptions.*;
import it.simonvic.giovi.lang.*;
import it.simonvic.giovi.lang.functions.FunctionScope;
import it.simonvic.giovi.lang.functions.GioviFunction;
import it.simonvic.giovi.lang.functions.GioviFunctionParameter;
import it.simonvic.giovi.lang.primitives.Natural;
import it.simonvic.giovi.lang.primitives.Struct;
import it.simonvic.giovi.lang.primitives.Struct.Named;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.util.OperationEvaluator;
import it.simonvic.giovi.util.TextProcessor;
import it.simonvic.giovi.visitors.SwitchHelperVisitor;

public class GioviVisitor extends GioviParserBaseVisitor<GioviObject> {

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
		SwitchHelperVisitor
			.evaluate(this, ctx)
			.ifPresent(block -> {
				enterScope();
				visit(block);
				exitScope();
			});
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
		if (gioviList.unbox().size() == 0)
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
		if (map.unbox().size() == 0)
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
			case ">" -> variable.redirection(visit(ctx.redirectionContent()));
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
	public GioviObject visitRedirectionToIdentifier(RedirectionToIdentifierContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.redirectionContent()),
			ctx.redirectionOperator().getText(),
			visit(ctx.identifier()));
		return null;
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
			case "<" -> variable.redirection(visit(ctx.redirectionContent()));
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
	public GioviObject visitPrimitiveContent(PrimitiveContentContext ctx) {
		String identifier = ctx.identifier().getText();
		return switch (identifier) {
			case "out" -> new GioviText(new Scanner(System.in).nextLine());
			default -> throw new GioviException("Unkown primitive content " + identifier);
		};
	}

	@Override
	public GioviObject visitRedirectionToPrimitiveDestination(
		RedirectionToPrimitiveDestinationContext ctx) {
		GioviObject content = visit(ctx.redirectionContent());
		switch (ctx.primitiveDestination().getText()) {
			case "\\out" -> System.out.println(content);
			case "\\err" -> System.err.println(content);
			default -> throw new GioviException(
				"Unkown primitive destination " + ctx.primitiveDestination().getText());
		}
		return null;
	}

	@Override
	public GioviObject visitRedirectionToPrimitiveDestinationRTL(
		RedirectionToPrimitiveDestinationRTLContext ctx) {
		GioviObject content = visit(ctx.redirectionContent());
		switch (ctx.primitiveDestination().getText()) {
			case "\\out" -> System.out.println(content);
			case "\\err" -> System.err.println(content);
			default -> throw new GioviException(
				"Unkown primitive destination " + ctx.primitiveDestination().getText());
		}
		return null;
	}

	@Override
	public GioviObject visitParenthesizedExpression(ParenthesizedExpressionContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public GioviObject visitPrefixedExpression(PrefixedExpressionContext ctx) {
		return OperationEvaluator.prefixed(
			ctx.prefix.getText(),
			visit(ctx.expression()));
	}

	@Override
	public GioviObject visitAdditiveExpression(AdditiveExpressionContext ctx) {
		return OperationEvaluator.additive(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
		return OperationEvaluator.multiplicative(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitAccessExpression(AccessExpressionContext ctx) {
		return visit(ctx.expression()).access(visit(ctx.accessKey()));
	}

	@Override
	public GioviObject visitPowerExpression(PowerExpressionContext ctx) {
		return visit(ctx.expression(0)).power(visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitRelationalExpression(RelationalExpressionContext ctx) {
		return OperationEvaluator.relational(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitNotExpression(NotExpressionContext ctx) {
		return visit(ctx.expression()).not();
	}

	@Override
	public GioviObject visitBoolExpression(BoolExpressionContext ctx) {
		return OperationEvaluator.bool(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitEqualityExpression(EqualityExpressionContext ctx) {
		return OperationEvaluator.equality(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitContainmentExpression(ContainmentExpressionContext ctx) {
		return OperationEvaluator.containment(
			visit(ctx.expression(0)),
			ctx.bop.getText(),
			visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitAccess(AccessContext ctx) {
		return getCurrentScope()
			.getVariableValue(ctx.identifier().getText())
			.access(visit(ctx.accessKey()));
	}

	@Override
	public GioviObject visitSpecialAccessKey(SpecialAccessKeyContext ctx) {
		return new AccessKey(ctx.key.getText());
	}

	@Override
	public GioviObject visitLiteralAccessKey(LiteralAccessKeyContext ctx) {
		return visit(ctx.literal());
	}

	@Override
	public GioviObject visitParenthesizedAccessKey(ParenthesizedAccessKeyContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public GioviObject visitRangeAccessKey(RangeAccessKeyContext ctx) {
		return visit(ctx.range());
	}

	@Override
	public GioviObject visitIdentifier(IdentifierContext ctx) {
		return getCurrentScope().getVariableValue(ctx.getText());
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
	public GioviObject visitIntegerLiteral(IntegerLiteralContext ctx) {
		return GioviInt.valueOf(ctx.getText());
	}

	@Override
	public GioviObject visitNaturalLiteral(NaturalLiteralContext ctx) {
		return GioviNatural.valueOf(ctx.getText());
	}

	@Override
	public GioviObject visitRealLiteral(RealLiteralContext ctx) {
		return GioviReal.valueOf(ctx.getText());
	}

	@Override
	public GioviObject visitBoolLiteral(BoolLiteralContext ctx) {
		return GioviBool.valueOf(ctx.getText());
	}

	@Override
	public GioviObject visitTextLiteral(TextLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviObject visitTextBlockLiteral(TextBlockLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.trimIncidentalWhitespaces()
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviObject visitExactTextLiteral(ExactTextLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviObject visitExactTextBlockLiteral(ExactTextBlockLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.trimIncidentalWhitespaces()
			.unescapeUnicode()
			.collect();
	}

	@Override
	public GioviObject visitNullLiteral(NullLiteralContext ctx) {
		return null;
	}

	@Override
	public GioviObject visitDefaultLiteral(DefaultLiteralContext ctx) {
		return new GioviDefault();
	}

	@Override
	public GioviObject visitType(TypeContext ctx) {
		if (ctx.collectionType() != null) {
			return visit(ctx.collectionType());
		}

		String typeIdentifier = ctx.identifier().getText();
		return switch (typeIdentifier) {
			case "Natural" -> new GioviTypePrimitive(GioviNatural.class);
			case "Int" -> new GioviTypePrimitive(GioviInt.class);
			case "Real" -> new GioviTypePrimitive(GioviReal.class);
			case "Bool" -> new GioviTypePrimitive(GioviBool.class);
			case "Text" -> new GioviTypePrimitive(GioviText.class);
			default -> {
				if (getCurrentScope().isDefinedStruct(typeIdentifier)) {
					yield new GioviTypeStruct(typeIdentifier);
				} else {
					throw new TypeNotFound(ctx.getText());
				}
			}
		};
	}

	@Override
	public GioviObject visitListType(ListTypeContext ctx) {
		return new GioviTypeList(visit(ctx.type()).asGioviType());
	}

	@Override
	public GioviObject visitMapType(MapTypeContext ctx) {
		return new GioviTypeMap(
			visit(ctx.mapKeyType).asGioviType(),
			visit(ctx.mapValueType).asGioviType());
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
		var value = switch (type) {
			case GioviTypeList t -> new GioviList(t.getEntryType());
			case GioviTypeMap t -> new GioviMap(t.getKeyType(), t.getElementType());
			case GioviTypePrimitive t -> t.spawn();
			case GioviTypeStruct t -> getCurrentScope()
				.getDefinitionStruct(typeCtx.identifier().getText());
		};

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
			getCurrentScope().declare(formalParameterIdentifier, formalValue.value());
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
			struct.putAll((Named) parentDefinition.unbox());
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

		String structName = ctx.structName.getText();
		var definitionStruct = new GioviStruct(structName, struct);
		getCurrentScope().define(structName, definitionStruct);
		return definitionStruct;
	}

	@Override
	public GioviObject visitStructCreation(StructCreationContext ctx) {
		var structFieldsCtx = ctx.structFields();
		return ctx.structType != null
			? createTypedStruct(structFieldsCtx, ctx.structType.getText())
			: createAnonStruct(structFieldsCtx);
	}

	private GioviStruct createTypedStruct(StructFieldsContext ctx, String structType) {
		var struct = (Struct.Named) getDefinitionStruct(structType).unbox().copy();
		if (ctx == null) {
			return new GioviStruct(structType, struct);
		}
		if (ctx.namedStructField().size() != 0) {
			for (var namedStructFieldCtx : ctx.namedStructField()) {
				String fieldName = namedStructFieldCtx.identifier().getText();
				if (!struct.contains(fieldName)) {
					throw new IncompatibleStructs(
						"Struct " + structType + " doesn't have a field " + fieldName);
				}
				struct.put(
					fieldName,
					visit(namedStructFieldCtx.expression()).asGioviValue());
			}
			return new GioviStruct(structType, struct);
		} else {
			int fieldsCount = ctx.expression().size();
			if (fieldsCount != struct.getFieldsCount()) {
				throw new IncompatibleStructs(structType + " has " + struct.getFieldsCount()
					+ " but " + fieldsCount + " were provided");
			}
			for (int i = 0; i < fieldsCount; i++) {
				struct.getField(i).box(visit(ctx.expression(i)).asGioviValue());
			}
			return new GioviStruct(structType, struct);
		}
	}

	private GioviStruct createAnonStruct(StructFieldsContext ctx) {
		if (ctx == null) {
			return new GioviStruct(new Struct.Unnamed());
		}
		if (ctx.namedStructField().size() != 0) {
			var struct = new Struct.Named();
			for (var namedStructFieldCtx : ctx.namedStructField()) {
				struct.put(
					namedStructFieldCtx.identifier().getText(),
					visit(namedStructFieldCtx.expression()).asGioviValue());
			}
			return new GioviStruct(struct);
		} else {
			var struct = new Struct.Unnamed();
			for (var expressionCtx : ctx.expression()) {
				struct.put(visit(expressionCtx).asGioviValue());
			}
			return new GioviStruct(struct);
		}
	}

	@Override
	public GioviObject visitDotExpression(DotExpressionContext ctx) {
		var lhs = visit(ctx.expression());
		return ctx.identifier() != null
			? lhs.dot(new AccessKey(ctx.identifier().getText()))
			: lhs.dot(
				getCurrentScope().getDefinitionFunction(ctx.functionCall().identifier().getText()));
	}

	private GioviStruct getDefinitionStruct(String structType) {
		if (!getCurrentScope().isDefinedStruct(structType)) {
			throw new TypeNotFound(structType);
		}
		return getCurrentScope().getDefinitionStruct(structType);
	}
}
