package it.simonvic.giovi.util;

import static it.simonvic.giovi.antlr.GioviLexer.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.antlr.v4.runtime.Token;

import giovi.GRecord;
import giovi.GTuple;
import giovi.GioviParserVisitResult;
import giovi.lang.*;
import it.simonvic.giovi.GField;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.exceptions.*;
import it.simonvic.giovi.lang.Scope;

public class VisitorUtils {

	public static GioviParserVisitResult bitwiseNot(GioviParserVisitResult value) {
		return value.bitwiseNot();
	}

	public static GioviParserVisitResult bitwiseBool(
		GioviParserVisitResult lhs,
		Token bop,
		GioviParserVisitResult rhs
	) {
		return switch (bop.getType()) {
			case BIT_AND -> lhs.bitwiseAnd(rhs);
			case BIT_OR -> lhs.bitwiseOr(rhs);
			case BIT_XOR -> lhs.bitwiseXor(rhs);
			default -> throw new OperatorNotSupported(lhs, bop, rhs);
		};
	}

	public static GioviParserVisitResult bitwiseShift(
		GioviParserVisitResult lhs,
		Token bop,
		GioviParserVisitResult rhs
	) {
		return switch (bop.getType()) {
			case BIT_LSHIFT -> lhs.bitwiseLeftShift(rhs);
			case BIT_RSHIFT -> lhs.bitwiseRightShift(rhs);
			case BIT_RUSHIFT -> lhs.bitwiseRightUnsignedShift(rhs);
			default -> throw new OperatorNotSupported(lhs, bop, rhs);
		};
	}

	public static GioviParserVisitResult prefixed(Token prefix, GioviParserVisitResult value) {
		return switch (prefix.getType()) {
			case PLUS -> value.positive();
			case MINUS -> value.negative();
			default -> throw new OperatorNotSupported(value, prefix);
		};
	}

	public static GioviParserVisitResult additive(
		GioviParserVisitResult left,
		Token bop,
		GioviParserVisitResult right
	) {
		return switch (bop.getType()) {
			case PLUS -> left.plus(right);
			case MINUS -> left.minus(right);
			default -> throw new OperatorNotSupported(left, bop, right);
		};
	}

	public static GioviParserVisitResult multiplicative(
		GioviParserVisitResult left,
		Token bop,
		GioviParserVisitResult right
	) {
		return switch (bop.getType()) {
			case MULTIPLICATION -> left.multiply(right);
			case DIVISION -> left.divide(right);
			case MODULO -> left.modulo(right);
			default -> throw new OperatorNotSupported(left, bop, right);
		};
	}

	public static GioviParserVisitResult relational(
		GioviParserVisitResult left,
		Token bop,
		GioviParserVisitResult right
	) {
		return switch (bop.getType()) {
			case LESS_EQUAL -> left.lessEqual(right);
			case GREATER_EQUAL -> left.greaterEqual(right);
			case BRACKET_ANGULAR_CLOSE -> left.greater(right);
			case BRACKET_ANGULAR_OPEN -> left.less(right);
			default -> throw new OperatorNotSupported(left, bop, right);
		};
	}

	public static GioviParserVisitResult bool(
		GioviParserVisitResult left,
		Token bop,
		GioviParserVisitResult right
	) {
		return switch (bop.getType()) {
			case AND, AND_SYM -> left.and(right);
			case OR, OR_SYM -> left.or(right);
			default -> throw new OperatorNotSupported(left, bop, right);
		};
	}

	@SuppressWarnings("incomplete-switch")
	public static GioviParserVisitResult bool(
		GioviParserVisitResult lhs,
		Token bop,
		Supplier<GioviParserVisitResult> rhsVisit
	) {
		if (lhs instanceof GioviBool b) {
			switch (bop.getType()) {
				case AND, AND_SYM -> {
					if (!b.value()) {
						return GioviBool.FALSE;
					}
				}
				case OR, OR_SYM -> {
					if (b.value()) {
						return GioviBool.TRUE;
					}
				}
			}
		}
		return VisitorUtils.bool(lhs, bop, rhsVisit.get());
	}


	public static GioviParserVisitResult equality(
		GioviParserVisitResult left,
		Token bop,
		GioviParserVisitResult right
	) {
		return switch (bop.getType()) {
			case EQUAL -> left.equal(right);
			case NOT_EQUAL -> left.notEqual(right);
			default -> throw new OperatorNotSupported(left, bop, right);
		};
	}

	public static GioviParserVisitResult containment(
		GioviParserVisitResult left,
		Token bop,
		GioviParserVisitResult right
	) {
		return switch (bop.getType()) {
			case TILDE_EQUAL, IN -> left.isContained(right);
			case EQUAL_TILDE -> left.contains(right);
			default -> throw new OperatorNotSupported(left, bop, right);
		};
	}

