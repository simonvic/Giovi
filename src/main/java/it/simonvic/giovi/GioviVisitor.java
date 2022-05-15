package it.simonvic.giovi;

import it.simonvic.giovi.antlr.GioviLexer;
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
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
	public GioviObject visitBlock(GioviParser.BlockContext ctx) {
		for (GioviParser.StatementContext statementContext : ctx.statement()) {
			if (getCurrentScope().shouldBreak()) {
				break;
			}

			if (getCurrentScope().shouldContinue()) {
				getCurrentScope().onContinued();
				break;
			}
			visit(statementContext);
		}
		return null;
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

			if (getCurrentScope().shouldBreak()) {
				break;
			}

			if (getCurrentScope().shouldContinue()) {
				getCurrentScope().onContinued();
				continue;
			}

			enterLoopScope();

			String iteratorName = iterators.get(0).getText();
			getCurrentScope().declare(iteratorName, value); //@todo iterator should be by value?
			GioviValue<?> iterator = getCurrentScope().getVariableValue(iteratorName);

			iterator.box(value);
			visit(block);
			exitScope();
		}

	}

	private void forMap(List<GioviParser.IdentifierContext> iterators, GioviMap<?, ?> map, GioviParser.BlockContext block) {
		if (iterators.size() != 2) throw new GioviException("Wrong number of iterators");
		if (map.unbox().size() == 0) return;

		for (Map.Entry<? extends GioviValue<?>, ? extends GioviValue<?>> entry : map.unbox().entrySet()) {

			if (getCurrentScope().shouldBreak()) {
				break;
			}

			if (getCurrentScope().shouldContinue()) {
				getCurrentScope().onContinued();
				continue;
			}

			enterLoopScope();

			String keyIteratorName = iterators.get(0).getText();
			String elementIteratorName = iterators.get(1).getText();

			//@todo iterators should be by value?
			getCurrentScope().declare(keyIteratorName, entry.getKey());
			getCurrentScope().declare(elementIteratorName, entry.getValue());
			getCurrentScope().getVariableValue(keyIteratorName).box(entry.getKey());
			getCurrentScope().getVariableValue(elementIteratorName).box(entry.getValue());
			visit(block);

			exitScope();
		}

	}

	@Override
	public GioviObject visitRepeatStatement(GioviParser.RepeatStatementContext ctx) {
		enterLoopScope();
		boolean infiniteRepeat = ctx.IF() == null;
		while (infiniteRepeat || visit(ctx.expression()).asBoolean()) {
			if (getCurrentScope().shouldBreak()) {
				break;
			}
			if (getCurrentScope().shouldContinue()) {
				getCurrentScope().onContinued();
				continue;
			}
			enterLoopScope();
			visit(ctx.block());
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
		String identifier = ctx.identifier().getText();
		switch (ctx.bop.getText()) {
			case ">" ->
				getCurrentScope().getVariableValue(identifier).redirection(visit(ctx.expression())).asGioviValue();
			case ">+" ->
				getCurrentScope().getVariableValue(identifier).redirectionPlus(visit(ctx.expression())).asGioviValue();
			case ">-" ->
				getCurrentScope().getVariableValue(identifier).redirectionMinus(visit(ctx.expression())).asGioviValue();
			case ">*" ->
				getCurrentScope().getVariableValue(identifier).redirectionMultiply(visit(ctx.expression())).asGioviValue();
			case ">/" ->
				getCurrentScope().getVariableValue(identifier).redirectionDivide(visit(ctx.expression())).asGioviValue();
			default -> throw new RedirectionException("Unknown redirection operator: " + ctx.bop.getText());
		}
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
	public GioviObject visitExpressionAccessor(GioviParser.ExpressionAccessorContext ctx) {
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
					paramValue.setDefaultValue(visit(formalParamCtx.defaultDefinition().expression()).asGioviValue());
					expectParamWithDefault = true;
				}
				formalParameters.put(
					formalParamCtx.identifier().getText(),
					new GioviFunctionParameter(paramValue, expectParamWithDefault));
			}
		}
		getCurrentScope().declare(ctx.identifier().getText(), new GioviFunction(
			ctx.block(),
			ctx.type() == null ? null : visit(ctx.type()).asGioviValue(),
			formalParameters));
		return null;
	}

	@Override
	public GioviObject visitFunctionCallUnnamed(GioviParser.FunctionCallUnnamedContext ctx) {
		GioviFunction func = getCurrentScope().getFunction(ctx.identifier().getText());
		int functionCallParamCount = ctx.functionParameterList() == null ? 0 : ctx.functionParameterList().functionParameter().size();
		int functionParamCount = func.getParameters().size();
		if (functionCallParamCount != functionParamCount) {
			throw new GioviException("Wrong number of parameters for function: " + ctx.identifier().getText());
		}
		enterFunctionScope();
		Iterator<Map.Entry<String, GioviFunctionParameter>> iterator = func.getParameters().entrySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Map.Entry<String, GioviFunctionParameter> entry = iterator.next();
			String formalParameterIdentifier = entry.getKey();
			GioviFunctionParameter formalValue = entry.getValue();
			GioviParser.FunctionParameterContext functionParam = ctx.functionParameterList().functionParameter(i);
			formalValue.value().box(visit(functionParam.expression()).asGioviValue());
			getCurrentScope().declare(formalParameterIdentifier, formalValue.value());
			i++;
		}
		for (GioviParser.StatementContext statement : func.getBody().statement()) {
			visit(statement);
			if (getCurrentScope().shouldReturn()) {
				func.getReturnValue()
					.orElseThrow(() -> new GioviException("Returning in function with no return value"))
					.box(getCurrentScope().getReturnValue());
				break;
			}
		}
		exitScope();
		return func.getReturnValue().orElse(null);
	}


	@Override
	public GioviObject visitReturnStatement(GioviParser.ReturnStatementContext ctx) {
		GioviValue<?> returnValue = visit(ctx.expression()).asGioviValue();
		getCurrentScope().doReturn(returnValue);
		return returnValue;
	}
}