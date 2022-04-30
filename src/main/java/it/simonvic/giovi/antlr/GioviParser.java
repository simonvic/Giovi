// Generated from /home/simonvic/Documents/Productivity/Programming/JAVA/IdeaProjects/Giovi/src/main/antlr4/it/simonvic/giovi/GioviParser.g4 by ANTLR 4.10.1
package it.simonvic.giovi.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GioviParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.10.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PLUS=1, MINUS=2, MULTIPLICATION=3, DIVISION=4, MODULO=5, EXPONENTIAL=6, 
		REDIRECTION_PLUS=7, REDIRECTION_MINUS=8, REDIRECTION_MULTIPLICATION=9, 
		REDIRECTION_DIVISION=10, EQUAL=11, NOT_EQUAL=12, TILDE=13, EQUAL_TILDE=14, 
		TILDE_EQUAL=15, GREATER_EQUAL=16, LESS_EQUAL=17, NOT=18, AND=19, OR=20, 
		BRACKET_ROUND_OPEN=21, BRACKET_ROUND_CLOSE=22, BRACKET_SQUARE_OPEN=23, 
		BRACKET_SQUARE_CLOSE=24, BRACKET_CURLY_OPEN=25, BRACKET_CURLY_CLOSE=26, 
		BRACKET_ANGULAR_OPEN=27, BRACKET_ANGULAR_CLOSE=28, DOT=29, SEQUENCE=30, 
		COMMA=31, UNDERSCORE=32, BACKSLASH=33, COLON=34, HASHTAG=35, AT=36, IF=37, 
		DO=38, ELSE=39, FOR=40, IN=41, REPEAT=42, BREAK=43, CONTINUE=44, CONSTANT=45, 
		TYPE_NATURAL=46, TYPE_INT=47, TYPE_REAL=48, TYPE_BOOL=49, TYPE_TEXT=50, 
		TYPE_LIST=51, TYPE_MAP=52, NULL=53, RETURN=54, WS=55, COMMENT=56, COMMENT_BLOCK=57, 
		INTEGER_LITERAL=58, NATURAL_LITERAL=59, REAL_LITERAL=60, BOOL_LITERAL=61, 
		TEXT_BLOCK_LITERAL=62, TEXT_LITERAL=63, EXACT_TEXT_BLOCK_LITERAL=64, EXACT_TEXT_LITERAL=65, 
		IDENTIFIER=66, DEST_STDOUT=67, DEST_STDERR=68;
	public static final int
		RULE_begin = 0, RULE_program = 1, RULE_block = 2, RULE_statement = 3, 
		RULE_repeatStatement = 4, RULE_forStatement = 5, RULE_returnStatement = 6, 
		RULE_breakStatement = 7, RULE_continueStatement = 8, RULE_redirection = 9, 
		RULE_constantDeclaration = 10, RULE_varDeclaration = 11, RULE_defaultDefinition = 12, 
		RULE_structDefinition = 13, RULE_nestedStructDefinition = 14, RULE_structAttributeDefinition = 15, 
		RULE_structCreation = 16, RULE_structAttribute = 17, RULE_functionDeclaration = 18, 
		RULE_formalParameters = 19, RULE_formalParameterDefinition = 20, RULE_functionCall = 21, 
		RULE_functionParameterList = 22, RULE_functionParameter = 23, RULE_ifStatement = 24, 
		RULE_switchStatement = 25, RULE_switchBlock = 26, RULE_switchLabel = 27, 
		RULE_caseMatch = 28, RULE_expression = 29, RULE_primary = 30, RULE_sequence = 31, 
		RULE_primitiveDestination = 32, RULE_stdout = 33, RULE_stderr = 34, RULE_type = 35, 
		RULE_structType = 36, RULE_primitiveStructType = 37, RULE_collectionAccess = 38, 
		RULE_collectionAccessor = 39, RULE_list = 40, RULE_listCreation = 41, 
		RULE_listEntries = 42, RULE_listEntry = 43, RULE_listAccessor = 44, RULE_map = 45, 
		RULE_mapCreation = 46, RULE_mapEntries = 47, RULE_mapEntry = 48, RULE_mapAccessor = 49, 
		RULE_primitiveType = 50, RULE_literal = 51, RULE_identifier = 52, RULE_integerLiteral = 53, 
		RULE_naturalLiteral = 54, RULE_realLiteral = 55, RULE_boolLiteral = 56, 
		RULE_textBlockLiteral = 57, RULE_textLiteral = 58, RULE_exactTextBlockLiteral = 59, 
		RULE_exactTextLiteral = 60, RULE_nullLiteral = 61, RULE_defaultLiteral = 62;
	private static String[] makeRuleNames() {
		return new String[] {
			"begin", "program", "block", "statement", "repeatStatement", "forStatement", 
			"returnStatement", "breakStatement", "continueStatement", "redirection", 
			"constantDeclaration", "varDeclaration", "defaultDefinition", "structDefinition", 
			"nestedStructDefinition", "structAttributeDefinition", "structCreation", 
			"structAttribute", "functionDeclaration", "formalParameters", "formalParameterDefinition", 
			"functionCall", "functionParameterList", "functionParameter", "ifStatement", 
			"switchStatement", "switchBlock", "switchLabel", "caseMatch", "expression", 
			"primary", "sequence", "primitiveDestination", "stdout", "stderr", "type", 
			"structType", "primitiveStructType", "collectionAccess", "collectionAccessor", 
			"list", "listCreation", "listEntries", "listEntry", "listAccessor", "map", 
			"mapCreation", "mapEntries", "mapEntry", "mapAccessor", "primitiveType", 
			"literal", "identifier", "integerLiteral", "naturalLiteral", "realLiteral", 
			"boolLiteral", "textBlockLiteral", "textLiteral", "exactTextBlockLiteral", 
			"exactTextLiteral", "nullLiteral", "defaultLiteral"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'+'", "'-'", "'*'", "'/'", "'%'", "'^'", "'>+'", "'>-'", "'>*'", 
			"'>/'", "'='", "'!='", "'~'", "'=~'", "'~='", "'>='", "'<='", "'!'", 
			"'&'", "'|'", "'('", "')'", "'['", "']'", "'{'", "'}'", "'<'", "'>'", 
			"'.'", "'..'", "','", "'_'", "'\\'", "':'", "'#'", "'@'", "'if'", "'do'", 
			"'else'", "'for'", "'in'", "'repeat'", "'break'", "'continue'", "'constant'", 
			"'Natural'", "'Int'", "'Real'", "'Bool'", "'Text'", "'List'", "'Map'", 
			"'null'", "'=>'", null, null, null, null, null, null, null, null, null, 
			null, null, null, "'\\out'", "'\\error'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PLUS", "MINUS", "MULTIPLICATION", "DIVISION", "MODULO", "EXPONENTIAL", 
			"REDIRECTION_PLUS", "REDIRECTION_MINUS", "REDIRECTION_MULTIPLICATION", 
			"REDIRECTION_DIVISION", "EQUAL", "NOT_EQUAL", "TILDE", "EQUAL_TILDE", 
			"TILDE_EQUAL", "GREATER_EQUAL", "LESS_EQUAL", "NOT", "AND", "OR", "BRACKET_ROUND_OPEN", 
			"BRACKET_ROUND_CLOSE", "BRACKET_SQUARE_OPEN", "BRACKET_SQUARE_CLOSE", 
			"BRACKET_CURLY_OPEN", "BRACKET_CURLY_CLOSE", "BRACKET_ANGULAR_OPEN", 
			"BRACKET_ANGULAR_CLOSE", "DOT", "SEQUENCE", "COMMA", "UNDERSCORE", "BACKSLASH", 
			"COLON", "HASHTAG", "AT", "IF", "DO", "ELSE", "FOR", "IN", "REPEAT", 
			"BREAK", "CONTINUE", "CONSTANT", "TYPE_NATURAL", "TYPE_INT", "TYPE_REAL", 
			"TYPE_BOOL", "TYPE_TEXT", "TYPE_LIST", "TYPE_MAP", "NULL", "RETURN", 
			"WS", "COMMENT", "COMMENT_BLOCK", "INTEGER_LITERAL", "NATURAL_LITERAL", 
			"REAL_LITERAL", "BOOL_LITERAL", "TEXT_BLOCK_LITERAL", "TEXT_LITERAL", 
			"EXACT_TEXT_BLOCK_LITERAL", "EXACT_TEXT_LITERAL", "IDENTIFIER", "DEST_STDOUT", 
			"DEST_STDERR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "GioviParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public GioviParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class BeginContext extends ParserRuleContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public TerminalNode EOF() { return getToken(GioviParser.EOF, 0); }
		public BeginContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_begin; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitBegin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BeginContext begin() throws RecognitionException {
		BeginContext _localctx = new BeginContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_begin);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			program();
			setState(127);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProgramContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT) | (1L << BRACKET_ROUND_OPEN) | (1L << BRACKET_SQUARE_OPEN) | (1L << BRACKET_CURLY_OPEN) | (1L << UNDERSCORE) | (1L << IF) | (1L << FOR) | (1L << REPEAT) | (1L << BREAK) | (1L << CONTINUE) | (1L << CONSTANT) | (1L << NULL) | (1L << RETURN) | (1L << INTEGER_LITERAL) | (1L << NATURAL_LITERAL) | (1L << REAL_LITERAL) | (1L << BOOL_LITERAL) | (1L << TEXT_BLOCK_LITERAL) | (1L << TEXT_LITERAL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (EXACT_TEXT_BLOCK_LITERAL - 64)) | (1L << (EXACT_TEXT_LITERAL - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
				{
				{
				setState(129);
				statement();
				}
				}
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public TerminalNode BRACKET_CURLY_OPEN() { return getToken(GioviParser.BRACKET_CURLY_OPEN, 0); }
		public TerminalNode BRACKET_CURLY_CLOSE() { return getToken(GioviParser.BRACKET_CURLY_CLOSE, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			match(BRACKET_CURLY_OPEN);
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT) | (1L << BRACKET_ROUND_OPEN) | (1L << BRACKET_SQUARE_OPEN) | (1L << BRACKET_CURLY_OPEN) | (1L << UNDERSCORE) | (1L << IF) | (1L << FOR) | (1L << REPEAT) | (1L << BREAK) | (1L << CONTINUE) | (1L << CONSTANT) | (1L << NULL) | (1L << RETURN) | (1L << INTEGER_LITERAL) | (1L << NATURAL_LITERAL) | (1L << REAL_LITERAL) | (1L << BOOL_LITERAL) | (1L << TEXT_BLOCK_LITERAL) | (1L << TEXT_LITERAL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (EXACT_TEXT_BLOCK_LITERAL - 64)) | (1L << (EXACT_TEXT_LITERAL - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
				{
				{
				setState(136);
				statement();
				}
				}
				setState(141);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(142);
			match(BRACKET_CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public IfStatementContext ifStatement() {
			return getRuleContext(IfStatementContext.class,0);
		}
		public SwitchStatementContext switchStatement() {
			return getRuleContext(SwitchStatementContext.class,0);
		}
		public RepeatStatementContext repeatStatement() {
			return getRuleContext(RepeatStatementContext.class,0);
		}
		public ForStatementContext forStatement() {
			return getRuleContext(ForStatementContext.class,0);
		}
		public ReturnStatementContext returnStatement() {
			return getRuleContext(ReturnStatementContext.class,0);
		}
		public BreakStatementContext breakStatement() {
			return getRuleContext(BreakStatementContext.class,0);
		}
		public ContinueStatementContext continueStatement() {
			return getRuleContext(ContinueStatementContext.class,0);
		}
		public RedirectionContext redirection() {
			return getRuleContext(RedirectionContext.class,0);
		}
		public StructDefinitionContext structDefinition() {
			return getRuleContext(StructDefinitionContext.class,0);
		}
		public ConstantDeclarationContext constantDeclaration() {
			return getRuleContext(ConstantDeclarationContext.class,0);
		}
		public VarDeclarationContext varDeclaration() {
			return getRuleContext(VarDeclarationContext.class,0);
		}
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_statement);
		try {
			setState(157);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(144);
				ifStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(145);
				switchStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(146);
				repeatStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(147);
				forStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(148);
				returnStatement();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(149);
				breakStatement();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(150);
				continueStatement();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(151);
				redirection();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(152);
				structDefinition();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(153);
				constantDeclaration();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(154);
				varDeclaration();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(155);
				functionDeclaration();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(156);
				expression(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RepeatStatementContext extends ParserRuleContext {
		public TerminalNode REPEAT() { return getToken(GioviParser.REPEAT, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode IF() { return getToken(GioviParser.IF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public RepeatStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_repeatStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitRepeatStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RepeatStatementContext repeatStatement() throws RecognitionException {
		RepeatStatementContext _localctx = new RepeatStatementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_repeatStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(REPEAT);
			setState(162);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IF) {
				{
				setState(160);
				match(IF);
				setState(161);
				expression(0);
				}
			}

			setState(164);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ForStatementContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(GioviParser.FOR, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode IN() { return getToken(GioviParser.IN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ForStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitForStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForStatementContext forStatement() throws RecognitionException {
		ForStatementContext _localctx = new ForStatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_forStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(FOR);
			setState(167);
			identifier();
			setState(168);
			match(IN);
			setState(169);
			expression(0);
			setState(170);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReturnStatementContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(GioviParser.RETURN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitReturnStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_returnStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT) | (1L << BRACKET_ROUND_OPEN) | (1L << BRACKET_SQUARE_OPEN) | (1L << BRACKET_CURLY_OPEN) | (1L << UNDERSCORE) | (1L << NULL) | (1L << INTEGER_LITERAL) | (1L << NATURAL_LITERAL) | (1L << REAL_LITERAL) | (1L << BOOL_LITERAL) | (1L << TEXT_BLOCK_LITERAL) | (1L << TEXT_LITERAL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (EXACT_TEXT_BLOCK_LITERAL - 64)) | (1L << (EXACT_TEXT_LITERAL - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
				{
				setState(172);
				expression(0);
				}
			}

			setState(175);
			match(RETURN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BreakStatementContext extends ParserRuleContext {
		public TerminalNode BREAK() { return getToken(GioviParser.BREAK, 0); }
		public BreakStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitBreakStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BreakStatementContext breakStatement() throws RecognitionException {
		BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_breakStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(177);
			match(BREAK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ContinueStatementContext extends ParserRuleContext {
		public TerminalNode CONTINUE() { return getToken(GioviParser.CONTINUE, 0); }
		public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitContinueStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContinueStatementContext continueStatement() throws RecognitionException {
		ContinueStatementContext _localctx = new ContinueStatementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_continueStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(179);
			match(CONTINUE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RedirectionContext extends ParserRuleContext {
		public RedirectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_redirection; }
	 
		public RedirectionContext() { }
		public void copyFrom(RedirectionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RedirectionToIdentifierContext extends RedirectionContext {
		public Token bop;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode BRACKET_ANGULAR_CLOSE() { return getToken(GioviParser.BRACKET_ANGULAR_CLOSE, 0); }
		public TerminalNode REDIRECTION_DIVISION() { return getToken(GioviParser.REDIRECTION_DIVISION, 0); }
		public TerminalNode REDIRECTION_MULTIPLICATION() { return getToken(GioviParser.REDIRECTION_MULTIPLICATION, 0); }
		public TerminalNode REDIRECTION_PLUS() { return getToken(GioviParser.REDIRECTION_PLUS, 0); }
		public TerminalNode REDIRECTION_MINUS() { return getToken(GioviParser.REDIRECTION_MINUS, 0); }
		public RedirectionToIdentifierContext(RedirectionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitRedirectionToIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RedirectionToPrimitiveDestinationContext extends RedirectionContext {
		public Token bop;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public PrimitiveDestinationContext primitiveDestination() {
			return getRuleContext(PrimitiveDestinationContext.class,0);
		}
		public TerminalNode BRACKET_ANGULAR_CLOSE() { return getToken(GioviParser.BRACKET_ANGULAR_CLOSE, 0); }
		public RedirectionToPrimitiveDestinationContext(RedirectionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitRedirectionToPrimitiveDestination(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RedirectionToVarDeclarationContext extends RedirectionContext {
		public Token bop;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public VarDeclarationContext varDeclaration() {
			return getRuleContext(VarDeclarationContext.class,0);
		}
		public TerminalNode BRACKET_ANGULAR_CLOSE() { return getToken(GioviParser.BRACKET_ANGULAR_CLOSE, 0); }
		public RedirectionToVarDeclarationContext(RedirectionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitRedirectionToVarDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RedirectionContext redirection() throws RecognitionException {
		RedirectionContext _localctx = new RedirectionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_redirection);
		int _la;
		try {
			setState(193);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				_localctx = new RedirectionToVarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(181);
				expression(0);
				setState(182);
				((RedirectionToVarDeclarationContext)_localctx).bop = match(BRACKET_ANGULAR_CLOSE);
				setState(183);
				varDeclaration();
				}
				break;
			case 2:
				_localctx = new RedirectionToPrimitiveDestinationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(185);
				expression(0);
				setState(186);
				((RedirectionToPrimitiveDestinationContext)_localctx).bop = match(BRACKET_ANGULAR_CLOSE);
				setState(187);
				primitiveDestination();
				}
				break;
			case 3:
				_localctx = new RedirectionToIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(189);
				expression(0);
				setState(190);
				((RedirectionToIdentifierContext)_localctx).bop = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << REDIRECTION_PLUS) | (1L << REDIRECTION_MINUS) | (1L << REDIRECTION_MULTIPLICATION) | (1L << REDIRECTION_DIVISION) | (1L << BRACKET_ANGULAR_CLOSE))) != 0)) ) {
					((RedirectionToIdentifierContext)_localctx).bop = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(191);
				identifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantDeclarationContext extends ParserRuleContext {
		public TerminalNode CONSTANT() { return getToken(GioviParser.CONSTANT, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ConstantDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantDeclaration; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitConstantDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantDeclarationContext constantDeclaration() throws RecognitionException {
		ConstantDeclarationContext _localctx = new ConstantDeclarationContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_constantDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			match(CONSTANT);
			setState(196);
			identifier();
			setState(197);
			type();
			setState(198);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDeclarationContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public VarDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDeclaration; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitVarDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDeclarationContext varDeclaration() throws RecognitionException {
		VarDeclarationContext _localctx = new VarDeclarationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_varDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			identifier();
			setState(201);
			type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefaultDefinitionContext extends ParserRuleContext {
		public DefaultLiteralContext defaultLiteral() {
			return getRuleContext(DefaultLiteralContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public DefaultDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultDefinition; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitDefaultDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefaultDefinitionContext defaultDefinition() throws RecognitionException {
		DefaultDefinitionContext _localctx = new DefaultDefinitionContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_defaultDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			defaultLiteral();
			setState(204);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StructDefinitionContext extends ParserRuleContext {
		public IdentifierContext parentName;
		public IdentifierContext structName;
		public TerminalNode BRACKET_ROUND_OPEN() { return getToken(GioviParser.BRACKET_ROUND_OPEN, 0); }
		public TerminalNode BRACKET_ROUND_CLOSE() { return getToken(GioviParser.BRACKET_ROUND_CLOSE, 0); }
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public TerminalNode COLON() { return getToken(GioviParser.COLON, 0); }
		public List<StructAttributeDefinitionContext> structAttributeDefinition() {
			return getRuleContexts(StructAttributeDefinitionContext.class);
		}
		public StructAttributeDefinitionContext structAttributeDefinition(int i) {
			return getRuleContext(StructAttributeDefinitionContext.class,i);
		}
		public List<NestedStructDefinitionContext> nestedStructDefinition() {
			return getRuleContexts(NestedStructDefinitionContext.class);
		}
		public NestedStructDefinitionContext nestedStructDefinition(int i) {
			return getRuleContext(NestedStructDefinitionContext.class,i);
		}
		public StructDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structDefinition; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitStructDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructDefinitionContext structDefinition() throws RecognitionException {
		StructDefinitionContext _localctx = new StructDefinitionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_structDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(206);
				((StructDefinitionContext)_localctx).parentName = identifier();
				setState(207);
				match(COLON);
				}
				break;
			}
			setState(211);
			((StructDefinitionContext)_localctx).structName = identifier();
			setState(212);
			match(BRACKET_ROUND_OPEN);
			setState(215); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(215);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					setState(213);
					structAttributeDefinition();
					}
					break;
				case 2:
					{
					setState(214);
					nestedStructDefinition();
					}
					break;
				}
				}
				setState(217); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(219);
			match(BRACKET_ROUND_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NestedStructDefinitionContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public StructDefinitionContext structDefinition() {
			return getRuleContext(StructDefinitionContext.class,0);
		}
		public NestedStructDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nestedStructDefinition; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitNestedStructDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NestedStructDefinitionContext nestedStructDefinition() throws RecognitionException {
		NestedStructDefinitionContext _localctx = new NestedStructDefinitionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_nestedStructDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			identifier();
			setState(222);
			structDefinition();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StructAttributeDefinitionContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public DefaultDefinitionContext defaultDefinition() {
			return getRuleContext(DefaultDefinitionContext.class,0);
		}
		public StructAttributeDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structAttributeDefinition; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitStructAttributeDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructAttributeDefinitionContext structAttributeDefinition() throws RecognitionException {
		StructAttributeDefinitionContext _localctx = new StructAttributeDefinitionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_structAttributeDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			identifier();
			setState(225);
			type();
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==UNDERSCORE) {
				{
				setState(226);
				defaultDefinition();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StructCreationContext extends ParserRuleContext {
		public TerminalNode BRACKET_ROUND_OPEN() { return getToken(GioviParser.BRACKET_ROUND_OPEN, 0); }
		public TerminalNode BRACKET_ROUND_CLOSE() { return getToken(GioviParser.BRACKET_ROUND_CLOSE, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode COLON() { return getToken(GioviParser.COLON, 0); }
		public List<StructAttributeContext> structAttribute() {
			return getRuleContexts(StructAttributeContext.class);
		}
		public StructAttributeContext structAttribute(int i) {
			return getRuleContext(StructAttributeContext.class,i);
		}
		public StructCreationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structCreation; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitStructCreation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructCreationContext structCreation() throws RecognitionException {
		StructCreationContext _localctx = new StructCreationContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_structCreation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(229);
				identifier();
				setState(230);
				match(COLON);
				}
			}

			setState(234);
			match(BRACKET_ROUND_OPEN);
			setState(236); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(235);
				structAttribute();
				}
				}
				setState(238); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT) | (1L << BRACKET_ROUND_OPEN) | (1L << BRACKET_SQUARE_OPEN) | (1L << BRACKET_CURLY_OPEN) | (1L << UNDERSCORE) | (1L << NULL) | (1L << INTEGER_LITERAL) | (1L << NATURAL_LITERAL) | (1L << REAL_LITERAL) | (1L << BOOL_LITERAL) | (1L << TEXT_BLOCK_LITERAL) | (1L << TEXT_LITERAL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (EXACT_TEXT_BLOCK_LITERAL - 64)) | (1L << (EXACT_TEXT_LITERAL - 64)) | (1L << (IDENTIFIER - 64)))) != 0) );
			setState(240);
			match(BRACKET_ROUND_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StructAttributeContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode COLON() { return getToken(GioviParser.COLON, 0); }
		public StructAttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structAttribute; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitStructAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructAttributeContext structAttribute() throws RecognitionException {
		StructAttributeContext _localctx = new StructAttributeContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_structAttribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(245);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(242);
				identifier();
				setState(243);
				match(COLON);
				}
				break;
			}
			setState(247);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionDeclarationContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode BRACKET_ROUND_OPEN() { return getToken(GioviParser.BRACKET_ROUND_OPEN, 0); }
		public TerminalNode BRACKET_ROUND_CLOSE() { return getToken(GioviParser.BRACKET_ROUND_CLOSE, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FormalParametersContext formalParameters() {
			return getRuleContext(FormalParametersContext.class,0);
		}
		public TerminalNode RETURN() { return getToken(GioviParser.RETURN, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDeclaration; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitFunctionDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionDeclarationContext functionDeclaration() throws RecognitionException {
		FunctionDeclarationContext _localctx = new FunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_functionDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			identifier();
			setState(250);
			match(BRACKET_ROUND_OPEN);
			setState(252);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(251);
				formalParameters();
				}
			}

			setState(254);
			match(BRACKET_ROUND_CLOSE);
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURN) {
				{
				setState(255);
				match(RETURN);
				setState(256);
				type();
				}
			}

			setState(259);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParametersContext extends ParserRuleContext {
		public List<FormalParameterDefinitionContext> formalParameterDefinition() {
			return getRuleContexts(FormalParameterDefinitionContext.class);
		}
		public FormalParameterDefinitionContext formalParameterDefinition(int i) {
			return getRuleContext(FormalParameterDefinitionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GioviParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GioviParser.COMMA, i);
		}
		public FormalParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameters; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitFormalParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormalParametersContext formalParameters() throws RecognitionException {
		FormalParametersContext _localctx = new FormalParametersContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_formalParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			formalParameterDefinition();
			setState(266);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(262);
				match(COMMA);
				setState(263);
				formalParameterDefinition();
				}
				}
				setState(268);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParameterDefinitionContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public DefaultDefinitionContext defaultDefinition() {
			return getRuleContext(DefaultDefinitionContext.class,0);
		}
		public FormalParameterDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameterDefinition; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitFormalParameterDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormalParameterDefinitionContext formalParameterDefinition() throws RecognitionException {
		FormalParameterDefinitionContext _localctx = new FormalParameterDefinitionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_formalParameterDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(269);
			identifier();
			setState(270);
			type();
			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==UNDERSCORE) {
				{
				setState(271);
				defaultDefinition();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionCallContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode BRACKET_ROUND_OPEN() { return getToken(GioviParser.BRACKET_ROUND_OPEN, 0); }
		public TerminalNode BRACKET_ROUND_CLOSE() { return getToken(GioviParser.BRACKET_ROUND_CLOSE, 0); }
		public FunctionParameterListContext functionParameterList() {
			return getRuleContext(FunctionParameterListContext.class,0);
		}
		public FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCall; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_functionCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			identifier();
			setState(275);
			match(BRACKET_ROUND_OPEN);
			setState(277);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT) | (1L << BRACKET_ROUND_OPEN) | (1L << BRACKET_SQUARE_OPEN) | (1L << BRACKET_CURLY_OPEN) | (1L << UNDERSCORE) | (1L << NULL) | (1L << INTEGER_LITERAL) | (1L << NATURAL_LITERAL) | (1L << REAL_LITERAL) | (1L << BOOL_LITERAL) | (1L << TEXT_BLOCK_LITERAL) | (1L << TEXT_LITERAL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (EXACT_TEXT_BLOCK_LITERAL - 64)) | (1L << (EXACT_TEXT_LITERAL - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
				{
				setState(276);
				functionParameterList();
				}
			}

			setState(279);
			match(BRACKET_ROUND_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionParameterListContext extends ParserRuleContext {
		public List<FunctionParameterContext> functionParameter() {
			return getRuleContexts(FunctionParameterContext.class);
		}
		public FunctionParameterContext functionParameter(int i) {
			return getRuleContext(FunctionParameterContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(GioviParser.COMMA, 0); }
		public FunctionParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionParameterList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitFunctionParameterList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionParameterListContext functionParameterList() throws RecognitionException {
		FunctionParameterListContext _localctx = new FunctionParameterListContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_functionParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(281);
			functionParameter();
			setState(284);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(282);
				match(COMMA);
				setState(283);
				functionParameter();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionParameterContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode COLON() { return getToken(GioviParser.COLON, 0); }
		public FunctionParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionParameter; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitFunctionParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionParameterContext functionParameter() throws RecognitionException {
		FunctionParameterContext _localctx = new FunctionParameterContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_functionParameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(289);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(286);
				identifier();
				setState(287);
				match(COLON);
				}
				break;
			}
			setState(291);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfStatementContext extends ParserRuleContext {
		public BlockContext ifBlock;
		public BlockContext elseBlock;
		public TerminalNode IF() { return getToken(GioviParser.IF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode DO() { return getToken(GioviParser.DO, 0); }
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(GioviParser.ELSE, 0); }
		public IfStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfStatementContext ifStatement() throws RecognitionException {
		IfStatementContext _localctx = new IfStatementContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_ifStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			match(IF);
			setState(294);
			expression(0);
			setState(295);
			match(DO);
			setState(296);
			((IfStatementContext)_localctx).ifBlock = block();
			setState(299);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(297);
				match(ELSE);
				setState(298);
				((IfStatementContext)_localctx).elseBlock = block();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SwitchStatementContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(GioviParser.IF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SwitchBlockContext switchBlock() {
			return getRuleContext(SwitchBlockContext.class,0);
		}
		public TerminalNode ELSE() { return getToken(GioviParser.ELSE, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public SwitchStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitSwitchStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SwitchStatementContext switchStatement() throws RecognitionException {
		SwitchStatementContext _localctx = new SwitchStatementContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_switchStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(301);
			match(IF);
			setState(302);
			expression(0);
			setState(303);
			switchBlock();
			setState(306);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(304);
				match(ELSE);
				setState(305);
				block();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SwitchBlockContext extends ParserRuleContext {
		public TerminalNode BRACKET_CURLY_OPEN() { return getToken(GioviParser.BRACKET_CURLY_OPEN, 0); }
		public TerminalNode BRACKET_CURLY_CLOSE() { return getToken(GioviParser.BRACKET_CURLY_CLOSE, 0); }
		public List<SwitchLabelContext> switchLabel() {
			return getRuleContexts(SwitchLabelContext.class);
		}
		public SwitchLabelContext switchLabel(int i) {
			return getRuleContext(SwitchLabelContext.class,i);
		}
		public SwitchBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchBlock; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitSwitchBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SwitchBlockContext switchBlock() throws RecognitionException {
		SwitchBlockContext _localctx = new SwitchBlockContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_switchBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			match(BRACKET_CURLY_OPEN);
			setState(310); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(309);
				switchLabel();
				}
				}
				setState(312); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQUAL) | (1L << NOT_EQUAL) | (1L << EQUAL_TILDE) | (1L << TILDE_EQUAL) | (1L << GREATER_EQUAL) | (1L << LESS_EQUAL) | (1L << AND) | (1L << OR) | (1L << BRACKET_CURLY_OPEN) | (1L << BRACKET_ANGULAR_OPEN) | (1L << BRACKET_ANGULAR_CLOSE))) != 0) );
			setState(314);
			match(BRACKET_CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SwitchLabelContext extends ParserRuleContext {
		public CaseMatchContext caseMatch() {
			return getRuleContext(CaseMatchContext.class,0);
		}
		public TerminalNode DO() { return getToken(GioviParser.DO, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public SwitchLabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchLabel; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitSwitchLabel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SwitchLabelContext switchLabel() throws RecognitionException {
		SwitchLabelContext _localctx = new SwitchLabelContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_switchLabel);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			caseMatch(0);
			setState(317);
			match(DO);
			setState(318);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CaseMatchContext extends ParserRuleContext {
		public Token op;
		public Token bop;
		public TerminalNode BRACKET_CURLY_OPEN() { return getToken(GioviParser.BRACKET_CURLY_OPEN, 0); }
		public List<CaseMatchContext> caseMatch() {
			return getRuleContexts(CaseMatchContext.class);
		}
		public CaseMatchContext caseMatch(int i) {
			return getRuleContext(CaseMatchContext.class,i);
		}
		public TerminalNode BRACKET_CURLY_CLOSE() { return getToken(GioviParser.BRACKET_CURLY_CLOSE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode LESS_EQUAL() { return getToken(GioviParser.LESS_EQUAL, 0); }
		public TerminalNode GREATER_EQUAL() { return getToken(GioviParser.GREATER_EQUAL, 0); }
		public TerminalNode BRACKET_ANGULAR_CLOSE() { return getToken(GioviParser.BRACKET_ANGULAR_CLOSE, 0); }
		public TerminalNode BRACKET_ANGULAR_OPEN() { return getToken(GioviParser.BRACKET_ANGULAR_OPEN, 0); }
		public TerminalNode AND() { return getToken(GioviParser.AND, 0); }
		public TerminalNode OR() { return getToken(GioviParser.OR, 0); }
		public TerminalNode EQUAL() { return getToken(GioviParser.EQUAL, 0); }
		public TerminalNode NOT_EQUAL() { return getToken(GioviParser.NOT_EQUAL, 0); }
		public TerminalNode TILDE_EQUAL() { return getToken(GioviParser.TILDE_EQUAL, 0); }
		public TerminalNode EQUAL_TILDE() { return getToken(GioviParser.EQUAL_TILDE, 0); }
		public CaseMatchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseMatch; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitCaseMatch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CaseMatchContext caseMatch() throws RecognitionException {
		return caseMatch(0);
	}

	private CaseMatchContext caseMatch(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		CaseMatchContext _localctx = new CaseMatchContext(_ctx, _parentState);
		CaseMatchContext _prevctx = _localctx;
		int _startState = 56;
		enterRecursionRule(_localctx, 56, RULE_caseMatch, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BRACKET_CURLY_OPEN:
				{
				setState(321);
				match(BRACKET_CURLY_OPEN);
				setState(322);
				caseMatch(0);
				setState(323);
				match(BRACKET_CURLY_CLOSE);
				}
				break;
			case GREATER_EQUAL:
			case LESS_EQUAL:
			case BRACKET_ANGULAR_OPEN:
			case BRACKET_ANGULAR_CLOSE:
				{
				setState(325);
				((CaseMatchContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GREATER_EQUAL) | (1L << LESS_EQUAL) | (1L << BRACKET_ANGULAR_OPEN) | (1L << BRACKET_ANGULAR_CLOSE))) != 0)) ) {
					((CaseMatchContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(326);
				expression(0);
				}
				break;
			case AND:
				{
				setState(327);
				((CaseMatchContext)_localctx).op = match(AND);
				setState(328);
				expression(0);
				}
				break;
			case OR:
				{
				setState(329);
				((CaseMatchContext)_localctx).op = match(OR);
				setState(330);
				expression(0);
				}
				break;
			case EQUAL:
			case NOT_EQUAL:
				{
				setState(331);
				((CaseMatchContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==EQUAL || _la==NOT_EQUAL) ) {
					((CaseMatchContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(332);
				expression(0);
				}
				break;
			case EQUAL_TILDE:
			case TILDE_EQUAL:
				{
				setState(333);
				((CaseMatchContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==EQUAL_TILDE || _la==TILDE_EQUAL) ) {
					((CaseMatchContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(334);
				expression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(345);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new CaseMatchContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_caseMatch);
					setState(337);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(338);
					((CaseMatchContext)_localctx).bop = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==AND || _la==OR) ) {
						((CaseMatchContext)_localctx).bop = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(341);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
					case 1:
						{
						setState(339);
						caseMatch(0);
						}
						break;
					case 2:
						{
						setState(340);
						expression(0);
						}
						break;
					}
					}
					} 
				}
				setState(347);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PrimaryExpressionContext extends ExpressionContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public PrimaryExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitPrimaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DotExpressionContext extends ExpressionContext {
		public Token bop;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode DOT() { return getToken(GioviParser.DOT, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public DotExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitDotExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BoolExpressionContext extends ExpressionContext {
		public Token bop;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode AND() { return getToken(GioviParser.AND, 0); }
		public TerminalNode OR() { return getToken(GioviParser.OR, 0); }
		public BoolExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitBoolExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MapCreationExpressionContext extends ExpressionContext {
		public MapCreationContext mapCreation() {
			return getRuleContext(MapCreationContext.class,0);
		}
		public MapCreationExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitMapCreationExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PrefixedExpressionContext extends ExpressionContext {
		public Token prefix;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(GioviParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(GioviParser.MINUS, 0); }
		public PrefixedExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitPrefixedExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AdditiveExpressionContext extends ExpressionContext {
		public Token bop;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode PLUS() { return getToken(GioviParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(GioviParser.MINUS, 0); }
		public AdditiveExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitAdditiveExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelationalExpressionContext extends ExpressionContext {
		public Token bop;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode LESS_EQUAL() { return getToken(GioviParser.LESS_EQUAL, 0); }
		public TerminalNode GREATER_EQUAL() { return getToken(GioviParser.GREATER_EQUAL, 0); }
		public TerminalNode BRACKET_ANGULAR_CLOSE() { return getToken(GioviParser.BRACKET_ANGULAR_CLOSE, 0); }
		public TerminalNode BRACKET_ANGULAR_OPEN() { return getToken(GioviParser.BRACKET_ANGULAR_OPEN, 0); }
		public RelationalExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitRelationalExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StructCreationExpressionContext extends ExpressionContext {
		public StructCreationContext structCreation() {
			return getRuleContext(StructCreationContext.class,0);
		}
		public StructCreationExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitStructCreationExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotExpressionContext extends ExpressionContext {
		public Token prefix;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode NOT() { return getToken(GioviParser.NOT, 0); }
		public NotExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitNotExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ContainmentExpressionContext extends ExpressionContext {
		public Token bop;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode TILDE_EQUAL() { return getToken(GioviParser.TILDE_EQUAL, 0); }
		public TerminalNode EQUAL_TILDE() { return getToken(GioviParser.EQUAL_TILDE, 0); }
		public ContainmentExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitContainmentExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MultiplicativeExpressionContext extends ExpressionContext {
		public Token bop;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode MULTIPLICATION() { return getToken(GioviParser.MULTIPLICATION, 0); }
		public TerminalNode DIVISION() { return getToken(GioviParser.DIVISION, 0); }
		public TerminalNode MODULO() { return getToken(GioviParser.MODULO, 0); }
		public MultiplicativeExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitMultiplicativeExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PowerExpressionContext extends ExpressionContext {
		public Token bop;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode EXPONENTIAL() { return getToken(GioviParser.EXPONENTIAL, 0); }
		public PowerExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitPowerExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ListCreationExpressionContext extends ExpressionContext {
		public ListCreationContext listCreation() {
			return getRuleContext(ListCreationContext.class,0);
		}
		public ListCreationExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitListCreationExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenthesizedExpressionContext extends ExpressionContext {
		public TerminalNode BRACKET_CURLY_OPEN() { return getToken(GioviParser.BRACKET_CURLY_OPEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode BRACKET_CURLY_CLOSE() { return getToken(GioviParser.BRACKET_CURLY_CLOSE, 0); }
		public ParenthesizedExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitParenthesizedExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EqualityExpressionContext extends ExpressionContext {
		public Token bop;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode EQUAL() { return getToken(GioviParser.EQUAL, 0); }
		public TerminalNode NOT_EQUAL() { return getToken(GioviParser.NOT_EQUAL, 0); }
		public EqualityExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitEqualityExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionCallExpressionContext extends ExpressionContext {
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public FunctionCallExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitFunctionCallExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CollectionAccessExpressionContext extends ExpressionContext {
		public CollectionAccessContext collectionAccess() {
			return getRuleContext(CollectionAccessContext.class,0);
		}
		public CollectionAccessExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitCollectionAccessExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 58;
		enterRecursionRule(_localctx, 58, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(363);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				_localctx = new PrimaryExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(349);
				primary();
				}
				break;
			case 2:
				{
				_localctx = new ParenthesizedExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(350);
				match(BRACKET_CURLY_OPEN);
				setState(351);
				expression(0);
				setState(352);
				match(BRACKET_CURLY_CLOSE);
				}
				break;
			case 3:
				{
				_localctx = new CollectionAccessExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(354);
				collectionAccess();
				}
				break;
			case 4:
				{
				_localctx = new ListCreationExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(355);
				listCreation();
				}
				break;
			case 5:
				{
				_localctx = new MapCreationExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(356);
				mapCreation();
				}
				break;
			case 6:
				{
				_localctx = new StructCreationExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(357);
				structCreation();
				}
				break;
			case 7:
				{
				_localctx = new FunctionCallExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(358);
				functionCall();
				}
				break;
			case 8:
				{
				_localctx = new PrefixedExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(359);
				((PrefixedExpressionContext)_localctx).prefix = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
					((PrefixedExpressionContext)_localctx).prefix = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(360);
				expression(9);
				}
				break;
			case 9:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(361);
				((NotExpressionContext)_localctx).prefix = match(NOT);
				setState(362);
				expression(8);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(394);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(392);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
					case 1:
						{
						_localctx = new PowerExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(365);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(366);
						((PowerExpressionContext)_localctx).bop = match(EXPONENTIAL);
						setState(367);
						expression(8);
						}
						break;
					case 2:
						{
						_localctx = new MultiplicativeExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(368);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(369);
						((MultiplicativeExpressionContext)_localctx).bop = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULTIPLICATION) | (1L << DIVISION) | (1L << MODULO))) != 0)) ) {
							((MultiplicativeExpressionContext)_localctx).bop = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(370);
						expression(7);
						}
						break;
					case 3:
						{
						_localctx = new AdditiveExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(371);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(372);
						((AdditiveExpressionContext)_localctx).bop = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((AdditiveExpressionContext)_localctx).bop = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(373);
						expression(6);
						}
						break;
					case 4:
						{
						_localctx = new RelationalExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(374);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(375);
						((RelationalExpressionContext)_localctx).bop = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GREATER_EQUAL) | (1L << LESS_EQUAL) | (1L << BRACKET_ANGULAR_OPEN) | (1L << BRACKET_ANGULAR_CLOSE))) != 0)) ) {
							((RelationalExpressionContext)_localctx).bop = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(376);
						expression(5);
						}
						break;
					case 5:
						{
						_localctx = new EqualityExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(377);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(378);
						((EqualityExpressionContext)_localctx).bop = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==EQUAL || _la==NOT_EQUAL) ) {
							((EqualityExpressionContext)_localctx).bop = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(379);
						expression(4);
						}
						break;
					case 6:
						{
						_localctx = new ContainmentExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(380);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(381);
						((ContainmentExpressionContext)_localctx).bop = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==EQUAL_TILDE || _la==TILDE_EQUAL) ) {
							((ContainmentExpressionContext)_localctx).bop = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(382);
						expression(3);
						}
						break;
					case 7:
						{
						_localctx = new BoolExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(383);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(384);
						((BoolExpressionContext)_localctx).bop = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==AND || _la==OR) ) {
							((BoolExpressionContext)_localctx).bop = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(385);
						expression(2);
						}
						break;
					case 8:
						{
						_localctx = new DotExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(386);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(387);
						((DotExpressionContext)_localctx).bop = match(DOT);
						setState(390);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
						case 1:
							{
							setState(388);
							identifier();
							}
							break;
						case 2:
							{
							setState(389);
							functionCall();
							}
							break;
						}
						}
						break;
					}
					} 
				}
				setState(396);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PrimaryContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_primary);
		try {
			setState(399);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(397);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(398);
				identifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SequenceContext extends ParserRuleContext {
		public ExpressionContext start;
		public ExpressionContext end;
		public ExpressionContext step;
		public List<TerminalNode> SEQUENCE() { return getTokens(GioviParser.SEQUENCE); }
		public TerminalNode SEQUENCE(int i) {
			return getToken(GioviParser.SEQUENCE, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public SequenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sequence; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitSequence(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SequenceContext sequence() throws RecognitionException {
		SequenceContext _localctx = new SequenceContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_sequence);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(401);
			((SequenceContext)_localctx).start = expression(0);
			setState(402);
			match(SEQUENCE);
			setState(403);
			((SequenceContext)_localctx).end = expression(0);
			setState(406);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEQUENCE) {
				{
				setState(404);
				match(SEQUENCE);
				setState(405);
				((SequenceContext)_localctx).step = expression(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimitiveDestinationContext extends ParserRuleContext {
		public StdoutContext stdout() {
			return getRuleContext(StdoutContext.class,0);
		}
		public StderrContext stderr() {
			return getRuleContext(StderrContext.class,0);
		}
		public PrimitiveDestinationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveDestination; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitPrimitiveDestination(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimitiveDestinationContext primitiveDestination() throws RecognitionException {
		PrimitiveDestinationContext _localctx = new PrimitiveDestinationContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_primitiveDestination);
		try {
			setState(410);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BACKSLASH:
			case DEST_STDOUT:
				enterOuterAlt(_localctx, 1);
				{
				setState(408);
				stdout();
				}
				break;
			case DEST_STDERR:
				enterOuterAlt(_localctx, 2);
				{
				setState(409);
				stderr();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StdoutContext extends ParserRuleContext {
		public TerminalNode DEST_STDOUT() { return getToken(GioviParser.DEST_STDOUT, 0); }
		public TerminalNode BACKSLASH() { return getToken(GioviParser.BACKSLASH, 0); }
		public StdoutContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stdout; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitStdout(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StdoutContext stdout() throws RecognitionException {
		StdoutContext _localctx = new StdoutContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_stdout);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(412);
			_la = _input.LA(1);
			if ( !(_la==BACKSLASH || _la==DEST_STDOUT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StderrContext extends ParserRuleContext {
		public TerminalNode DEST_STDERR() { return getToken(GioviParser.DEST_STDERR, 0); }
		public StderrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stderr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitStderr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StderrContext stderr() throws RecognitionException {
		StderrContext _localctx = new StderrContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_stderr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(414);
			match(DEST_STDERR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public StructTypeContext structType() {
			return getRuleContext(StructTypeContext.class,0);
		}
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TYPE_LIST:
			case TYPE_MAP:
			case IDENTIFIER:
				{
				setState(416);
				structType();
				}
				break;
			case TYPE_NATURAL:
			case TYPE_INT:
			case TYPE_REAL:
			case TYPE_BOOL:
			case TYPE_TEXT:
				{
				setState(417);
				primitiveType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StructTypeContext extends ParserRuleContext {
		public PrimitiveStructTypeContext primitiveStructType() {
			return getRuleContext(PrimitiveStructTypeContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public StructTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structType; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitStructType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructTypeContext structType() throws RecognitionException {
		StructTypeContext _localctx = new StructTypeContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_structType);
		try {
			setState(422);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TYPE_LIST:
			case TYPE_MAP:
				enterOuterAlt(_localctx, 1);
				{
				setState(420);
				primitiveStructType();
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(421);
				identifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimitiveStructTypeContext extends ParserRuleContext {
		public ListContext list() {
			return getRuleContext(ListContext.class,0);
		}
		public MapContext map() {
			return getRuleContext(MapContext.class,0);
		}
		public PrimitiveStructTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveStructType; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitPrimitiveStructType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimitiveStructTypeContext primitiveStructType() throws RecognitionException {
		PrimitiveStructTypeContext _localctx = new PrimitiveStructTypeContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_primitiveStructType);
		try {
			setState(426);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TYPE_LIST:
				enterOuterAlt(_localctx, 1);
				{
				setState(424);
				list();
				}
				break;
			case TYPE_MAP:
				enterOuterAlt(_localctx, 2);
				{
				setState(425);
				map();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CollectionAccessContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode BRACKET_SQUARE_OPEN() { return getToken(GioviParser.BRACKET_SQUARE_OPEN, 0); }
		public CollectionAccessorContext collectionAccessor() {
			return getRuleContext(CollectionAccessorContext.class,0);
		}
		public TerminalNode BRACKET_SQUARE_CLOSE() { return getToken(GioviParser.BRACKET_SQUARE_CLOSE, 0); }
		public CollectionAccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_collectionAccess; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitCollectionAccess(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CollectionAccessContext collectionAccess() throws RecognitionException {
		CollectionAccessContext _localctx = new CollectionAccessContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_collectionAccess);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(428);
			identifier();
			setState(429);
			match(BRACKET_SQUARE_OPEN);
			setState(430);
			collectionAccessor();
			setState(431);
			match(BRACKET_SQUARE_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CollectionAccessorContext extends ParserRuleContext {
		public ListAccessorContext listAccessor() {
			return getRuleContext(ListAccessorContext.class,0);
		}
		public MapAccessorContext mapAccessor() {
			return getRuleContext(MapAccessorContext.class,0);
		}
		public CollectionAccessorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_collectionAccessor; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitCollectionAccessor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CollectionAccessorContext collectionAccessor() throws RecognitionException {
		CollectionAccessorContext _localctx = new CollectionAccessorContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_collectionAccessor);
		try {
			setState(435);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(433);
				listAccessor();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(434);
				mapAccessor();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListContext extends ParserRuleContext {
		public TerminalNode TYPE_LIST() { return getToken(GioviParser.TYPE_LIST, 0); }
		public TerminalNode BRACKET_SQUARE_OPEN() { return getToken(GioviParser.BRACKET_SQUARE_OPEN, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode BRACKET_SQUARE_CLOSE() { return getToken(GioviParser.BRACKET_SQUARE_CLOSE, 0); }
		public ListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_list; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListContext list() throws RecognitionException {
		ListContext _localctx = new ListContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(437);
			match(TYPE_LIST);
			setState(438);
			match(BRACKET_SQUARE_OPEN);
			setState(439);
			type();
			setState(440);
			match(BRACKET_SQUARE_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListCreationContext extends ParserRuleContext {
		public TerminalNode BRACKET_SQUARE_OPEN() { return getToken(GioviParser.BRACKET_SQUARE_OPEN, 0); }
		public ListEntriesContext listEntries() {
			return getRuleContext(ListEntriesContext.class,0);
		}
		public TerminalNode BRACKET_SQUARE_CLOSE() { return getToken(GioviParser.BRACKET_SQUARE_CLOSE, 0); }
		public ListCreationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listCreation; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitListCreation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListCreationContext listCreation() throws RecognitionException {
		ListCreationContext _localctx = new ListCreationContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_listCreation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(442);
			match(BRACKET_SQUARE_OPEN);
			setState(443);
			listEntries();
			setState(444);
			match(BRACKET_SQUARE_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListEntriesContext extends ParserRuleContext {
		public List<ListEntryContext> listEntry() {
			return getRuleContexts(ListEntryContext.class);
		}
		public ListEntryContext listEntry(int i) {
			return getRuleContext(ListEntryContext.class,i);
		}
		public ListEntriesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listEntries; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitListEntries(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListEntriesContext listEntries() throws RecognitionException {
		ListEntriesContext _localctx = new ListEntriesContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_listEntries);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(449);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT) | (1L << BRACKET_ROUND_OPEN) | (1L << BRACKET_SQUARE_OPEN) | (1L << BRACKET_CURLY_OPEN) | (1L << UNDERSCORE) | (1L << NULL) | (1L << INTEGER_LITERAL) | (1L << NATURAL_LITERAL) | (1L << REAL_LITERAL) | (1L << BOOL_LITERAL) | (1L << TEXT_BLOCK_LITERAL) | (1L << TEXT_LITERAL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (EXACT_TEXT_BLOCK_LITERAL - 64)) | (1L << (EXACT_TEXT_LITERAL - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
				{
				{
				setState(446);
				listEntry();
				}
				}
				setState(451);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListEntryContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SequenceContext sequence() {
			return getRuleContext(SequenceContext.class,0);
		}
		public ListEntryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listEntry; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitListEntry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListEntryContext listEntry() throws RecognitionException {
		ListEntryContext _localctx = new ListEntryContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_listEntry);
		try {
			setState(454);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(452);
				expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(453);
				sequence();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListAccessorContext extends ParserRuleContext {
		public TerminalNode MODULO() { return getToken(GioviParser.MODULO, 0); }
		public TerminalNode AT() { return getToken(GioviParser.AT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ListAccessorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listAccessor; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitListAccessor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListAccessorContext listAccessor() throws RecognitionException {
		ListAccessorContext _localctx = new ListAccessorContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_listAccessor);
		try {
			setState(459);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MODULO:
				enterOuterAlt(_localctx, 1);
				{
				setState(456);
				match(MODULO);
				}
				break;
			case AT:
				enterOuterAlt(_localctx, 2);
				{
				setState(457);
				match(AT);
				}
				break;
			case PLUS:
			case MINUS:
			case NOT:
			case BRACKET_ROUND_OPEN:
			case BRACKET_SQUARE_OPEN:
			case BRACKET_CURLY_OPEN:
			case UNDERSCORE:
			case NULL:
			case INTEGER_LITERAL:
			case NATURAL_LITERAL:
			case REAL_LITERAL:
			case BOOL_LITERAL:
			case TEXT_BLOCK_LITERAL:
			case TEXT_LITERAL:
			case EXACT_TEXT_BLOCK_LITERAL:
			case EXACT_TEXT_LITERAL:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 3);
				{
				setState(458);
				expression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MapContext extends ParserRuleContext {
		public TypeContext mapKeyType;
		public TypeContext mapValueType;
		public TerminalNode TYPE_MAP() { return getToken(GioviParser.TYPE_MAP, 0); }
		public TerminalNode BRACKET_SQUARE_OPEN() { return getToken(GioviParser.BRACKET_SQUARE_OPEN, 0); }
		public TerminalNode COLON() { return getToken(GioviParser.COLON, 0); }
		public TerminalNode BRACKET_SQUARE_CLOSE() { return getToken(GioviParser.BRACKET_SQUARE_CLOSE, 0); }
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public MapContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_map; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitMap(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapContext map() throws RecognitionException {
		MapContext _localctx = new MapContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_map);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(461);
			match(TYPE_MAP);
			setState(462);
			match(BRACKET_SQUARE_OPEN);
			setState(463);
			((MapContext)_localctx).mapKeyType = type();
			setState(464);
			match(COLON);
			setState(465);
			((MapContext)_localctx).mapValueType = type();
			setState(466);
			match(BRACKET_SQUARE_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MapCreationContext extends ParserRuleContext {
		public TerminalNode BRACKET_SQUARE_OPEN() { return getToken(GioviParser.BRACKET_SQUARE_OPEN, 0); }
		public MapEntriesContext mapEntries() {
			return getRuleContext(MapEntriesContext.class,0);
		}
		public TerminalNode BRACKET_SQUARE_CLOSE() { return getToken(GioviParser.BRACKET_SQUARE_CLOSE, 0); }
		public MapCreationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapCreation; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitMapCreation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapCreationContext mapCreation() throws RecognitionException {
		MapCreationContext _localctx = new MapCreationContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_mapCreation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(468);
			match(BRACKET_SQUARE_OPEN);
			setState(469);
			mapEntries();
			setState(470);
			match(BRACKET_SQUARE_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MapEntriesContext extends ParserRuleContext {
		public List<MapEntryContext> mapEntry() {
			return getRuleContexts(MapEntryContext.class);
		}
		public MapEntryContext mapEntry(int i) {
			return getRuleContext(MapEntryContext.class,i);
		}
		public MapEntriesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapEntries; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitMapEntries(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapEntriesContext mapEntries() throws RecognitionException {
		MapEntriesContext _localctx = new MapEntriesContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_mapEntries);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(475);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT) | (1L << BRACKET_ROUND_OPEN) | (1L << BRACKET_SQUARE_OPEN) | (1L << BRACKET_CURLY_OPEN) | (1L << UNDERSCORE) | (1L << NULL) | (1L << INTEGER_LITERAL) | (1L << NATURAL_LITERAL) | (1L << REAL_LITERAL) | (1L << BOOL_LITERAL) | (1L << TEXT_BLOCK_LITERAL) | (1L << TEXT_LITERAL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (EXACT_TEXT_BLOCK_LITERAL - 64)) | (1L << (EXACT_TEXT_LITERAL - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
				{
				{
				setState(472);
				mapEntry();
				}
				}
				setState(477);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MapEntryContext extends ParserRuleContext {
		public ExpressionContext mapKey;
		public ExpressionContext mapValue;
		public TerminalNode COLON() { return getToken(GioviParser.COLON, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public MapEntryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapEntry; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitMapEntry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapEntryContext mapEntry() throws RecognitionException {
		MapEntryContext _localctx = new MapEntryContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_mapEntry);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(478);
			((MapEntryContext)_localctx).mapKey = expression(0);
			setState(479);
			match(COLON);
			setState(480);
			((MapEntryContext)_localctx).mapValue = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MapAccessorContext extends ParserRuleContext {
		public TerminalNode MODULO() { return getToken(GioviParser.MODULO, 0); }
		public TerminalNode AT() { return getToken(GioviParser.AT, 0); }
		public TerminalNode MULTIPLICATION() { return getToken(GioviParser.MULTIPLICATION, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public MapAccessorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapAccessor; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitMapAccessor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapAccessorContext mapAccessor() throws RecognitionException {
		MapAccessorContext _localctx = new MapAccessorContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_mapAccessor);
		try {
			setState(486);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MODULO:
				enterOuterAlt(_localctx, 1);
				{
				setState(482);
				match(MODULO);
				}
				break;
			case AT:
				enterOuterAlt(_localctx, 2);
				{
				setState(483);
				match(AT);
				}
				break;
			case MULTIPLICATION:
				enterOuterAlt(_localctx, 3);
				{
				setState(484);
				match(MULTIPLICATION);
				}
				break;
			case PLUS:
			case MINUS:
			case NOT:
			case BRACKET_ROUND_OPEN:
			case BRACKET_SQUARE_OPEN:
			case BRACKET_CURLY_OPEN:
			case UNDERSCORE:
			case NULL:
			case INTEGER_LITERAL:
			case NATURAL_LITERAL:
			case REAL_LITERAL:
			case BOOL_LITERAL:
			case TEXT_BLOCK_LITERAL:
			case TEXT_LITERAL:
			case EXACT_TEXT_BLOCK_LITERAL:
			case EXACT_TEXT_LITERAL:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 4);
				{
				setState(485);
				expression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimitiveTypeContext extends ParserRuleContext {
		public TerminalNode TYPE_INT() { return getToken(GioviParser.TYPE_INT, 0); }
		public TerminalNode TYPE_NATURAL() { return getToken(GioviParser.TYPE_NATURAL, 0); }
		public TerminalNode TYPE_BOOL() { return getToken(GioviParser.TYPE_BOOL, 0); }
		public TerminalNode TYPE_REAL() { return getToken(GioviParser.TYPE_REAL, 0); }
		public TerminalNode TYPE_TEXT() { return getToken(GioviParser.TYPE_TEXT, 0); }
		public PrimitiveTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveType; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitPrimitiveType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimitiveTypeContext primitiveType() throws RecognitionException {
		PrimitiveTypeContext _localctx = new PrimitiveTypeContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_primitiveType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(488);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TYPE_NATURAL) | (1L << TYPE_INT) | (1L << TYPE_REAL) | (1L << TYPE_BOOL) | (1L << TYPE_TEXT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public IntegerLiteralContext integerLiteral() {
			return getRuleContext(IntegerLiteralContext.class,0);
		}
		public NaturalLiteralContext naturalLiteral() {
			return getRuleContext(NaturalLiteralContext.class,0);
		}
		public RealLiteralContext realLiteral() {
			return getRuleContext(RealLiteralContext.class,0);
		}
		public BoolLiteralContext boolLiteral() {
			return getRuleContext(BoolLiteralContext.class,0);
		}
		public TextBlockLiteralContext textBlockLiteral() {
			return getRuleContext(TextBlockLiteralContext.class,0);
		}
		public TextLiteralContext textLiteral() {
			return getRuleContext(TextLiteralContext.class,0);
		}
		public ExactTextBlockLiteralContext exactTextBlockLiteral() {
			return getRuleContext(ExactTextBlockLiteralContext.class,0);
		}
		public ExactTextLiteralContext exactTextLiteral() {
			return getRuleContext(ExactTextLiteralContext.class,0);
		}
		public NullLiteralContext nullLiteral() {
			return getRuleContext(NullLiteralContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public DefaultLiteralContext defaultLiteral() {
			return getRuleContext(DefaultLiteralContext.class,0);
		}
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_literal);
		try {
			setState(501);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INTEGER_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(490);
				integerLiteral();
				}
				break;
			case NATURAL_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(491);
				naturalLiteral();
				}
				break;
			case REAL_LITERAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(492);
				realLiteral();
				}
				break;
			case BOOL_LITERAL:
				enterOuterAlt(_localctx, 4);
				{
				setState(493);
				boolLiteral();
				}
				break;
			case TEXT_BLOCK_LITERAL:
				enterOuterAlt(_localctx, 5);
				{
				setState(494);
				textBlockLiteral();
				}
				break;
			case TEXT_LITERAL:
				enterOuterAlt(_localctx, 6);
				{
				setState(495);
				textLiteral();
				}
				break;
			case EXACT_TEXT_BLOCK_LITERAL:
				enterOuterAlt(_localctx, 7);
				{
				setState(496);
				exactTextBlockLiteral();
				}
				break;
			case EXACT_TEXT_LITERAL:
				enterOuterAlt(_localctx, 8);
				{
				setState(497);
				exactTextLiteral();
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 9);
				{
				setState(498);
				nullLiteral();
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 10);
				{
				setState(499);
				identifier();
				}
				break;
			case UNDERSCORE:
				enterOuterAlt(_localctx, 11);
				{
				setState(500);
				defaultLiteral();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(GioviParser.IDENTIFIER, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(503);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntegerLiteralContext extends ParserRuleContext {
		public TerminalNode INTEGER_LITERAL() { return getToken(GioviParser.INTEGER_LITERAL, 0); }
		public IntegerLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integerLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitIntegerLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntegerLiteralContext integerLiteral() throws RecognitionException {
		IntegerLiteralContext _localctx = new IntegerLiteralContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_integerLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(505);
			match(INTEGER_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NaturalLiteralContext extends ParserRuleContext {
		public TerminalNode NATURAL_LITERAL() { return getToken(GioviParser.NATURAL_LITERAL, 0); }
		public NaturalLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_naturalLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitNaturalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NaturalLiteralContext naturalLiteral() throws RecognitionException {
		NaturalLiteralContext _localctx = new NaturalLiteralContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_naturalLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(507);
			match(NATURAL_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RealLiteralContext extends ParserRuleContext {
		public TerminalNode REAL_LITERAL() { return getToken(GioviParser.REAL_LITERAL, 0); }
		public RealLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_realLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitRealLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RealLiteralContext realLiteral() throws RecognitionException {
		RealLiteralContext _localctx = new RealLiteralContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_realLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(509);
			match(REAL_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoolLiteralContext extends ParserRuleContext {
		public TerminalNode BOOL_LITERAL() { return getToken(GioviParser.BOOL_LITERAL, 0); }
		public BoolLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitBoolLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolLiteralContext boolLiteral() throws RecognitionException {
		BoolLiteralContext _localctx = new BoolLiteralContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_boolLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(511);
			match(BOOL_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TextBlockLiteralContext extends ParserRuleContext {
		public TerminalNode TEXT_BLOCK_LITERAL() { return getToken(GioviParser.TEXT_BLOCK_LITERAL, 0); }
		public TextBlockLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_textBlockLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitTextBlockLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TextBlockLiteralContext textBlockLiteral() throws RecognitionException {
		TextBlockLiteralContext _localctx = new TextBlockLiteralContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_textBlockLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(513);
			match(TEXT_BLOCK_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TextLiteralContext extends ParserRuleContext {
		public TerminalNode TEXT_LITERAL() { return getToken(GioviParser.TEXT_LITERAL, 0); }
		public TextLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_textLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitTextLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TextLiteralContext textLiteral() throws RecognitionException {
		TextLiteralContext _localctx = new TextLiteralContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_textLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(515);
			match(TEXT_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExactTextBlockLiteralContext extends ParserRuleContext {
		public TerminalNode EXACT_TEXT_BLOCK_LITERAL() { return getToken(GioviParser.EXACT_TEXT_BLOCK_LITERAL, 0); }
		public ExactTextBlockLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exactTextBlockLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitExactTextBlockLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExactTextBlockLiteralContext exactTextBlockLiteral() throws RecognitionException {
		ExactTextBlockLiteralContext _localctx = new ExactTextBlockLiteralContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_exactTextBlockLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(517);
			match(EXACT_TEXT_BLOCK_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExactTextLiteralContext extends ParserRuleContext {
		public TerminalNode EXACT_TEXT_LITERAL() { return getToken(GioviParser.EXACT_TEXT_LITERAL, 0); }
		public ExactTextLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exactTextLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitExactTextLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExactTextLiteralContext exactTextLiteral() throws RecognitionException {
		ExactTextLiteralContext _localctx = new ExactTextLiteralContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_exactTextLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(519);
			match(EXACT_TEXT_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NullLiteralContext extends ParserRuleContext {
		public TerminalNode NULL() { return getToken(GioviParser.NULL, 0); }
		public NullLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nullLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitNullLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NullLiteralContext nullLiteral() throws RecognitionException {
		NullLiteralContext _localctx = new NullLiteralContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_nullLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(521);
			match(NULL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefaultLiteralContext extends ParserRuleContext {
		public TerminalNode UNDERSCORE() { return getToken(GioviParser.UNDERSCORE, 0); }
		public DefaultLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GioviParserVisitor ) return ((GioviParserVisitor<? extends T>)visitor).visitDefaultLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefaultLiteralContext defaultLiteral() throws RecognitionException {
		DefaultLiteralContext _localctx = new DefaultLiteralContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_defaultLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(523);
			match(UNDERSCORE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 28:
			return caseMatch_sempred((CaseMatchContext)_localctx, predIndex);
		case 29:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean caseMatch_sempred(CaseMatchContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 7);
		case 2:
			return precpred(_ctx, 6);
		case 3:
			return precpred(_ctx, 5);
		case 4:
			return precpred(_ctx, 4);
		case 5:
			return precpred(_ctx, 3);
		case 6:
			return precpred(_ctx, 2);
		case 7:
			return precpred(_ctx, 1);
		case 8:
			return precpred(_ctx, 15);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001D\u020e\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002"+
		"<\u0007<\u0002=\u0007=\u0002>\u0007>\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0005\u0001\u0083\b\u0001\n\u0001\f\u0001\u0086\t\u0001\u0001"+
		"\u0002\u0001\u0002\u0005\u0002\u008a\b\u0002\n\u0002\f\u0002\u008d\t\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u009e\b\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0003\u0004\u00a3\b\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0003\u0006\u00ae\b\u0006\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u00c2\b\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0003\r\u00d2\b\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0004\r\u00d8\b\r\u000b\r\f\r\u00d9\u0001\r"+
		"\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0003\u000f\u00e4\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0003"+
		"\u0010\u00e9\b\u0010\u0001\u0010\u0001\u0010\u0004\u0010\u00ed\b\u0010"+
		"\u000b\u0010\f\u0010\u00ee\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0003\u0011\u00f6\b\u0011\u0001\u0011\u0001\u0011\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0003\u0012\u00fd\b\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0003\u0012\u0102\b\u0012\u0001\u0012\u0001\u0012\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0005\u0013\u0109\b\u0013\n\u0013\f\u0013\u010c"+
		"\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u0111\b\u0014"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u0116\b\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u011d\b\u0016"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u0122\b\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0003\u0018\u012c\b\u0018\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0003\u0019\u0133\b\u0019\u0001\u001a\u0001\u001a"+
		"\u0004\u001a\u0137\b\u001a\u000b\u001a\f\u001a\u0138\u0001\u001a\u0001"+
		"\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0003\u001c\u0150\b\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0003\u001c\u0156\b\u001c\u0005\u001c\u0158\b\u001c"+
		"\n\u001c\f\u001c\u015b\t\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003"+
		"\u001d\u016c\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0187\b\u001d\u0005\u001d\u0189"+
		"\b\u001d\n\u001d\f\u001d\u018c\t\u001d\u0001\u001e\u0001\u001e\u0003\u001e"+
		"\u0190\b\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f"+
		"\u0003\u001f\u0197\b\u001f\u0001 \u0001 \u0003 \u019b\b \u0001!\u0001"+
		"!\u0001\"\u0001\"\u0001#\u0001#\u0003#\u01a3\b#\u0001$\u0001$\u0003$\u01a7"+
		"\b$\u0001%\u0001%\u0003%\u01ab\b%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001"+
		"\'\u0001\'\u0003\'\u01b4\b\'\u0001(\u0001(\u0001(\u0001(\u0001(\u0001"+
		")\u0001)\u0001)\u0001)\u0001*\u0005*\u01c0\b*\n*\f*\u01c3\t*\u0001+\u0001"+
		"+\u0003+\u01c7\b+\u0001,\u0001,\u0001,\u0003,\u01cc\b,\u0001-\u0001-\u0001"+
		"-\u0001-\u0001-\u0001-\u0001-\u0001.\u0001.\u0001.\u0001.\u0001/\u0005"+
		"/\u01da\b/\n/\f/\u01dd\t/\u00010\u00010\u00010\u00010\u00011\u00011\u0001"+
		"1\u00011\u00031\u01e7\b1\u00012\u00012\u00013\u00013\u00013\u00013\u0001"+
		"3\u00013\u00013\u00013\u00013\u00013\u00013\u00033\u01f6\b3\u00014\u0001"+
		"4\u00015\u00015\u00016\u00016\u00017\u00017\u00018\u00018\u00019\u0001"+
		"9\u0001:\u0001:\u0001;\u0001;\u0001<\u0001<\u0001=\u0001=\u0001>\u0001"+
		">\u0001>\u0000\u00028:?\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\"+
		"^`bdfhjlnprtvxz|\u0000\t\u0002\u0000\u0007\n\u001c\u001c\u0002\u0000\u0010"+
		"\u0011\u001b\u001c\u0001\u0000\u000b\f\u0001\u0000\u000e\u000f\u0001\u0000"+
		"\u0013\u0014\u0001\u0000\u0001\u0002\u0001\u0000\u0003\u0005\u0002\u0000"+
		"!!CC\u0001\u0000.2\u0222\u0000~\u0001\u0000\u0000\u0000\u0002\u0084\u0001"+
		"\u0000\u0000\u0000\u0004\u0087\u0001\u0000\u0000\u0000\u0006\u009d\u0001"+
		"\u0000\u0000\u0000\b\u009f\u0001\u0000\u0000\u0000\n\u00a6\u0001\u0000"+
		"\u0000\u0000\f\u00ad\u0001\u0000\u0000\u0000\u000e\u00b1\u0001\u0000\u0000"+
		"\u0000\u0010\u00b3\u0001\u0000\u0000\u0000\u0012\u00c1\u0001\u0000\u0000"+
		"\u0000\u0014\u00c3\u0001\u0000\u0000\u0000\u0016\u00c8\u0001\u0000\u0000"+
		"\u0000\u0018\u00cb\u0001\u0000\u0000\u0000\u001a\u00d1\u0001\u0000\u0000"+
		"\u0000\u001c\u00dd\u0001\u0000\u0000\u0000\u001e\u00e0\u0001\u0000\u0000"+
		"\u0000 \u00e8\u0001\u0000\u0000\u0000\"\u00f5\u0001\u0000\u0000\u0000"+
		"$\u00f9\u0001\u0000\u0000\u0000&\u0105\u0001\u0000\u0000\u0000(\u010d"+
		"\u0001\u0000\u0000\u0000*\u0112\u0001\u0000\u0000\u0000,\u0119\u0001\u0000"+
		"\u0000\u0000.\u0121\u0001\u0000\u0000\u00000\u0125\u0001\u0000\u0000\u0000"+
		"2\u012d\u0001\u0000\u0000\u00004\u0134\u0001\u0000\u0000\u00006\u013c"+
		"\u0001\u0000\u0000\u00008\u014f\u0001\u0000\u0000\u0000:\u016b\u0001\u0000"+
		"\u0000\u0000<\u018f\u0001\u0000\u0000\u0000>\u0191\u0001\u0000\u0000\u0000"+
		"@\u019a\u0001\u0000\u0000\u0000B\u019c\u0001\u0000\u0000\u0000D\u019e"+
		"\u0001\u0000\u0000\u0000F\u01a2\u0001\u0000\u0000\u0000H\u01a6\u0001\u0000"+
		"\u0000\u0000J\u01aa\u0001\u0000\u0000\u0000L\u01ac\u0001\u0000\u0000\u0000"+
		"N\u01b3\u0001\u0000\u0000\u0000P\u01b5\u0001\u0000\u0000\u0000R\u01ba"+
		"\u0001\u0000\u0000\u0000T\u01c1\u0001\u0000\u0000\u0000V\u01c6\u0001\u0000"+
		"\u0000\u0000X\u01cb\u0001\u0000\u0000\u0000Z\u01cd\u0001\u0000\u0000\u0000"+
		"\\\u01d4\u0001\u0000\u0000\u0000^\u01db\u0001\u0000\u0000\u0000`\u01de"+
		"\u0001\u0000\u0000\u0000b\u01e6\u0001\u0000\u0000\u0000d\u01e8\u0001\u0000"+
		"\u0000\u0000f\u01f5\u0001\u0000\u0000\u0000h\u01f7\u0001\u0000\u0000\u0000"+
		"j\u01f9\u0001\u0000\u0000\u0000l\u01fb\u0001\u0000\u0000\u0000n\u01fd"+
		"\u0001\u0000\u0000\u0000p\u01ff\u0001\u0000\u0000\u0000r\u0201\u0001\u0000"+
		"\u0000\u0000t\u0203\u0001\u0000\u0000\u0000v\u0205\u0001\u0000\u0000\u0000"+
		"x\u0207\u0001\u0000\u0000\u0000z\u0209\u0001\u0000\u0000\u0000|\u020b"+
		"\u0001\u0000\u0000\u0000~\u007f\u0003\u0002\u0001\u0000\u007f\u0080\u0005"+
		"\u0000\u0000\u0001\u0080\u0001\u0001\u0000\u0000\u0000\u0081\u0083\u0003"+
		"\u0006\u0003\u0000\u0082\u0081\u0001\u0000\u0000\u0000\u0083\u0086\u0001"+
		"\u0000\u0000\u0000\u0084\u0082\u0001\u0000\u0000\u0000\u0084\u0085\u0001"+
		"\u0000\u0000\u0000\u0085\u0003\u0001\u0000\u0000\u0000\u0086\u0084\u0001"+
		"\u0000\u0000\u0000\u0087\u008b\u0005\u0019\u0000\u0000\u0088\u008a\u0003"+
		"\u0006\u0003\u0000\u0089\u0088\u0001\u0000\u0000\u0000\u008a\u008d\u0001"+
		"\u0000\u0000\u0000\u008b\u0089\u0001\u0000\u0000\u0000\u008b\u008c\u0001"+
		"\u0000\u0000\u0000\u008c\u008e\u0001\u0000\u0000\u0000\u008d\u008b\u0001"+
		"\u0000\u0000\u0000\u008e\u008f\u0005\u001a\u0000\u0000\u008f\u0005\u0001"+
		"\u0000\u0000\u0000\u0090\u009e\u00030\u0018\u0000\u0091\u009e\u00032\u0019"+
		"\u0000\u0092\u009e\u0003\b\u0004\u0000\u0093\u009e\u0003\n\u0005\u0000"+
		"\u0094\u009e\u0003\f\u0006\u0000\u0095\u009e\u0003\u000e\u0007\u0000\u0096"+
		"\u009e\u0003\u0010\b\u0000\u0097\u009e\u0003\u0012\t\u0000\u0098\u009e"+
		"\u0003\u001a\r\u0000\u0099\u009e\u0003\u0014\n\u0000\u009a\u009e\u0003"+
		"\u0016\u000b\u0000\u009b\u009e\u0003$\u0012\u0000\u009c\u009e\u0003:\u001d"+
		"\u0000\u009d\u0090\u0001\u0000\u0000\u0000\u009d\u0091\u0001\u0000\u0000"+
		"\u0000\u009d\u0092\u0001\u0000\u0000\u0000\u009d\u0093\u0001\u0000\u0000"+
		"\u0000\u009d\u0094\u0001\u0000\u0000\u0000\u009d\u0095\u0001\u0000\u0000"+
		"\u0000\u009d\u0096\u0001\u0000\u0000\u0000\u009d\u0097\u0001\u0000\u0000"+
		"\u0000\u009d\u0098\u0001\u0000\u0000\u0000\u009d\u0099\u0001\u0000\u0000"+
		"\u0000\u009d\u009a\u0001\u0000\u0000\u0000\u009d\u009b\u0001\u0000\u0000"+
		"\u0000\u009d\u009c\u0001\u0000\u0000\u0000\u009e\u0007\u0001\u0000\u0000"+
		"\u0000\u009f\u00a2\u0005*\u0000\u0000\u00a0\u00a1\u0005%\u0000\u0000\u00a1"+
		"\u00a3\u0003:\u001d\u0000\u00a2\u00a0\u0001\u0000\u0000\u0000\u00a2\u00a3"+
		"\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4\u00a5"+
		"\u0003\u0004\u0002\u0000\u00a5\t\u0001\u0000\u0000\u0000\u00a6\u00a7\u0005"+
		"(\u0000\u0000\u00a7\u00a8\u0003h4\u0000\u00a8\u00a9\u0005)\u0000\u0000"+
		"\u00a9\u00aa\u0003:\u001d\u0000\u00aa\u00ab\u0003\u0004\u0002\u0000\u00ab"+
		"\u000b\u0001\u0000\u0000\u0000\u00ac\u00ae\u0003:\u001d\u0000\u00ad\u00ac"+
		"\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u00af"+
		"\u0001\u0000\u0000\u0000\u00af\u00b0\u00056\u0000\u0000\u00b0\r\u0001"+
		"\u0000\u0000\u0000\u00b1\u00b2\u0005+\u0000\u0000\u00b2\u000f\u0001\u0000"+
		"\u0000\u0000\u00b3\u00b4\u0005,\u0000\u0000\u00b4\u0011\u0001\u0000\u0000"+
		"\u0000\u00b5\u00b6\u0003:\u001d\u0000\u00b6\u00b7\u0005\u001c\u0000\u0000"+
		"\u00b7\u00b8\u0003\u0016\u000b\u0000\u00b8\u00c2\u0001\u0000\u0000\u0000"+
		"\u00b9\u00ba\u0003:\u001d\u0000\u00ba\u00bb\u0005\u001c\u0000\u0000\u00bb"+
		"\u00bc\u0003@ \u0000\u00bc\u00c2\u0001\u0000\u0000\u0000\u00bd\u00be\u0003"+
		":\u001d\u0000\u00be\u00bf\u0007\u0000\u0000\u0000\u00bf\u00c0\u0003h4"+
		"\u0000\u00c0\u00c2\u0001\u0000\u0000\u0000\u00c1\u00b5\u0001\u0000\u0000"+
		"\u0000\u00c1\u00b9\u0001\u0000\u0000\u0000\u00c1\u00bd\u0001\u0000\u0000"+
		"\u0000\u00c2\u0013\u0001\u0000\u0000\u0000\u00c3\u00c4\u0005-\u0000\u0000"+
		"\u00c4\u00c5\u0003h4\u0000\u00c5\u00c6\u0003F#\u0000\u00c6\u00c7\u0003"+
		":\u001d\u0000\u00c7\u0015\u0001\u0000\u0000\u0000\u00c8\u00c9\u0003h4"+
		"\u0000\u00c9\u00ca\u0003F#\u0000\u00ca\u0017\u0001\u0000\u0000\u0000\u00cb"+
		"\u00cc\u0003|>\u0000\u00cc\u00cd\u0003:\u001d\u0000\u00cd\u0019\u0001"+
		"\u0000\u0000\u0000\u00ce\u00cf\u0003h4\u0000\u00cf\u00d0\u0005\"\u0000"+
		"\u0000\u00d0\u00d2\u0001\u0000\u0000\u0000\u00d1\u00ce\u0001\u0000\u0000"+
		"\u0000\u00d1\u00d2\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000"+
		"\u0000\u00d3\u00d4\u0003h4\u0000\u00d4\u00d7\u0005\u0015\u0000\u0000\u00d5"+
		"\u00d8\u0003\u001e\u000f\u0000\u00d6\u00d8\u0003\u001c\u000e\u0000\u00d7"+
		"\u00d5\u0001\u0000\u0000\u0000\u00d7\u00d6\u0001\u0000\u0000\u0000\u00d8"+
		"\u00d9\u0001\u0000\u0000\u0000\u00d9\u00d7\u0001\u0000\u0000\u0000\u00d9"+
		"\u00da\u0001\u0000\u0000\u0000\u00da\u00db\u0001\u0000\u0000\u0000\u00db"+
		"\u00dc\u0005\u0016\u0000\u0000\u00dc\u001b\u0001\u0000\u0000\u0000\u00dd"+
		"\u00de\u0003h4\u0000\u00de\u00df\u0003\u001a\r\u0000\u00df\u001d\u0001"+
		"\u0000\u0000\u0000\u00e0\u00e1\u0003h4\u0000\u00e1\u00e3\u0003F#\u0000"+
		"\u00e2\u00e4\u0003\u0018\f\u0000\u00e3\u00e2\u0001\u0000\u0000\u0000\u00e3"+
		"\u00e4\u0001\u0000\u0000\u0000\u00e4\u001f\u0001\u0000\u0000\u0000\u00e5"+
		"\u00e6\u0003h4\u0000\u00e6\u00e7\u0005\"\u0000\u0000\u00e7\u00e9\u0001"+
		"\u0000\u0000\u0000\u00e8\u00e5\u0001\u0000\u0000\u0000\u00e8\u00e9\u0001"+
		"\u0000\u0000\u0000\u00e9\u00ea\u0001\u0000\u0000\u0000\u00ea\u00ec\u0005"+
		"\u0015\u0000\u0000\u00eb\u00ed\u0003\"\u0011\u0000\u00ec\u00eb\u0001\u0000"+
		"\u0000\u0000\u00ed\u00ee\u0001\u0000\u0000\u0000\u00ee\u00ec\u0001\u0000"+
		"\u0000\u0000\u00ee\u00ef\u0001\u0000\u0000\u0000\u00ef\u00f0\u0001\u0000"+
		"\u0000\u0000\u00f0\u00f1\u0005\u0016\u0000\u0000\u00f1!\u0001\u0000\u0000"+
		"\u0000\u00f2\u00f3\u0003h4\u0000\u00f3\u00f4\u0005\"\u0000\u0000\u00f4"+
		"\u00f6\u0001\u0000\u0000\u0000\u00f5\u00f2\u0001\u0000\u0000\u0000\u00f5"+
		"\u00f6\u0001\u0000\u0000\u0000\u00f6\u00f7\u0001\u0000\u0000\u0000\u00f7"+
		"\u00f8\u0003:\u001d\u0000\u00f8#\u0001\u0000\u0000\u0000\u00f9\u00fa\u0003"+
		"h4\u0000\u00fa\u00fc\u0005\u0015\u0000\u0000\u00fb\u00fd\u0003&\u0013"+
		"\u0000\u00fc\u00fb\u0001\u0000\u0000\u0000\u00fc\u00fd\u0001\u0000\u0000"+
		"\u0000\u00fd\u00fe\u0001\u0000\u0000\u0000\u00fe\u0101\u0005\u0016\u0000"+
		"\u0000\u00ff\u0100\u00056\u0000\u0000\u0100\u0102\u0003F#\u0000\u0101"+
		"\u00ff\u0001\u0000\u0000\u0000\u0101\u0102\u0001\u0000\u0000\u0000\u0102"+
		"\u0103\u0001\u0000\u0000\u0000\u0103\u0104\u0003\u0004\u0002\u0000\u0104"+
		"%\u0001\u0000\u0000\u0000\u0105\u010a\u0003(\u0014\u0000\u0106\u0107\u0005"+
		"\u001f\u0000\u0000\u0107\u0109\u0003(\u0014\u0000\u0108\u0106\u0001\u0000"+
		"\u0000\u0000\u0109\u010c\u0001\u0000\u0000\u0000\u010a\u0108\u0001\u0000"+
		"\u0000\u0000\u010a\u010b\u0001\u0000\u0000\u0000\u010b\'\u0001\u0000\u0000"+
		"\u0000\u010c\u010a\u0001\u0000\u0000\u0000\u010d\u010e\u0003h4\u0000\u010e"+
		"\u0110\u0003F#\u0000\u010f\u0111\u0003\u0018\f\u0000\u0110\u010f\u0001"+
		"\u0000\u0000\u0000\u0110\u0111\u0001\u0000\u0000\u0000\u0111)\u0001\u0000"+
		"\u0000\u0000\u0112\u0113\u0003h4\u0000\u0113\u0115\u0005\u0015\u0000\u0000"+
		"\u0114\u0116\u0003,\u0016\u0000\u0115\u0114\u0001\u0000\u0000\u0000\u0115"+
		"\u0116\u0001\u0000\u0000\u0000\u0116\u0117\u0001\u0000\u0000\u0000\u0117"+
		"\u0118\u0005\u0016\u0000\u0000\u0118+\u0001\u0000\u0000\u0000\u0119\u011c"+
		"\u0003.\u0017\u0000\u011a\u011b\u0005\u001f\u0000\u0000\u011b\u011d\u0003"+
		".\u0017\u0000\u011c\u011a\u0001\u0000\u0000\u0000\u011c\u011d\u0001\u0000"+
		"\u0000\u0000\u011d-\u0001\u0000\u0000\u0000\u011e\u011f\u0003h4\u0000"+
		"\u011f\u0120\u0005\"\u0000\u0000\u0120\u0122\u0001\u0000\u0000\u0000\u0121"+
		"\u011e\u0001\u0000\u0000\u0000\u0121\u0122\u0001\u0000\u0000\u0000\u0122"+
		"\u0123\u0001\u0000\u0000\u0000\u0123\u0124\u0003:\u001d\u0000\u0124/\u0001"+
		"\u0000\u0000\u0000\u0125\u0126\u0005%\u0000\u0000\u0126\u0127\u0003:\u001d"+
		"\u0000\u0127\u0128\u0005&\u0000\u0000\u0128\u012b\u0003\u0004\u0002\u0000"+
		"\u0129\u012a\u0005\'\u0000\u0000\u012a\u012c\u0003\u0004\u0002\u0000\u012b"+
		"\u0129\u0001\u0000\u0000\u0000\u012b\u012c\u0001\u0000\u0000\u0000\u012c"+
		"1\u0001\u0000\u0000\u0000\u012d\u012e\u0005%\u0000\u0000\u012e\u012f\u0003"+
		":\u001d\u0000\u012f\u0132\u00034\u001a\u0000\u0130\u0131\u0005\'\u0000"+
		"\u0000\u0131\u0133\u0003\u0004\u0002\u0000\u0132\u0130\u0001\u0000\u0000"+
		"\u0000\u0132\u0133\u0001\u0000\u0000\u0000\u01333\u0001\u0000\u0000\u0000"+
		"\u0134\u0136\u0005\u0019\u0000\u0000\u0135\u0137\u00036\u001b\u0000\u0136"+
		"\u0135\u0001\u0000\u0000\u0000\u0137\u0138\u0001\u0000\u0000\u0000\u0138"+
		"\u0136\u0001\u0000\u0000\u0000\u0138\u0139\u0001\u0000\u0000\u0000\u0139"+
		"\u013a\u0001\u0000\u0000\u0000\u013a\u013b\u0005\u001a\u0000\u0000\u013b"+
		"5\u0001\u0000\u0000\u0000\u013c\u013d\u00038\u001c\u0000\u013d\u013e\u0005"+
		"&\u0000\u0000\u013e\u013f\u0003\u0004\u0002\u0000\u013f7\u0001\u0000\u0000"+
		"\u0000\u0140\u0141\u0006\u001c\uffff\uffff\u0000\u0141\u0142\u0005\u0019"+
		"\u0000\u0000\u0142\u0143\u00038\u001c\u0000\u0143\u0144\u0005\u001a\u0000"+
		"\u0000\u0144\u0150\u0001\u0000\u0000\u0000\u0145\u0146\u0007\u0001\u0000"+
		"\u0000\u0146\u0150\u0003:\u001d\u0000\u0147\u0148\u0005\u0013\u0000\u0000"+
		"\u0148\u0150\u0003:\u001d\u0000\u0149\u014a\u0005\u0014\u0000\u0000\u014a"+
		"\u0150\u0003:\u001d\u0000\u014b\u014c\u0007\u0002\u0000\u0000\u014c\u0150"+
		"\u0003:\u001d\u0000\u014d\u014e\u0007\u0003\u0000\u0000\u014e\u0150\u0003"+
		":\u001d\u0000\u014f\u0140\u0001\u0000\u0000\u0000\u014f\u0145\u0001\u0000"+
		"\u0000\u0000\u014f\u0147\u0001\u0000\u0000\u0000\u014f\u0149\u0001\u0000"+
		"\u0000\u0000\u014f\u014b\u0001\u0000\u0000\u0000\u014f\u014d\u0001\u0000"+
		"\u0000\u0000\u0150\u0159\u0001\u0000\u0000\u0000\u0151\u0152\n\u0001\u0000"+
		"\u0000\u0152\u0155\u0007\u0004\u0000\u0000\u0153\u0156\u00038\u001c\u0000"+
		"\u0154\u0156\u0003:\u001d\u0000\u0155\u0153\u0001\u0000\u0000\u0000\u0155"+
		"\u0154\u0001\u0000\u0000\u0000\u0156\u0158\u0001\u0000\u0000\u0000\u0157"+
		"\u0151\u0001\u0000\u0000\u0000\u0158\u015b\u0001\u0000\u0000\u0000\u0159"+
		"\u0157\u0001\u0000\u0000\u0000\u0159\u015a\u0001\u0000\u0000\u0000\u015a"+
		"9\u0001\u0000\u0000\u0000\u015b\u0159\u0001\u0000\u0000\u0000\u015c\u015d"+
		"\u0006\u001d\uffff\uffff\u0000\u015d\u016c\u0003<\u001e\u0000\u015e\u015f"+
		"\u0005\u0019\u0000\u0000\u015f\u0160\u0003:\u001d\u0000\u0160\u0161\u0005"+
		"\u001a\u0000\u0000\u0161\u016c\u0001\u0000\u0000\u0000\u0162\u016c\u0003"+
		"L&\u0000\u0163\u016c\u0003R)\u0000\u0164\u016c\u0003\\.\u0000\u0165\u016c"+
		"\u0003 \u0010\u0000\u0166\u016c\u0003*\u0015\u0000\u0167\u0168\u0007\u0005"+
		"\u0000\u0000\u0168\u016c\u0003:\u001d\t\u0169\u016a\u0005\u0012\u0000"+
		"\u0000\u016a\u016c\u0003:\u001d\b\u016b\u015c\u0001\u0000\u0000\u0000"+
		"\u016b\u015e\u0001\u0000\u0000\u0000\u016b\u0162\u0001\u0000\u0000\u0000"+
		"\u016b\u0163\u0001\u0000\u0000\u0000\u016b\u0164\u0001\u0000\u0000\u0000"+
		"\u016b\u0165\u0001\u0000\u0000\u0000\u016b\u0166\u0001\u0000\u0000\u0000"+
		"\u016b\u0167\u0001\u0000\u0000\u0000\u016b\u0169\u0001\u0000\u0000\u0000"+
		"\u016c\u018a\u0001\u0000\u0000\u0000\u016d\u016e\n\u0007\u0000\u0000\u016e"+
		"\u016f\u0005\u0006\u0000\u0000\u016f\u0189\u0003:\u001d\b\u0170\u0171"+
		"\n\u0006\u0000\u0000\u0171\u0172\u0007\u0006\u0000\u0000\u0172\u0189\u0003"+
		":\u001d\u0007\u0173\u0174\n\u0005\u0000\u0000\u0174\u0175\u0007\u0005"+
		"\u0000\u0000\u0175\u0189\u0003:\u001d\u0006\u0176\u0177\n\u0004\u0000"+
		"\u0000\u0177\u0178\u0007\u0001\u0000\u0000\u0178\u0189\u0003:\u001d\u0005"+
		"\u0179\u017a\n\u0003\u0000\u0000\u017a\u017b\u0007\u0002\u0000\u0000\u017b"+
		"\u0189\u0003:\u001d\u0004\u017c\u017d\n\u0002\u0000\u0000\u017d\u017e"+
		"\u0007\u0003\u0000\u0000\u017e\u0189\u0003:\u001d\u0003\u017f\u0180\n"+
		"\u0001\u0000\u0000\u0180\u0181\u0007\u0004\u0000\u0000\u0181\u0189\u0003"+
		":\u001d\u0002\u0182\u0183\n\u000f\u0000\u0000\u0183\u0186\u0005\u001d"+
		"\u0000\u0000\u0184\u0187\u0003h4\u0000\u0185\u0187\u0003*\u0015\u0000"+
		"\u0186\u0184\u0001\u0000\u0000\u0000\u0186\u0185\u0001\u0000\u0000\u0000"+
		"\u0187\u0189\u0001\u0000\u0000\u0000\u0188\u016d\u0001\u0000\u0000\u0000"+
		"\u0188\u0170\u0001\u0000\u0000\u0000\u0188\u0173\u0001\u0000\u0000\u0000"+
		"\u0188\u0176\u0001\u0000\u0000\u0000\u0188\u0179\u0001\u0000\u0000\u0000"+
		"\u0188\u017c\u0001\u0000\u0000\u0000\u0188\u017f\u0001\u0000\u0000\u0000"+
		"\u0188\u0182\u0001\u0000\u0000\u0000\u0189\u018c\u0001\u0000\u0000\u0000"+
		"\u018a\u0188\u0001\u0000\u0000\u0000\u018a\u018b\u0001\u0000\u0000\u0000"+
		"\u018b;\u0001\u0000\u0000\u0000\u018c\u018a\u0001\u0000\u0000\u0000\u018d"+
		"\u0190\u0003f3\u0000\u018e\u0190\u0003h4\u0000\u018f\u018d\u0001\u0000"+
		"\u0000\u0000\u018f\u018e\u0001\u0000\u0000\u0000\u0190=\u0001\u0000\u0000"+
		"\u0000\u0191\u0192\u0003:\u001d\u0000\u0192\u0193\u0005\u001e\u0000\u0000"+
		"\u0193\u0196\u0003:\u001d\u0000\u0194\u0195\u0005\u001e\u0000\u0000\u0195"+
		"\u0197\u0003:\u001d\u0000\u0196\u0194\u0001\u0000\u0000\u0000\u0196\u0197"+
		"\u0001\u0000\u0000\u0000\u0197?\u0001\u0000\u0000\u0000\u0198\u019b\u0003"+
		"B!\u0000\u0199\u019b\u0003D\"\u0000\u019a\u0198\u0001\u0000\u0000\u0000"+
		"\u019a\u0199\u0001\u0000\u0000\u0000\u019bA\u0001\u0000\u0000\u0000\u019c"+
		"\u019d\u0007\u0007\u0000\u0000\u019dC\u0001\u0000\u0000\u0000\u019e\u019f"+
		"\u0005D\u0000\u0000\u019fE\u0001\u0000\u0000\u0000\u01a0\u01a3\u0003H"+
		"$\u0000\u01a1\u01a3\u0003d2\u0000\u01a2\u01a0\u0001\u0000\u0000\u0000"+
		"\u01a2\u01a1\u0001\u0000\u0000\u0000\u01a3G\u0001\u0000\u0000\u0000\u01a4"+
		"\u01a7\u0003J%\u0000\u01a5\u01a7\u0003h4\u0000\u01a6\u01a4\u0001\u0000"+
		"\u0000\u0000\u01a6\u01a5\u0001\u0000\u0000\u0000\u01a7I\u0001\u0000\u0000"+
		"\u0000\u01a8\u01ab\u0003P(\u0000\u01a9\u01ab\u0003Z-\u0000\u01aa\u01a8"+
		"\u0001\u0000\u0000\u0000\u01aa\u01a9\u0001\u0000\u0000\u0000\u01abK\u0001"+
		"\u0000\u0000\u0000\u01ac\u01ad\u0003h4\u0000\u01ad\u01ae\u0005\u0017\u0000"+
		"\u0000\u01ae\u01af\u0003N\'\u0000\u01af\u01b0\u0005\u0018\u0000\u0000"+
		"\u01b0M\u0001\u0000\u0000\u0000\u01b1\u01b4\u0003X,\u0000\u01b2\u01b4"+
		"\u0003b1\u0000\u01b3\u01b1\u0001\u0000\u0000\u0000\u01b3\u01b2\u0001\u0000"+
		"\u0000\u0000\u01b4O\u0001\u0000\u0000\u0000\u01b5\u01b6\u00053\u0000\u0000"+
		"\u01b6\u01b7\u0005\u0017\u0000\u0000\u01b7\u01b8\u0003F#\u0000\u01b8\u01b9"+
		"\u0005\u0018\u0000\u0000\u01b9Q\u0001\u0000\u0000\u0000\u01ba\u01bb\u0005"+
		"\u0017\u0000\u0000\u01bb\u01bc\u0003T*\u0000\u01bc\u01bd\u0005\u0018\u0000"+
		"\u0000\u01bdS\u0001\u0000\u0000\u0000\u01be\u01c0\u0003V+\u0000\u01bf"+
		"\u01be\u0001\u0000\u0000\u0000\u01c0\u01c3\u0001\u0000\u0000\u0000\u01c1"+
		"\u01bf\u0001\u0000\u0000\u0000\u01c1\u01c2\u0001\u0000\u0000\u0000\u01c2"+
		"U\u0001\u0000\u0000\u0000\u01c3\u01c1\u0001\u0000\u0000\u0000\u01c4\u01c7"+
		"\u0003:\u001d\u0000\u01c5\u01c7\u0003>\u001f\u0000\u01c6\u01c4\u0001\u0000"+
		"\u0000\u0000\u01c6\u01c5\u0001\u0000\u0000\u0000\u01c7W\u0001\u0000\u0000"+
		"\u0000\u01c8\u01cc\u0005\u0005\u0000\u0000\u01c9\u01cc\u0005$\u0000\u0000"+
		"\u01ca\u01cc\u0003:\u001d\u0000\u01cb\u01c8\u0001\u0000\u0000\u0000\u01cb"+
		"\u01c9\u0001\u0000\u0000\u0000\u01cb\u01ca\u0001\u0000\u0000\u0000\u01cc"+
		"Y\u0001\u0000\u0000\u0000\u01cd\u01ce\u00054\u0000\u0000\u01ce\u01cf\u0005"+
		"\u0017\u0000\u0000\u01cf\u01d0\u0003F#\u0000\u01d0\u01d1\u0005\"\u0000"+
		"\u0000\u01d1\u01d2\u0003F#\u0000\u01d2\u01d3\u0005\u0018\u0000\u0000\u01d3"+
		"[\u0001\u0000\u0000\u0000\u01d4\u01d5\u0005\u0017\u0000\u0000\u01d5\u01d6"+
		"\u0003^/\u0000\u01d6\u01d7\u0005\u0018\u0000\u0000\u01d7]\u0001\u0000"+
		"\u0000\u0000\u01d8\u01da\u0003`0\u0000\u01d9\u01d8\u0001\u0000\u0000\u0000"+
		"\u01da\u01dd\u0001\u0000\u0000\u0000\u01db\u01d9\u0001\u0000\u0000\u0000"+
		"\u01db\u01dc\u0001\u0000\u0000\u0000\u01dc_\u0001\u0000\u0000\u0000\u01dd"+
		"\u01db\u0001\u0000\u0000\u0000\u01de\u01df\u0003:\u001d\u0000\u01df\u01e0"+
		"\u0005\"\u0000\u0000\u01e0\u01e1\u0003:\u001d\u0000\u01e1a\u0001\u0000"+
		"\u0000\u0000\u01e2\u01e7\u0005\u0005\u0000\u0000\u01e3\u01e7\u0005$\u0000"+
		"\u0000\u01e4\u01e7\u0005\u0003\u0000\u0000\u01e5\u01e7\u0003:\u001d\u0000"+
		"\u01e6\u01e2\u0001\u0000\u0000\u0000\u01e6\u01e3\u0001\u0000\u0000\u0000"+
		"\u01e6\u01e4\u0001\u0000\u0000\u0000\u01e6\u01e5\u0001\u0000\u0000\u0000"+
		"\u01e7c\u0001\u0000\u0000\u0000\u01e8\u01e9\u0007\b\u0000\u0000\u01e9"+
		"e\u0001\u0000\u0000\u0000\u01ea\u01f6\u0003j5\u0000\u01eb\u01f6\u0003"+
		"l6\u0000\u01ec\u01f6\u0003n7\u0000\u01ed\u01f6\u0003p8\u0000\u01ee\u01f6"+
		"\u0003r9\u0000\u01ef\u01f6\u0003t:\u0000\u01f0\u01f6\u0003v;\u0000\u01f1"+
		"\u01f6\u0003x<\u0000\u01f2\u01f6\u0003z=\u0000\u01f3\u01f6\u0003h4\u0000"+
		"\u01f4\u01f6\u0003|>\u0000\u01f5\u01ea\u0001\u0000\u0000\u0000\u01f5\u01eb"+
		"\u0001\u0000\u0000\u0000\u01f5\u01ec\u0001\u0000\u0000\u0000\u01f5\u01ed"+
		"\u0001\u0000\u0000\u0000\u01f5\u01ee\u0001\u0000\u0000\u0000\u01f5\u01ef"+
		"\u0001\u0000\u0000\u0000\u01f5\u01f0\u0001\u0000\u0000\u0000\u01f5\u01f1"+
		"\u0001\u0000\u0000\u0000\u01f5\u01f2\u0001\u0000\u0000\u0000\u01f5\u01f3"+
		"\u0001\u0000\u0000\u0000\u01f5\u01f4\u0001\u0000\u0000\u0000\u01f6g\u0001"+
		"\u0000\u0000\u0000\u01f7\u01f8\u0005B\u0000\u0000\u01f8i\u0001\u0000\u0000"+
		"\u0000\u01f9\u01fa\u0005:\u0000\u0000\u01fak\u0001\u0000\u0000\u0000\u01fb"+
		"\u01fc\u0005;\u0000\u0000\u01fcm\u0001\u0000\u0000\u0000\u01fd\u01fe\u0005"+
		"<\u0000\u0000\u01feo\u0001\u0000\u0000\u0000\u01ff\u0200\u0005=\u0000"+
		"\u0000\u0200q\u0001\u0000\u0000\u0000\u0201\u0202\u0005>\u0000\u0000\u0202"+
		"s\u0001\u0000\u0000\u0000\u0203\u0204\u0005?\u0000\u0000\u0204u\u0001"+
		"\u0000\u0000\u0000\u0205\u0206\u0005@\u0000\u0000\u0206w\u0001\u0000\u0000"+
		"\u0000\u0207\u0208\u0005A\u0000\u0000\u0208y\u0001\u0000\u0000\u0000\u0209"+
		"\u020a\u00055\u0000\u0000\u020a{\u0001\u0000\u0000\u0000\u020b\u020c\u0005"+
		" \u0000\u0000\u020c}\u0001\u0000\u0000\u0000+\u0084\u008b\u009d\u00a2"+
		"\u00ad\u00c1\u00d1\u00d7\u00d9\u00e3\u00e8\u00ee\u00f5\u00fc\u0101\u010a"+
		"\u0110\u0115\u011c\u0121\u012b\u0132\u0138\u014f\u0155\u0159\u016b\u0186"+
		"\u0188\u018a\u018f\u0196\u019a\u01a2\u01a6\u01aa\u01b3\u01c1\u01c6\u01cb"+
		"\u01db\u01e6\u01f5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}