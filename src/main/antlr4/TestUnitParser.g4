parser grammar TestUnitParser;

options {
    tokenVocab=TestUnitLexer;
}

begin: unit* EOF;

titleSeparator:  EQUAL EQUAL EQUAL EQUAL EQUAL* ;
codeSeparator:  DASH DASH DASH DASH DASH* ;

unit: titleSeparator
	title=text
	titleSeparator
	code=text
	codeSeparator
	tree=text
	;

text: TEXT+ ;

