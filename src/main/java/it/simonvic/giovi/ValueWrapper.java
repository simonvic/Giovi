package it.simonvic.giovi;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;

// TODO: what if we implement GioviValue<J>
public sealed interface ValueWrapper<G extends GioviValue<?>>
	extends GioviParserVisitResult
	permits Const, MutableValueWrapper {

	/**
	 * @return the value
	 */
	G unwrap();

	ValueWrapper<?> copy();

	void setValue(G value);

	default GioviType<?> getGioviType() {
		return unwrap().getGioviType();
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	default GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviType<?> t -> unwrap().as(t);
			default -> GioviParserVisitResult.super.as(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// bitwise

	@Override
	default GioviParserVisitResult bitwiseNot() {
		return unwrap().bitwiseNot();
	}

	@Override
	default GioviParserVisitResult bitwiseAnd(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().bitwiseAnd(v.unwrap());
			default -> unwrap().bitwiseAnd(right);
		};
	}

	@Override
	default GioviParserVisitResult bitwiseOr(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().bitwiseOr(v.unwrap());
			default -> unwrap().bitwiseOr(right);
		};
	}

	@Override
	default GioviParserVisitResult bitwiseXor(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().bitwiseXor(v.unwrap());
			default -> unwrap().bitwiseXor(right);
		};
	}

	@Override
	default GioviParserVisitResult bitwiseLeftShift(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().bitwiseLeftShift(v.unwrap());
			default -> unwrap().bitwiseLeftShift(right);
		};
	}

	@Override
	default GioviParserVisitResult bitwiseRightShift(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().bitwiseRightShift(v.unwrap());
			default -> unwrap().bitwiseRightShift(right);
		};
	}

	@Override
	default GioviParserVisitResult bitwiseRightUnsignedShift(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().bitwiseRightUnsignedShift(v.unwrap());
			default -> unwrap().bitwiseRightUnsignedShift(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	default GioviParserVisitResult positive() {
		return unwrap().positive();
	}

	@Override
	default GioviParserVisitResult negative() {
		return unwrap().negative();
	}

	@Override
	default GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().plus(v.unwrap());
			default -> unwrap().plus(right);
		};
	}

	@Override
	default GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().minus(v.unwrap());
			default -> unwrap().minus(right);
		};
	}

	@Override
	default GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().multiply(v.unwrap());
			default -> unwrap().multiply(right);
		};
	}

	@Override
	default GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().divide(v.unwrap());
			default -> unwrap().divide(right);
		};
	}

	@Override
	default GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().modulo(v.unwrap());
			default -> unwrap().modulo(right);
		};
	}

	@Override
	default GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().power(v.unwrap());
			default -> unwrap().power(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	@Override
	default GioviParserVisitResult not() {
		return unwrap().not();
	}

	@Override
	default GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().and(v.unwrap());
			default -> unwrap().and(right);
		};
	}

	@Override
	default GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().or(v.unwrap());
			default -> unwrap().or(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	default GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().equal(v.unwrap());
			default -> unwrap().equal(right);
		};
	}

	@Override
	default GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().notEqual(v.unwrap());
			default -> unwrap().notEqual(right);
		};
	}

	@Override
	default GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().less(v.unwrap());
			default -> unwrap().less(right);
		};
	}

	@Override
	default GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().lessEqual(v.unwrap());
			default -> unwrap().lessEqual(right);
		};
	}

	@Override
	default GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().greater(v.unwrap());
			default -> unwrap().greater(right);
		};
	}

	@Override
	default GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().greaterEqual(v.unwrap());
			default -> unwrap().greaterEqual(right);
		};
	}

	@Override
	default GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().contains(v.unwrap());
			default -> unwrap().contains(right);
		};
	}

	@Override
	default GioviParserVisitResult isContained(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().isContained(v.unwrap());
			default -> unwrap().isContained(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	default GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> unwrap().dot(v.unwrap());
			default -> unwrap().dot(right);
		};
	}


}

