package it.simonvic.giovi.exceptions;

import org.antlr.v4.runtime.Token;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviValue;
import it.simonvic.giovi.ValueWrapper;
import it.simonvic.giovi.antlr.GioviLexer;

public class OperatorNotSupported extends GioviException {

	public OperatorNotSupported(
		GioviParserVisitResult lhs,
		Token token,
		GioviParserVisitResult rhs
	) {
		this(lhs, token.getType(), rhs);
	}

	public OperatorNotSupported(GioviParserVisitResult operand, Token token) {
		this(operand, token.getType());
	}

	public OperatorNotSupported(
		GioviParserVisitResult lhs,
		int tokenType,
		GioviParserVisitResult rhs
	) {
		super(
			"""
			Operator %s (%s) not supported
				by:   %s
				with: %s
			""".formatted(
				GioviLexer.VOCABULARY.getSymbolicName(tokenType),
				GioviLexer.VOCABULARY.getDisplayName(tokenType),
				operandToString(lhs),
				operandToString(rhs)
			)
		);
	}

	public OperatorNotSupported(GioviParserVisitResult operand, int tokenType) {
		super(
			"""
			Operator %s (%s) not supported
				by:   %s
			""".formatted(
				GioviLexer.VOCABULARY.getSymbolicName(tokenType),
				GioviLexer.VOCABULARY.getDisplayName(tokenType),
				operandToString(operand)
			)
		);
	}

	private static String operandToString(GioviParserVisitResult operand) {
		return switch (operand) {
			case GioviValue<?> v -> v.getGioviType().toString() + " (" + v + ")";
			case ValueWrapper<?> v -> v.getGioviType().toString() + " (" + v + ")";
			default -> operand.toString();
		};
	}

}