	public static GioviParserVisitResult redirection(
		GioviParserVisitResult content,
		Token bop,
		GioviParserVisitResult destination
	) {
		return switch (bop.getType()) {
			case REDIRECTION, REDIRECTION_RTL
				-> destination.redirection(content);
			case REDIRECTION_PLUS, REDIRECTION_PLUS_RTL
				-> destination.redirectionPlus(content);
			case REDIRECTION_MINUS, REDIRECTION_MINUS_RTL
				-> destination.redirectionMinus(content);
			case REDIRECTION_MULTIPLICATION, REDIRECTION_MULTIPLICATION_RTL
				-> destination.redirectionMultiply(content);
			case REDIRECTION_DIVISION, REDIRECTION_DIVISION_RTL
				-> destination.redirectionDivide(content);
			default -> throw new RedirectionException("Unknown redirection operator: " + bop);
		};
	}

	public static void redirectionToPrimitive(
		GioviParserVisitResult content,
		Token bop,
		String destination
	) {
		switch (bop.getType()) {
			case REDIRECTION, REDIRECTION_RTL -> {
				switch (destination) {
					case "_out" -> System.out.println(content);
					case "_err" -> System.err.println(content);
					default -> throw new GioviException(
						"Unkown primitive destination " + destination
					);
				}
			}
			case REDIRECTION_PLUS, REDIRECTION_PLUS_RTL -> {
				switch (destination) {
					case "_out" -> System.out.print(content);
					case "_err" -> System.err.print(content);
					default -> throw new GioviException(
						"Unkown primitive destination " + destination
					);
				}
			}
			default -> throw new RedirectionException(
				"Operator not supported for primitive destination: " + bop
			);
		}
	}

	// TODO: potential idea for refactoring: don't deconstruct imperatively; visit the patterns and
	// build a strategy, and then deconstruct at once with the built strategy. Similar to the
	// ForStatementDelegate

	public static void redirectionToPattern(
		GioviVisitor visitor,
		GioviParserVisitResult content,
		Token bop,
		RedirectionPatternContext destination
	) {
		switch (destination) {
			case RedirectionPatternPlaceholderContext _ -> {/* noop */}
			case RedirectionPatternProductContext pCtx
				-> deconstruct(visitor, visitor.getCurrentScope(), content, pCtx.productPattern());
			default -> throw new UnsupportedDeconstruction(content, destination);
		}
	}

	/**
	 * Deconstruct the given content (value) into a pattern
	 * @param visitor with which to perform the deconstruction
	 * @param targetScope
	 * @param content to deconstruct
	 * @param patternCtx
	 */
	public static void deconstruct(
		GioviVisitor visitor,
		Scope targetScope,
		GioviParserVisitResult content,
		PatternContext patternCtx
	) {
		switch (patternCtx) {
			// case PatternListContext pCtx -> deconstruct(visitor, content, pCtx.listPattern());
			// case PatternMapContext pCtx -> deconstruct(visitor, content, pCtx.mapPattern());
			case PatternProductContext pCtx
				-> deconstruct(visitor, targetScope, content, pCtx.productPattern());
			case PatternUnaryContext pCtx
				-> deconstruct(visitor, targetScope, content, pCtx.unaryPattern());
			case PatternPlaceholderContext _ -> {/* noop */}
			default -> throw new UnsupportedDeconstruction(content, patternCtx);
		}
	}

	/**
	 * Deconstruct the given content (value) into a unary pattern
	 * @param visitor with which to perform the deconstruction
	 * @param targetScope
	 * @param content to deconstruct
	 * @param patternCtx
	 */
	public static void deconstruct(
		GioviVisitor visitor,
		Scope targetScope,
		GioviParserVisitResult content,
		UnaryPatternContext patternCtx
	) {
		String identifier = patternCtx.identifier().getText();
		GioviValue<?> value;
		if (patternCtx.type() == null) {
			value = content.unwrapAsGioviValue().copy();
		} else {
			var type = visitor.visit(patternCtx.type()).asGioviType();
			try {
				value = type.widen(content.unwrapAsGioviValue());
			} catch (NonWideneable e) {
				throw new UnsupportedDeconstruction(content, patternCtx, e);
			}
		}
		targetScope.declare(identifier, new Var<>(value));
	}

	/**
	 * Deconstruct the given content (value) with a product pattern
	 * @param visitor
	 * @param targetScope
	 * @param content to deconstruct; must be a product value
	 * @param patternCtx
	 */
	public static void deconstruct(
		GioviVisitor visitor,
		Scope targetScope,
		GioviParserVisitResult content,
		ProductPatternContext patternCtx
	) {
		deconstruct(
			visitor,
			targetScope,
			content,
			Optional.ofNullable(patternCtx.whole),
			patternCtx.patternProductFields()
		);
	}

