package it.simonvic.giovi.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import giovi.GioviParserVisitResult;
import giovi.Record;
import giovi.Tuple;
import giovi.lang.*;
import it.simonvic.giovi.GioviToken;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.exceptions.*;

public class VisitorUtils {

	public static GioviParserVisitResult bitwiseNot(GioviParserVisitResult value) {
		return value.bitwiseNot();
	}

	public static GioviParserVisitResult bitwiseBool(
		GioviParserVisitResult lhs,
		GioviToken bop,
		GioviParserVisitResult rhs
	) {
		return switch (bop) {
			case BIT_AND -> lhs.bitwiseAnd(rhs);
			case BIT_OR -> lhs.bitwiseOr(rhs);
			case BIT_XOR -> lhs.bitwiseXor(rhs);
			default -> throw new OperatorNotSupported(lhs, bop, rhs);
		};
	}

	public static GioviParserVisitResult bitwiseShift(
		GioviParserVisitResult lhs,
		GioviToken bop,
		GioviParserVisitResult rhs
	) {
		return switch (bop) {
			case BIT_LSHIFT -> lhs.bitwiseLeftShift(rhs);
			case BIT_RSHIFT -> lhs.bitwiseRightShift(rhs);
			case BIT_RUSHIFT -> lhs.bitwiseRightUnsignedShift(rhs);
			default -> throw new OperatorNotSupported(lhs, bop, rhs);
		};
	}

	public static GioviParserVisitResult prefixed(GioviToken prefix, GioviParserVisitResult value) {
		return switch (prefix) {
			case PLUS -> value.positive();
			case MINUS -> value.negative();
			default -> throw new OperatorNotSupported(value, prefix);
		};
	}

