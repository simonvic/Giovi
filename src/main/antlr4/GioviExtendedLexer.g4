lexer grammar GioviExtendedLexer;

import GioviLexer;

WS: [ \t\r\n\u000C]+ -> channel(3);

COMMENT:       '#' (~[?{\r\n] ~[\r\n]*)? -> channel(1) ;
COMMENT_BLOCK: '#{' .*? ('}#' | EOF) -> channel(1) ;
DOC:           '#?' (~[{\r\n] ~[\r\n]*)? -> channel(2) ;
DOC_BLOCK:     '#?{' .*? ('}#' | EOF) -> channel(2) ;