	/**
	 * Deconstruct the given content (value) with a product pattern
	 * @param visitor
	 * @param targetScope
	 * @param content to deconstruct; must be a product value
	 * @param optWholeCtx
	 * @param patternCtx
	 */
	public static void deconstruct(
		GioviVisitor visitor,
		Scope targetScope,
		GioviParserVisitResult content,
		Optional<UnaryPatternContext> optWholeCtx,
		PatternProductFieldsContext patternCtx
	) {
		var value = content.unwrapAsGioviValue();
		optWholeCtx.ifPresent(wholeCtx -> deconstruct(visitor, targetScope, value, wholeCtx));
		switch (patternCtx) {
			case PatternProductPositionalContext pCtx -> {
				switch (value) {
					case GioviStruct struct
						-> deconstructPositional(
							visitor,
							targetScope,
							struct.value(),
							pCtx.patternField()
						);
					case GioviRecord record
						-> deconstructPositional(
							visitor,
							targetScope,
							record.value(),
							pCtx.patternField()
						);
					case GioviTuple tuple
						-> deconstructPositional(
							visitor,
							targetScope,
							tuple.value(),
							pCtx.patternField()
						);
					default
						-> throw new UnsupportedDeconstruction(value, patternCtx);
				}
			}
			case PatternProductNominalContext nCtx -> {
				switch (value) {
					case GioviStruct struct
						-> deconstructNominal(
							visitor,
							targetScope,
							struct.value(),
							nCtx.patternFieldNominal()
						);
					case GioviRecord record
						-> deconstructNominal(
							visitor,
							targetScope,
							record.value(),
							nCtx.patternFieldNominal()
						);
					default
						-> throw new UnsupportedDeconstruction(value, patternCtx);
				}
			}
			default -> throw new UnsupportedDeconstruction(value, patternCtx);
		}
	}

	/**
	 * Deconstruct a record using nominal approach
	 * @param visitor
	 * @param targetScope
	 * @param record
	 * @param patternsCtx
	 */
	public static void deconstructNominal(
		GioviVisitor visitor,
		Scope targetScope,
		GRecord record,
		List<PatternFieldNominalContext> patternsCtx
	) {
		for (var patternCtx : patternsCtx) {
			switch (patternCtx) {
				case PatternFieldNominalExplicitContext explicitCtx -> {
					String fieldIdentifier = explicitCtx.name.getText();
					var field = record.getField(fieldIdentifier)
						.orElseThrow(
							() -> new UnsupportedDeconstruction(
								record,
								explicitCtx,
								new NoSuchField(fieldIdentifier)
							)
						);
					deconstruct(visitor, targetScope, field, explicitCtx.patternField());
				}
				case PatternFieldNominalImplicitContext implicitCtx -> {
					var unaryCtx = implicitCtx.unaryPattern();
					String fieldIdentifier = unaryCtx.identifier().getText();
					var field = record.getField(fieldIdentifier)
						.orElseThrow(
							() -> new UnsupportedDeconstruction(
								record,
								unaryCtx,
								new NoSuchField(fieldIdentifier)
							)
						);
					deconstruct(visitor, targetScope, field, unaryCtx);
				}
				default -> throw new UnsupportedDeconstruction(record, patternCtx);
			}
		}

	}

	/**
	 * Deconstruct a tuple using a positional approach
	 * @param visitor
	 * @param targetScope
	 * @param tuple
	 * @param patternsCtx
	 */
	public static void deconstructPositional(
		GioviVisitor visitor,
		Scope targetScope,
		GTuple tuple,
		List<PatternFieldContext> patternsCtx
	) {
		for (int i = 0; i < patternsCtx.size(); i++) {
			final int j = i;
			var field = tuple.getField(i)
				.orElseThrow(() -> new UnsupportedDeconstruction(tuple, new NoSuchField(j)));
			deconstruct(visitor, targetScope, field, patternsCtx.get(i));
		}
	}

	/**
	 * Deconstruct a product field
	 * @param visitor
	 * @param targetScope
	 * @param field
	 * @param patternCtx
	 */
	public static void deconstruct(
		GioviVisitor visitor,
		Scope targetScope,
		GField<?> field,
		PatternFieldContext patternCtx
	) {
		switch (patternCtx) {
			case PatternFieldPlaceholderContext _
				-> {/* noop */}
			case PatternFieldUnaryContext pCtx
				-> deconstruct(visitor, targetScope, field, pCtx.unaryPattern());
			case PatternFieldProductContext pCtx
				-> deconstruct(visitor, targetScope, field, pCtx.productPattern());
			default -> throw new UnsupportedDeconstruction(field, patternCtx);
		}
	}


}