	public static GioviParserVisitResult additive(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case PLUS -> left.plus(right);
			case MINUS -> left.minus(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult multiplicative(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case MULT -> left.multiply(right);
			case DIV -> left.divide(right);
			case MODULO -> left.modulo(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult relational(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case LESS_EQUAL -> left.lessEqual(right);
			case GREATER_EQUAL -> left.greaterEqual(right);
			case GREATER -> left.greater(right);
			case LESS -> left.less(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult bool(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case AND, AND_SYM -> left.and(right);
			case OR, OR_SYM -> left.or(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	@SuppressWarnings("incomplete-switch")
	public static GioviParserVisitResult bool(
		GioviParserVisitResult lhs,
		GioviToken operator,
		Supplier<GioviParserVisitResult> rhsVisit
	) {
		if (lhs instanceof GioviBool b) {
			switch (operator) {
				case AND, AND_SYM -> {
					if (!b.value()) {
						return new GioviBool(false);
					}
				}
				case OR, OR_SYM -> {
					if (b.value()) {
						return new GioviBool(true);
					}
				}
			}
		}
		return VisitorUtils.bool(lhs, operator, rhsVisit.get());
	}


	public static GioviParserVisitResult equality(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case EQUAL -> left.equal(right);
			case NOT_EQUAL -> left.notEqual(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult containment(
		GioviParserVisitResult left,
		GioviToken operator,
		GioviParserVisitResult right
	) {
		return switch (operator) {
			case CONTAINED, IN -> left.isContained(right);
			case CONTAINS -> left.contains(right);
			default -> throw new OperatorNotSupported(left, operator, right);
		};
	}

	public static GioviParserVisitResult redirection(
		GioviParserVisitResult content,
		GioviToken operator,
		GioviParserVisitResult destination
	) {
		// TODO: if destination is constant (not a Var) throw error
		return switch (operator) {
			case REDIR, REDIR_RTL -> destination.redirection(content);
			case REDIR_PLUS, REDIR_PLUS_RTL -> destination.redirectionPlus(content);
			case REDIR_MINUS, REDIR_MINUS_RTL -> destination.redirectionMinus(content);
			case REDIR_MULT, REDIR_MULT_RTL -> destination.redirectionMultiply(content);
			case REDIR_DIV, REDIR_DIV_RTL -> destination.redirectionDivide(content);
			default -> throw new RedirectionException("Unknown redirection operator: " + operator);
		};
	}

	public static void redirectionToPrimitive(
		GioviParserVisitResult content,
		GioviToken operator,
		String destination
	) {
		switch (operator) {
			case REDIR, REDIR_RTL -> {
				switch (destination) {
					case "_out" -> System.out.println(content);
					case "_err" -> System.err.println(content);
					default -> throw new GioviException(
						"Unkown primitive destination " + destination
					);
				}
			}
			case REDIR_PLUS, REDIR_PLUS_RTL -> {
				switch (destination) {
					case "_out" -> System.out.print(content);
					case "_err" -> System.err.print(content);
					default -> throw new GioviException(
						"Unkown primitive destination " + destination
					);
				}
			}
			default -> throw new RedirectionException(
				"Operator not supported for primitive destination: " + operator
			);
		}
	}

	public static void redirectionToPattern(
		GioviVisitor visitor,
		GioviParserVisitResult content,
		GioviToken operator,
		RedirectionPatternContext destination
	) {
		switch (destination) {
			case RedirectionPatternPlaceholderContext _ -> {/* noop */}
			case RedirectionPatternProductContext pCtx
				-> deconstruct(visitor, content, pCtx.productPattern());
			default -> throw new UnsupportedDeconstruction(content, destination);
		}
	}

	/**
	 * Deconstruct the given content (value) into a pattern
	 * @param visitor with which to perform the deconstruction
	 * @param content to deconstruct
	 * @param patternCtx
	 */
	public static void deconstruct(
		GioviVisitor visitor,
		GioviParserVisitResult content,
		PatternContext patternCtx
	) {
		switch (patternCtx) {
			// case PatternListContext pCtx -> deconstruct(visitor, content, pCtx.listPattern());
			// case PatternMapContext pCtx -> deconstruct(visitor, content, pCtx.mapPattern());
			case PatternProductContext pCtx -> deconstruct(visitor, content, pCtx.productPattern());
			case PatternUnaryContext pCtx -> deconstruct(visitor, content, pCtx.unaryPattern());
			case PatternPlaceholderContext _ -> {/* noop */}
			default -> throw new UnsupportedDeconstruction(content, patternCtx);
		}
	}

	/**
	 * Deconstruct the given content (value) into a unary pattern
	 * @param visitor with which to perform the deconstruction
	 * @param content to deconstruct
	 * @param patternCtx
	 */
	public static void deconstruct(
		GioviVisitor visitor,
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
		visitor.getCurrentScope().declare(identifier, new VarD<>(value));
	}

	/**
	 * Deconstruct the given content (value) with a product pattern
	 * @param visitor
	 * @param content to deconstruct; must be a product value
	 * @param patternCtx
	 */
	public static void deconstruct(
		GioviVisitor visitor,
		GioviParserVisitResult content,
		ProductPatternContext patternCtx
	) {
		deconstruct(
			visitor,
			content,
			Optional.ofNullable(patternCtx.whole),
			patternCtx.patternProductMembers()
		);
	}

	/**
	 * Deconstruct the given content (value) with a product pattern
	 * @param visitor
	 * @param content to deconstruct; must be a product value
	 * @param optWholeCtx
	 * @param patternCtx
	 */
	public static void deconstruct(
		GioviVisitor visitor,
		GioviParserVisitResult content,
		Optional<UnaryPatternContext> optWholeCtx,
		PatternProductMembersContext patternCtx
	) {
		var value = content.unwrapAsGioviValue();
		optWholeCtx.ifPresent(wholeCtx -> deconstruct(visitor, value, wholeCtx));
		// TODO(opsem): how should the members be deconstructed?
		// From the whole (possibly widened/narrowed from the deconstruction)?
		// Or from the original value?
		switch (patternCtx) {
			case PatternProductPositionalContext pCtx -> {
				switch (value) {
					case GioviStruct struct
						-> deconstructPositional(visitor, struct.value(), pCtx.patternMember());
					case GioviRecord record
						-> deconstructPositional(visitor, record.value(), pCtx.patternMember());
					case GioviTuple tuple
						-> deconstructPositional(visitor, tuple.value(), pCtx.patternMember());
					default
						-> throw new UnsupportedDeconstruction(value, patternCtx);
				}
			}
			case PatternProductNamedContext nCtx -> {
				switch (value) {
					case GioviStruct struct
						-> deconstructNamed(visitor, struct.value(), nCtx.patternMemberNamed());
					case GioviRecord record
						-> deconstructNamed(visitor, record.value(), nCtx.patternMemberNamed());
					default
						-> throw new UnsupportedDeconstruction(value, patternCtx);
				}
			}
			default -> throw new UnsupportedDeconstruction(value, patternCtx);
		}
	}

	/**
	 * Deconstruct a record using named approach
	 * @param visitor
	 * @param record
	 * @param patternsCtx
	 */
	public static void deconstructNamed(
		GioviVisitor visitor,
		Record record,
		List<PatternMemberNamedContext> patternsCtx
	) {
		for (var patternCtx : patternsCtx) {
			switch (patternCtx) {
				case PatternMemberNamedExplicitContext explicitCtx -> {
					String fieldIdentifier = explicitCtx.name.getText();
					var field = record.getField(fieldIdentifier)
						.orElseThrow(
							() -> new UnsupportedDeconstruction(
								record,
								explicitCtx,
								new NoSuchField(fieldIdentifier)
							)
						);
					deconstruct(visitor, field, explicitCtx.patternMember());
				}
				case PatternMemberNamedImplicitContext implicitCtx -> {
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
					deconstruct(visitor, field, unaryCtx);
				}
				default -> throw new UnsupportedDeconstruction(record, patternCtx);
			}
		}

	}

	/**
	 * Deconstruct a tuple using a positional approach
	 * @param visitor
	 * @param tuple
	 * @param patternsCtx
	 */
	public static void deconstructPositional(
		GioviVisitor visitor,
		Tuple tuple,
		List<PatternMemberContext> patternsCtx
	) {
		for (int i = 0; i < patternsCtx.size(); i++) {
			final int j = i;
			var field = tuple.getField(i)
				.orElseThrow(() -> new UnsupportedDeconstruction(tuple, new NoSuchField(j)));
			deconstruct(visitor, field, patternsCtx.get(i));
		}
	}

	/**
	 * Deconstruct a product member
	 * @param visitor
	 * @param field
	 * @param patternCtx
	 */
	public static void deconstruct(
		GioviVisitor visitor,
		VarD<?> field,
		PatternMemberContext patternCtx
	) {
		switch (patternCtx) {
			case PatternMemberPlaceholderContext _
				-> {/* noop */}
			case PatternMemberUnaryContext pCtx
				-> deconstruct(visitor, field, pCtx.unaryPattern());
			case PatternMemberProductContext pCtx
				-> deconstruct(visitor, field, pCtx.productPattern());
			default -> throw new UnsupportedDeconstruction(field, patternCtx);
		}
	}


}
