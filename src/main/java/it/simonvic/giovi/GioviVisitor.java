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
import it.simonvic.giovi.lang.type.GioviType;
import it.simonvic.giovi.lang.type.GioviTypeList;
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
			if (s.shouldBreak() || s.shouldReturn() || s.shouldContinue()) {
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
	public GioviObject visitContinueStatement(ContinueStatementContext ctx) {
		getCurrentScope().doContinue();
		return null;
	}

	@Override
	public GioviObject visitForStatement(ForStatementContext ctx) {
		if (ctx.sequenceOrExpression().sequence() != null) {
			forList(ctx.iterators().identifier(), ctx.sequenceOrExpression().sequence(), ctx.statementOrBlock());
		} else {
			GioviObject collection = visit(ctx.sequenceOrExpression().expression());
			switch (collection) {
				case GioviList list -> forList(ctx.iterators().identifier(), list, ctx.statementOrBlock());
				case GioviMap map -> forMap(ctx.iterators().identifier(), map, ctx.statementOrBlock());
				default -> throw new OperatorNotSupported("for");
			}
		}
		return null;
	}

	private void forList(List<IdentifierContext> iterators, SequenceContext sequence, StatementOrBlockContext statementOrBlock) {
		throw new GioviException("For loop over sequence not implemented yet");
	}

	private void forList(List<IdentifierContext> iterators, GioviList list, StatementOrBlockContext statementOrBlock) {
		if (iterators.size() != 1) throw new GioviException("Wrong number of iterators");
		if (list.unbox().size() == 0) return;


		for (GioviValue<?> value : list.unbox()) {

			enterLoopScope();

			//@TODO update when structs are implemented
			String iteratorName = iterators.get(0).getText();
			getCurrentScope().declare(iteratorName, value); //@todo iterator should be by value?
			GioviValue<?> iterator = getCurrentScope().getVariableValue(iteratorName);
			iterator.box(value);

			visit(statementOrBlock);

			if (getCurrentScope().shouldBreak() || getCurrentScope().shouldReturn()) {
				exitScope();
				break;
			}

			if (getCurrentScope().shouldContinue()) {
				exitScope();
				continue;
			}

			exitScope();
		}

	}

	private void forMap(List<IdentifierContext> iterators, GioviMap map, StatementOrBlockContext statementOrBlock) {
		if (iterators.size() != 2) throw new GioviException("Wrong number of iterators");
		if (map.unbox().size() == 0) return;

		for (Map.Entry<? extends GioviValue<?>, ? extends GioviValue<?>> entry : map.unbox().entrySet()) {

			enterLoopScope();

			//@todo update when structs are implemented
			String keyIteratorName = iterators.get(0).getText();
			String elementIteratorName = iterators.get(1).getText();

			//@todo iterators should be by value?
			getCurrentScope().declare(keyIteratorName, entry.getKey());
			getCurrentScope().getVariableValue(keyIteratorName).box(entry.getKey());
			getCurrentScope().declare(elementIteratorName, entry.getValue());
			getCurrentScope().getVariableValue(elementIteratorName).box(entry.getValue());

			visit(statementOrBlock);

			if (getCurrentScope().shouldBreak() || getCurrentScope().shouldReturn()) {
				exitScope();
				break;
			}

			if (getCurrentScope().shouldContinue()) {
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

			if (getCurrentScope().shouldContinue()) {
				exitScope();
				continue;
			}

			exitScope();
		}
		return null;
	}


	@Override
	public GioviObject visitRedirectionToVarDeclaration(RedirectionToVarDeclarationContext ctx) {
		//@todo improve this. Declare variable with value already set
		visit(ctx.varDeclaration());
		switch (ctx.bop.getText()) {
			case ">" -> getCurrentScope()
				.getVariableValue(ctx.varDeclaration().identifier().getText())
				.redirection(visit(ctx.expression()));
			default -> throw new RedirectionException("Unknown redirection operator: " + ctx.bop.getText());
		}
		return null;
	}

	@Override
	public GioviObject visitVarDeclaration(VarDeclarationContext ctx) {
		GioviValue<?> variable = spawnType(ctx.type(), Optional.ofNullable(ctx.defaultDefinition()));
		getCurrentScope().declare(ctx.identifier().getText(), variable);
		return null;
	}

	@Override
	public GioviObject visitRedirectionToIdentifier(RedirectionToIdentifierContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.expression()),
			ctx.redirectionOperator().getText(),
			visit(ctx.identifier()));
		return null;
	}

	@Override
	public GioviObject visitRedirectionToExpression(RedirectionToExpressionContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.expression(0)),
			ctx.redirectionOperator().getText(),
			visit(ctx.expression(1))
		);
		return null;
	}

	@Override
	public GioviObject visitRedirectionToCollectionAccess(RedirectionToCollectionAccessContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.expression()),
			ctx.redirectionOperator().getText(),
			visit(ctx.collectionAccess()));
		return null;
	}


	@Override
	public GioviObject visitRedirectionToPrimitiveDestination(RedirectionToPrimitiveDestinationContext ctx) {
		GioviObject content = visit(ctx.expression());
		switch (ctx.primitiveDestination().getText()) {
			case "\\out" -> System.out.println(content);
			case "\\err" -> System.err.println(content);
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
	public GioviObject visitCollectionAccess(CollectionAccessContext ctx) {
		return getCurrentScope()
			.getVariableValue(ctx.identifier().getText())
			.asGioviCollection()
			.access(visit(ctx.collectionAccessor()));
	}

	@Override
	public GioviObject visitSpecialAccessor(SpecialAccessorContext ctx) {
		return new Accessor(ctx.getText());
	}

	@Override
	public GioviObject visitCollectionLiteralAccessor(CollectionLiteralAccessorContext ctx) {
		return visit(ctx.literal());
	}

	@Override
	public GioviObject visitCollectionParenthesizedAccessor(CollectionParenthesizedAccessorContext ctx) {
		//@todo improve this
		if (ctx.specialAccessor() != null) return visit(ctx.specialAccessor());
		if (ctx.literal() != null) return visit(ctx.literal());
		return visit(ctx.expression());
	}


	@Override
	public GioviObject visitIdentifier(IdentifierContext ctx) {
		return getCurrentScope().getVariableValue(ctx.getText());
	}

	@Override
	public GioviObject visitSequence(SequenceContext ctx) {
		return Sequence.of(
			visit(ctx.start).asGioviValue(),
			visit(ctx.end).asGioviValue(),
			ctx.step == null ? null : visit(ctx.step).asGioviValue());
	}

	@Override
	public GioviObject visitListCreation(ListCreationContext ctx) {
		List<GioviValue<?>> entries = new ArrayList<>();
		for (ListEntryContext entryCtx : ctx.listEntries().listEntry()) {
			GioviObject entry = visit(entryCtx);
			switch (entry) {
				case Sequence<?> seq -> entries.addAll(seq.getValues());
				case GioviValue<?> v -> entries.add(v);
				default -> throw new GioviException("Incompatible type for list creation: " + entry.getClass());
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
		return new GioviInt(Integer.parseInt(ctx.getText()));
	}

	@Override
	public GioviObject visitNaturalLiteral(NaturalLiteralContext ctx) {
		return new GioviNatural(new Natural(Integer.parseInt(ctx.getText())));
	}

	@Override
	public GioviObject visitRealLiteral(RealLiteralContext ctx) {
		return new GioviReal(Double.parseDouble(ctx.getText()));
	}

	@Override
	public GioviObject visitBoolLiteral(BoolLiteralContext ctx) {
		return new GioviBool(Boolean.parseBoolean(ctx.getText()));
	}

	@Override
	public GioviObject visitTextLiteral(TextLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.collect();
	}

	@Override
	public GioviObject visitTextBlockLiteral(TextBlockLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.trimIncidentalWhitespaces()
			.collect();
	}

	@Override
	public GioviObject visitExactTextLiteral(ExactTextLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.collect();
	}

	@Override
	public GioviObject visitExactTextBlockLiteral(ExactTextBlockLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.trimIncidentalWhitespaces()
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
			case "Natural" -> new GioviType(GioviNatural.class);
			case "Int" -> new GioviType(GioviInt.class);
			case "Real" -> new GioviType(GioviReal.class);
			case "Bool" -> new GioviType(GioviBool.class);
			case "Text" -> new GioviType(GioviText.class);
			default -> {
				if (getCurrentScope().isDefinedStruct(typeIdentifier)) {
					yield new GioviType(GioviStruct.class);
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
		return new GioviType(GioviMap.class);
	}

	@Override
	public GioviObject visitFunctionDeclaration(FunctionDeclarationContext ctx) {
		LinkedHashMap<String, GioviFunctionParameter> formalParameters = new LinkedHashMap<>();
		if (ctx.formalParameters() != null) {
			boolean expectParamWithDefault = false;
			for (FormalParameterDefinitionContext formalParamCtx : ctx.formalParameters().formalParameterDefinition()) {
				GioviValue<?> paramValue = spawnType(formalParamCtx.type(), Optional.ofNullable(formalParamCtx.defaultDefinition()));
				if (formalParamCtx.defaultDefinition() == null) {
					if (expectParamWithDefault) {
						throw new GioviException("Expecting default definition for parameter: " + formalParamCtx.identifier().getText());
					}
				} else {
					GioviObject defaultValue = visit(formalParamCtx.defaultDefinition().expression());
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
			ctx.type() == null ? null : visit(ctx.type()).asGioviType().unbox(),
			formalParameters));
		return null;
	}

	private GioviValue<?> spawnType(TypeContext typeCtx, Optional<DefaultDefinitionContext> defaultDefinitionCtx) {
		GioviValue<?> value;
		GioviType type = visit(typeCtx).asGioviType();
		if (type.unbox() == GioviList.class) {
			GioviType entryType = visit(typeCtx.collectionType().listType().type()).asGioviType();
			value = new GioviList(entryType);
		} else if (type.unbox() == GioviMap.class) {
			GioviType keyType = visit(typeCtx.collectionType().mapType().mapKeyType).asGioviType();
			GioviType elementType = visit(typeCtx.collectionType().mapType().mapValueType).asGioviType();
			value = new GioviMap(keyType, elementType);
		} else if (getCurrentScope().isDefinedStruct(typeCtx.getText())) {
			value = getCurrentScope().getDefinitionStruct(typeCtx.getText());
		} else {
			value = type.spawn();
		}
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
				Class<?> returnType = func
					.getReturnType()
					.orElseThrow(() -> new GioviException("Returning in function with no return value"));
				GioviValue<?> returnValue = getCurrentScope().getReturnValue();
				Class<?> returnedType = returnValue.getClass();
				if (returnedType != returnType) {
					throw new GioviException("Wrong return type, expecting " + returnType + " but got " + returnedType);
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
		int functionCallParamCount = ctx.functionParameterList() == null ? 0 : ctx.functionParameterList().functionParameter().size();
		if (functionCallParamCount > func.getParameters().size()) {
			throw new GioviException("Wrong number of parameters for function: " + ctx.identifier().getText());
		}
		if (ctx.functionParameterList() == null || ctx.functionParameterList().functionParameter(0).identifier() == null) {
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
		Iterator<Map.Entry<String, GioviFunctionParameter>> funcIterator = func.getParameters().entrySet().iterator();
		int i = 0;
		while (funcIterator.hasNext()) {
			Map.Entry<String, GioviFunctionParameter> entry = funcIterator.next();
			String formalParameterIdentifier = entry.getKey();
			GioviFunctionParameter formalValue = entry.getValue();
			if (i < funcCallParamsCount) {
				FunctionParameterContext funcParamCtx = funcCallParams.get(i);
				if (funcParamCtx.identifier() != null) {
					throw new GioviException("Unexpected named parameter: " + funcParamCtx.identifier().getText());
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
				throw new GioviException("Duplicate parameter name in function call: " + callParamName);
			}
			if (!funcParams.containsKey(callParamName)) {
				throw new GioviException("Unknown parameter name: " + callParamName);
			}
			funcCallParamsMap.put(callParamName, visit(funcCallParamCtx.expression()).asGioviValue());
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
			if (getCurrentScope().isDefinedStruct(ctx.parentName.getText())) {
				var definition = getCurrentScope().getDefinitionStruct(ctx.parentName.getText());
				struct.putAll((Struct.Named) definition.unbox());
			} else {
				throw new TypeNotFound(ctx.parentName.getText());
			}
		}
		for (var attributeCtx : ctx.structBodyDefinition().structAttributeDefinition()) {
			String identifier = attributeCtx.identifier().getText();
			var attribute = spawnType(attributeCtx.type(), Optional.ofNullable(attributeCtx.defaultDefinition()));
			if (attributeCtx.defaultDefinition() != null) {
				attribute.setDefaultValue(visit(attributeCtx.defaultDefinition()).asGioviValue());
			}
			struct.put(identifier, attribute);
		}
//		for (var structDefinitionCtx : ctx.structBodyDefinition().structDefinition()) {
//			s.put(structDefinitionCtx.structName.getText(), visit(structDefinitionCtx).asGioviStruct());
//		}
//		for (var functionCtx : ctx.structBodyDefinition().functionDeclaration()) {
//			s.put(functionCtx.identifier().getText(), visit(functionCtx).asGioviFunction());
//		}

		getCurrentScope().define(ctx.structName.getText(), new GioviNamedStruct(struct));
		return null;
	}

	@Override
	public GioviObject visitNamedStructCreation(NamedStructCreationContext ctx) {
		var struct = new Struct.Named();
		if (ctx.structType != null) {
			if (!getCurrentScope().isDefinedStruct(ctx.identifier().getText())) {
				throw new TypeNotFound(ctx.identifier().getText());
			}
			var definition = getCurrentScope().getDefinitionStruct(ctx.identifier().getText());
			struct.set(definition.unbox());
			for (var attributeCtx : ctx.namedStructAttribute()) {
				String attributeName = attributeCtx.identifier().getText();
				if (!struct.contains(attributeName)) {
					throw new IncompatibleStructs("Struct '" + ctx.structType.getText() + "' doesn't have an attribute '" + attributeName + "'");
				}
				struct.get(attributeName).box(visit(attributeCtx.expression()).asGioviValue());
			}
		} else {
			for (var attributeCtx : ctx.namedStructAttribute()) {
				struct.put(attributeCtx.identifier().getText(), visit(attributeCtx.expression()).asGioviValue());
			}
		}
		return new GioviNamedStruct(struct);
	}

	@Override
	public GioviObject visitUnnamedStructCreation(UnnamedStructCreationContext ctx) {
		var struct = new Struct.Unnamed();
		if (ctx.structType != null) {
			if (!getCurrentScope().isDefinedStruct(ctx.identifier().getText())) {
				throw new TypeNotFound(ctx.identifier().getText());
			}
			var definition = getCurrentScope().getDefinitionStruct(ctx.identifier().getText());
			struct.set(definition.unbox());
			for (var attributeCtx : ctx.unnamedStructAttribute()) {
				// TODO check that fields are valid?
				struct.put(visit(attributeCtx.expression()).asGioviValue());
			}
		} else {
			for (var attributeCtx : ctx.unnamedStructAttribute()) {
				struct.put(visit(attributeCtx.expression()).asGioviValue());
			}
		}
		return new GioviUnnamedStruct(struct);
	}

	//	@Override
//	public GioviObject visitStructCreation(StructCreationContext ctx) {
//		GioviStruct struct = new GioviStruct();
//		if (ctx.identifier() != null) {
//			if (getCurrentScope().isDefinedStruct(ctx.identifier().getText())) {
//				var definition = getCurrentScope().getDefinitionStruct(ctx.identifier().getText());
//				struct.unbox().putAll(definition.unbox());
//			} else {
//				throw new TypeNotFound(ctx.identifier().getText());
//			}
//		}
//		for (var attributeCtx : ctx.structAttribute()) {
//			String identifier = attributeCtx.identifier() != null
//				? attributeCtx.identifier().getText()
//				: "" + struct.getLastIndex();
//			struct.put(identifier, visit(attributeCtx.expression()).asGioviValue());
//		}
//		return struct;
//	}

	@Override
	public GioviObject visitDotExpression(DotExpressionContext ctx) {
		var lhs = visit(ctx.expression());
		return ctx.identifier() != null
			? lhs.dot(ctx.identifier().getText())
			: lhs.dot(getCurrentScope().getDefinitionFunction(ctx.functionCall().identifier().getText()));
	}
}
