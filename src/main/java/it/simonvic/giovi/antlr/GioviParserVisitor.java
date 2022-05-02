// Generated from /home/simonvic/Documents/Productivity/Programming/JAVA/IdeaProjects/Giovi/src/main/antlr4/it/simonvic/giovi/GioviParser.g4 by ANTLR 4.10.1
package it.simonvic.giovi.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GioviParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GioviParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GioviParser#begin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBegin(GioviParser.BeginContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(GioviParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(GioviParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(GioviParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#repeatStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatStatement(GioviParser.RepeatStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(GioviParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(GioviParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#breakStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(GioviParser.BreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#continueStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStatement(GioviParser.ContinueStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code redirectionToVarDeclaration}
	 * labeled alternative in {@link GioviParser#redirection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRedirectionToVarDeclaration(GioviParser.RedirectionToVarDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code redirectionToPrimitiveDestination}
	 * labeled alternative in {@link GioviParser#redirection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRedirectionToPrimitiveDestination(GioviParser.RedirectionToPrimitiveDestinationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code redirectionToIdentifier}
	 * labeled alternative in {@link GioviParser#redirection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRedirectionToIdentifier(GioviParser.RedirectionToIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#constantDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantDeclaration(GioviParser.ConstantDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#varDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDeclaration(GioviParser.VarDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#defaultDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefaultDefinition(GioviParser.DefaultDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#structDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructDefinition(GioviParser.StructDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#nestedStructDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedStructDefinition(GioviParser.NestedStructDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#structAttributeDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructAttributeDefinition(GioviParser.StructAttributeDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#structCreation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructCreation(GioviParser.StructCreationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#structAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructAttribute(GioviParser.StructAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#functionDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDeclaration(GioviParser.FunctionDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#formalParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameters(GioviParser.FormalParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#formalParameterDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameterDefinition(GioviParser.FormalParameterDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(GioviParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#functionParameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionParameterList(GioviParser.FunctionParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#functionParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionParameter(GioviParser.FunctionParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(GioviParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#switchStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchStatement(GioviParser.SwitchStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#switchBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchBlock(GioviParser.SwitchBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#switchLabel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchLabel(GioviParser.SwitchLabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#caseMatch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseMatch(GioviParser.CaseMatchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code primaryExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpression(GioviParser.PrimaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dotExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDotExpression(GioviParser.DotExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolExpression(GioviParser.BoolExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mapCreationExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapCreationExpression(GioviParser.MapCreationExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prefixedExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixedExpression(GioviParser.PrefixedExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code additiveExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(GioviParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relationalExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(GioviParser.RelationalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code structCreationExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructCreationExpression(GioviParser.StructCreationExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpression(GioviParser.NotExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code containmentExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContainmentExpression(GioviParser.ContainmentExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multiplicativeExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(GioviParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code powerExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPowerExpression(GioviParser.PowerExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code listCreationExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListCreationExpression(GioviParser.ListCreationExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesizedExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesizedExpression(GioviParser.ParenthesizedExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code equalityExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(GioviParser.EqualityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionCallExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallExpression(GioviParser.FunctionCallExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code collectionAccessExpression}
	 * labeled alternative in {@link GioviParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollectionAccessExpression(GioviParser.CollectionAccessExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(GioviParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#sequence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSequence(GioviParser.SequenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#primitiveDestination}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveDestination(GioviParser.PrimitiveDestinationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(GioviParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#structType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructType(GioviParser.StructTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#collectionType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollectionType(GioviParser.CollectionTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#collectionAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollectionAccess(GioviParser.CollectionAccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#collectionAccessor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollectionAccessor(GioviParser.CollectionAccessorContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList(GioviParser.ListContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#listCreation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListCreation(GioviParser.ListCreationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#listEntries}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListEntries(GioviParser.ListEntriesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#listEntry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListEntry(GioviParser.ListEntryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#listAccessor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListAccessor(GioviParser.ListAccessorContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#map}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMap(GioviParser.MapContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#mapCreation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapCreation(GioviParser.MapCreationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#mapEntries}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapEntries(GioviParser.MapEntriesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#mapEntry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapEntry(GioviParser.MapEntryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#mapAccessor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapAccessor(GioviParser.MapAccessorContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(GioviParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(GioviParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#integerLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerLiteral(GioviParser.IntegerLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#naturalLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNaturalLiteral(GioviParser.NaturalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#realLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRealLiteral(GioviParser.RealLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#boolLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolLiteral(GioviParser.BoolLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#textBlockLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextBlockLiteral(GioviParser.TextBlockLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#textLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextLiteral(GioviParser.TextLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#exactTextBlockLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExactTextBlockLiteral(GioviParser.ExactTextBlockLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#exactTextLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExactTextLiteral(GioviParser.ExactTextLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#nullLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullLiteral(GioviParser.NullLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GioviParser#defaultLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefaultLiteral(GioviParser.DefaultLiteralContext ctx);
}