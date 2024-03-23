package it.simonvic.giovi;

import java.util.HashMap;
import java.util.Map;

public enum GioviToken {


	//@formatter:off
	PLUS                          ("+"),
	MINUS                         ("-"),
	MULT                          ("*"),
	DIV                           ("/"),
	MODULO                        ("%"),
	EXPONENTIAL                   ("^"),
	REDIR                         ("->"),
	REDIR_PLUS                    ("->+"),
	REDIR_MINUS                   ("->-"),
	REDIR_MULT                    ("->*"),
	REDIR_DIV                     ("->/"),
	REDIR_RTL                     ("<-"),
	REDIR_PLUS_RTL                ("+<-"),
	REDIR_MINUS_RTL               ("-<-"),
	REDIR_MULT_RTL                ("*<-"),
	REDIR_DIV_RTL                 ("/<-"),
	EQUAL                         ("="),
	NOT_EQUAL                     ("!="),
	TILDE                         ("~"),
	CONTAINS                      ("=~"),
	CONTAINED                     ("~="),
	GREATER_EQUAL                 (">="),
	LESS_EQUAL                    ("<="),
	RETURN_ARROW                  ("=>"),
	LESS                          ("<"),
	GREATER                       (">"),
	NOT_SYM                       ("!"),
	AND_SYM                       ("&"),
	OR_SYM                        ("|"),
	NOT                           ("not"),
	AND                           ("and"),
	OR                            ("or"),
	BRACKET_ROUND_OPEN            ("("),
	BRACKET_ROUND_CLOSE           (")"),
	BRACKET_SQUARE_OPEN           ("["),
	BRACKET_SQUARE_CLOSE          ("]"),
	BRACKET_CURLY_OPEN            ("{"),
	BRACKET_CURLY_CLOSE           ("}"),
	DOT                           ("."),
	RANGE                         (".."),
	COMMA                         (","),
	UNDERSCORE                    ("_"),
	BACKSLASH                     ("\\"),
	COLON                         (":"),
	SEMICOLON                     (";"),
	HASHTAG                       ("#"),
	AT                            ("@"),
	IF                            ("if"),
	DO                            ("do"),
	ELSE                          ("else"),
	FOR                           ("for"),
	IN                            ("in"),
	REPEAT                        ("repeat"),
	BREAK                         ("break"),
	CONTINUE                      ("skip"),
	CONSTANT                      ("constant"),
	RETURN                        ("return"),
	IMPORT                        ("import"),
	DEFAULT                       ("default"),
	ALIAS                         ("alias"),
	AS                            ("as"),
	IS                            ("is"),
	SELF                          ("self");
	//@formatter:on

	private static final Map<String, GioviToken> cache = new HashMap<>();

	static {
		for (var token : values()) {
			cache.put(token.label, token);
		}
	}

	public static GioviToken of(String label) {
		return cache.get(label);
	}

	public final String label;

	GioviToken(String label) {
		this.label = label;
	}


}
