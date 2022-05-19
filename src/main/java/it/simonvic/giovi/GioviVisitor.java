package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.exceptions.*;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.LoopScope;
import it.simonvic.giovi.lang.Scope;
import it.simonvic.giovi.lang.Sequence;
import it.simonvic.giovi.lang.functions.FunctionScope;
import it.simonvic.giovi.lang.functions.GioviFunction;
import it.simonvic.giovi.lang.functions.GioviFunctionParameter;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.util.OperationEvaluator;
import it.simonvic.giovi.util.TextProcessor;
import it.simonvic.giovi.visitors.SwitchHelperVisitor;

import java.util.*;

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
	public GioviObject visitIfStatement(GioviParser.IfStatementContext ctx) {
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
	public GioviObject visitBlock(GioviParser.BlockContext ctx) {
		for (GioviParser.StatementContext statementContext : ctx.statement()) {
			visit(statementContext);
			Scope s = getCurrentScope();
			if (s.shouldBreak() || s.shouldReturn() || s.shouldContinue()) {
				break;
			}
		}
		return null;
	}

	@Override
	public GioviObject visitSwitchStatement(GioviParser.SwitchStatementContext ctx) {
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
	public GioviObject visitBreakStatement(GioviParser.BreakStatementContext ctx) {
		getCurrentScope().doBreak();
		return null;
	}

	@Override
	public GioviObject visitContinueStatement(GioviParser.ContinueStatementContext ctx) {
		getCurrentScope().doContinue();
		return null;
	}

	@Override
	public GioviObject visitForStatement(GioviParser.ForStatementContext ctx) {
		GioviObject collection = visit(ctx.collection);
		switch (collection) {
			case GioviList<?> list -> forList(ctx.identifier(), list, ctx.block());
			case GioviMap<?, ?> map -> forMap(ctx.identifier(), map, ctx.block());
			default -> throw new OperatorNotSupported("for");
		}
		return null;
	}

	private void forList(List<GioviParser.IdentifierContext> iterators, GioviList<?> list, GioviParser.BlockContext block) {
		if (iterators.size() != 1) throw new GioviException("Wrong number of iterators");
		if (list.unbox().size() == 0) return;


		for (GioviValue<?> value : list.unbox()) {

			enterLoopScope();

			//@todo update when structs are implemented
			String iteratorName = iterators.get(0).getText();
			getCurrentScope().declare(iteratorName, value); //@todo iterator should be by value?
			GioviValue<?> iterator = getCurrentScope().getVariableValue(iteratorName);
			iterator.box(value);

			visit(block);

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

	private void forMap(List<GioviParser.IdentifierContext> iterators, GioviMap<?, ?> map, GioviParser.BlockContext block) {
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

			visit(block);

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
	public GioviObject visitRepeatStatement(GioviParser.RepeatStatementContext ctx) {
		enterLoopScope();
		boolean infiniteRepeat = ctx.IF() == null;
		while (infiniteRepeat || visit(ctx.expression()).asBoolean()) {
			enterLoopScope();
			visit(ctx.block());

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
	public GioviObject visitRedirectionToVarDeclaration(GioviParser.RedirectionToVarDeclarationContext ctx) {
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
	public GioviObject visitVarDeclaration(GioviParser.VarDeclarationContext ctx) {
		getCurrentScope().declare(ctx.identifier().getText(), visit(ctx.type()).asGioviValue());
		return null;
	}

	@Override
	public GioviObject visitRedirectionToIdentifier(GioviParser.RedirectionToIdentifierContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.identifier()),
			ctx.redirectionOperator().getText(),
			visit(ctx.expression()));
		return null;
	}

	@Override
	public GioviObject visitRedirectionToCollectionAccess(GioviParser.RedirectionToCollectionAccessContext ctx) {
		OperationEvaluator.redirection(
			visit(ctx.collectionAccess()),
			ctx.redirectionOperator().getText(),
			visit(ctx.expression()));
		return null;
	}


	@Override
	public GioviObject visitRedirectionToPrimitiveDestination(GioviParser.RedirectionToPrimitiveDestinationContext ctx) {
		GioviObject content = visit(ctx.expression());
		switch (ctx.primitiveDestination().getText()) {
			case "\\", "\\out" -> System.out.println(content);
			case "\\error" -> System.err.println(content);
		}
		return null;
	}

	@Override
	public GioviObject visitParenthesizedExpression(GioviParser.ParenthesizedExpressionContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public GioviObject visitPrefixedExpression(GioviParser.PrefixedExpressionContext ctx) {
		return OperationEvaluator.prefixed(
			visit(ctx.expression()),
			ctx.prefix.getText());
	}

	@Override
	public GioviObject visitAdditiveExpression(GioviParser.AdditiveExpressionContext ctx) {
		return OperationEvaluator.additive(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitMultiplicativeExpression(GioviParser.MultiplicativeExpressionContext ctx) {
		return OperationEvaluator.multiplicative(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitPowerExpression(GioviParser.PowerExpressionContext ctx) {
		return visit(ctx.expression(0)).power(visit(ctx.expression(1)));
	}

	@Override
	public GioviObject visitRelationalExpression(GioviParser.RelationalExpressionContext ctx) {
		return OperationEvaluator.relational(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitNotExpression(GioviParser.NotExpressionContext ctx) {
		return visit(ctx.expression()).not();
	}

	@Override
	public GioviObject visitBoolExpression(GioviParser.BoolExpressionContext ctx) {
		return OperationEvaluator.bool(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitEqualityExpression(GioviParser.EqualityExpressionContext ctx) {
		return OperationEvaluator.equality(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitContainmentExpression(GioviParser.ContainmentExpressionContext ctx) {
		return OperationEvaluator.containment(
			visit(ctx.expression(0)),
			visit(ctx.expression(1)),
			ctx.bop.getText());
	}

	@Override
	public GioviObject visitCollectionAccess(GioviParser.CollectionAccessContext ctx) {
		return getCurrentScope()
			.getVariableValue(ctx.identifier().getText())
			.asGioviCollection()
			.access(visit(ctx.collectionAccessor()));
	}

	@Override
	public GioviObject visitSpecialAccessor(GioviParser.SpecialAccessorContext ctx) {
		return new Accessor(ctx.getText());
	}

	@Override
	public GioviObject visitCollectionLiteralAccessor(GioviParser.CollectionLiteralAccessorContext ctx) {
		return visit(ctx.literal());
	}

	@Override
	public GioviObject visitCollectionParenthesizedAccessor(GioviParser.CollectionParenthesizedAccessorContext ctx) {
		//@todo improve this
		if (ctx.specialAccessor() != null) return visit(ctx.specialAccessor());
		if (ctx.literal() != null) return visit(ctx.literal());
		return visit(ctx.expression());
	}


	@Override
	public GioviObject visitIdentifier(GioviParser.IdentifierContext ctx) {
		return getCurrentScope().getVariableValue(ctx.getText());
	}

	@Override
	public GioviObject visitSequence(GioviParser.SequenceContext ctx) {
		return Sequence.of(
			visit(ctx.start).asGioviValue(),
			visit(ctx.end).asGioviValue(),
			ctx.step == null ? null : visit(ctx.step).asGioviValue());
	}

	@Override
	public GioviObject visitListCreation(GioviParser.ListCreationContext ctx) {
		GioviList<GioviValue<?>> list = new GioviList<>();
		for (GioviParser.ListEntryContext entryCtx : ctx.listEntries().listEntry()) {
			GioviObject entry = visit(entryCtx);
			switch (entry) {
				case Sequence<?> seq -> list.addAll(seq.getValues());
				case GioviValue<?> v -> list.add(v);
				default -> throw new IncompatibleTypes(entry.getClass(), list.getClass());
			}
		}
		return list;
	}

	@Override
	public GioviObject visitMapCreation(GioviParser.MapCreationContext ctx) {
		GioviMap<GioviValue<?>, GioviValue<?>> map = new GioviMap<>();
		for (GioviParser.MapEntryContext entryCtx : ctx.mapEntries().mapEntry()) {
			map.put(visit(entryCtx.mapKey).asGioviValue(), visit(entryCtx.mapValue).asGioviValue());
		}
		return map;
	}

	@Override
	public GioviObject visitIntegerLiteral(GioviParser.IntegerLiteralContext ctx) {
		return new GioviInt(Integer.parseInt(ctx.getText()));
	}

	@Override
	public GioviObject visitNaturalLiteral(GioviParser.NaturalLiteralContext ctx) {
		return new GioviNatural(new Natural(Integer.parseInt(ctx.getText())));
	}

	@Override
	public GioviObject visitRealLiteral(GioviParser.RealLiteralContext ctx) {
		return new GioviReal(Double.parseDouble(ctx.getText()));
	}

	@Override
	public GioviObject visitBoolLiteral(GioviParser.BoolLiteralContext ctx) {
		return new GioviBool(Boolean.parseBoolean(ctx.getText()));
	}

	@Override
	public GioviObject visitTextLiteral(GioviParser.TextLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.collect();
	}

	@Override
	public GioviObject visitTextBlockLiteral(GioviParser.TextBlockLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.injectVariables(getCurrentScope())
			.injectExpressions(this)
			.trimIncidentalWhitespaces()
			.collect();
	}

	@Override
	public GioviObject visitExactTextLiteral(GioviParser.ExactTextLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(1)
			.collect();
	}

	@Override
	public GioviObject visitExactTextBlockLiteral(GioviParser.ExactTextBlockLiteralContext ctx) {
		return TextProcessor.of(ctx.getText())
			.trimDelimiters(2)
			.trimIncidentalWhitespaces()
			.collect();
	}

	@Override
	public GioviObject visitNullLiteral(GioviParser.NullLiteralContext ctx) {
		return null;
	}

	@Override
	public GioviObject visitDefaultLiteral(GioviParser.DefaultLiteralContext ctx) {
		return new GioviDefault();
	}

	@Override
	public GioviObject visitType(GioviParser.TypeContext ctx) {
		if (ctx.collectionType() != null) {
			return visit(ctx.collectionType());
		}

		return switch (ctx.identifier().getText()) {
			case "Natural" -> new GioviNatural();
			case "Int" -> new GioviInt();
			case "Real" -> new GioviReal();
			case "Bool" -> new GioviBool();
			case "Text" -> new GioviText();
			default -> throw new TypeNotFound(ctx.getText());
		};
	}

	@Override
	public GioviObject visitCollectionType(GioviParser.CollectionTypeContext ctx) {
		if (ctx.list() != null) {
			return new GioviList<>();
		}

		if (ctx.map() != null) {
			return new GioviMap<>();
		}

		throw new TypeNotFound(ctx.getText());
	}

	@Override
	public GioviObject visitFunctionDeclaration(GioviParser.FunctionDeclarationContext ctx) {
		LinkedHashMap<String, GioviFunctionParameter> formalParameters = new LinkedHashMap<>();
		if (ctx.formalParameters() != null) {
			boolean expectParamWithDefault = false;
			for (GioviParser.FormalParameterDefinitionContext formalParamCtx : ctx.formalParameters().formalParameterDefinition()) {
				GioviValue<?> paramValue = visit(formalParamCtx.type()).asGioviValue();
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
		getCurrentScope().declare(ctx.identifier().getText(), new GioviFunction(
			ctx.block(),
			ctx.type() == null ? null : visit(ctx.type()).asGioviValue().getType(),
			formalParameters));
		return null;
	}

	@Override
	public GioviObject visitFunctionCall(GioviParser.FunctionCallContext ctx) {
		GioviFunction func = getCurrentScope().getFunction(ctx.identifier().getText());
		enterFunctionScope();
		setupFunctionParameters(ctx, func);
		GioviValue<?> returnValue = visitFunction(func);
		exitScope();
		return returnValue;
	}

	private GioviValue<?> visitFunction(GioviFunction func) {
		for (GioviParser.StatementContext statement : func.getBody().statement()) {
			visit(statement);
			if (getCurrentScope().shouldReturn()) {
				Class<?> returnType = func
					.getReturnType()
					.orElseThrow(() -> new GioviException("Returning in function with no return value"));
				GioviValue<?> returnValue = getCurrentScope().getReturnValue();
				if (returnValue.getType() != returnType) {
					throw new GioviException("Wrong return type");
				}
				return returnValue;
			}
		}
		if (func.getReturnType().isPresent()) {
			throw new GioviException("Missing return statement in function");
		}
		return null;
	}

	private void setupFunctionParameters(GioviParser.FunctionCallContext ctx, GioviFunction func) {
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

	private void setupFunctionParametersIteratively(GioviParser.FunctionCallContext ctx, GioviFunction func) {
		List<GioviParser.FunctionParameterContext> funcCallParams = ctx.functionParameterList() == null
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
				GioviParser.FunctionParameterContext funcParamCtx = funcCallParams.get(i);
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

	private void setupNamedFunctionParameters(GioviParser.FunctionCallContext ctx, GioviFunction func) {
		List<GioviParser.FunctionParameterContext> funcCallParams = ctx.functionParameterList() == null
			? List.of()
			: ctx.functionParameterList().functionParameter();
		LinkedHashMap<String, GioviFunctionParameter> funcParams = func.getParameters();

		Map<String, GioviValue<?>> funcCallParamsMap = new HashMap<>();
		for (GioviParser.FunctionParameterContext funcCallParamCtx : funcCallParams) {
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
	public GioviObject visitReturnStatement(GioviParser.ReturnStatementContext ctx) {
		GioviValue<?> returnValue = visit(ctx.expression()).asGioviValue();
		getCurrentScope().doReturn(returnValue);
		return returnValue;
	}
}