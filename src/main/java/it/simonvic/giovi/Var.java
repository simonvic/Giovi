package it.simonvic.giovi;

import java.util.Objects;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.exceptions.RedirectionException;

// TODO: what if we invert? Make GioviValues mutable and use immutable view when needed

// TODO: what if we implement GioviValue<T>
// TODO: we could do the same for constants (will allow to throw in redirections)
public class Var<T extends GioviValue<?>> implements GioviParserVisitResult {

	public static <T> Var<? extends GioviValue<?>> of(T value) {
		return new Var<>(GioviValue.of(value));
	}

	protected T value;

	/**
	 * @param value
	 */
	public Var(T value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public T unwrap() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(T value) {
		this.value = value;
	}

	public void setValueUnsafe(GioviValue<?> value) {
		this.value = (T) value;
	}

	public void setValueWithWidening(GioviValue<?> value) throws NonWideneable {
		this.value = (T) value.widenTo(this.value.getGioviType());
	}

	// TODO: temporary shorthand. Eventually remove and handle manually the redirection/widening
	public void setValueWithRedirection(GioviParserVisitResult value) {
		// NOTE: if this.value is recursive, we can assume it is already initialized
		try {
			redirection(value);
		} catch (OperatorNotSupported ex) {
			throw new RedirectionException(ex);
		}
	}

	@Override
	public String toString() {
		return "" + value + "";
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Var<?> other = (Var<?>) obj;
		return Objects.equals(value, other.value);
	}

	public GioviType<?> getGioviType() {
		return value.getGioviType();
	}

	public Var<?> copy() {
		return new Var<>(value.copy());
	}

	@Deprecated
	public Var<?> widenTo(GioviType<?> gioviType) throws NonWideneable {
		return new Var<>(gioviType.widen(value));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviType<?> t -> value.as(t);
			default -> GioviParserVisitResult.super.as(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	// TODO: setting the value in a redirection is different than the GioviValue
	// implementation and might lead to a double setting of the value

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (T) this.value.redirection(content);
		return this.value;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// bitwise

	@Override
	public GioviParserVisitResult bitwiseNot() {
		return value.bitwiseNot();
	}

	@Override
	public GioviParserVisitResult bitwiseAnd(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.bitwiseAnd(v.unwrap());
			default -> value.bitwiseAnd(right);
		};
	}

	@Override
	public GioviParserVisitResult bitwiseOr(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.bitwiseOr(v.unwrap());
			default -> value.bitwiseOr(right);
		};
	}

	@Override
	public GioviParserVisitResult bitwiseXor(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.bitwiseXor(v.unwrap());
			default -> value.bitwiseXor(right);
		};
	}

	@Override
	public GioviParserVisitResult bitwiseLeftShift(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.bitwiseLeftShift(v.unwrap());
			default -> value.bitwiseLeftShift(right);
		};
	}

	@Override
	public GioviParserVisitResult bitwiseRightShift(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.bitwiseRightShift(v.unwrap());
			default -> value.bitwiseRightShift(right);
		};
	}

	@Override
	public GioviParserVisitResult bitwiseRightUnsignedShift(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.bitwiseRightUnsignedShift(v.unwrap());
			default -> value.bitwiseRightUnsignedShift(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult positive() {
		return value.positive();
	}

	@Override
	public GioviParserVisitResult negative() {
		return value.negative();
	}

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.plus(v.unwrap());
			default -> value.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.minus(v.unwrap());
			default -> value.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.multiply(v.unwrap());
			default -> value.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.divide(v.unwrap());
			default -> value.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.modulo(v.unwrap());
			default -> value.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.power(v.unwrap());
			default -> value.power(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	@Override
	public GioviParserVisitResult not() {
		return value.not();
	}

	@Override
	public GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.and(v.unwrap());
			default -> value.and(right);
		};
	}

	@Override
	public GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.or(v.unwrap());
			default -> value.or(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.equal(v.unwrap());
			default -> value.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.notEqual(v.unwrap());
			default -> value.notEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.less(v.unwrap());
			default -> value.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.lessEqual(v.unwrap());
			default -> value.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.greater(v.unwrap());
			default -> value.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.greaterEqual(v.unwrap());
			default -> value.greaterEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.contains(v.unwrap());
			default -> value.contains(right);
		};
	}

	@Override
	public GioviParserVisitResult isContained(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.isContained(v.unwrap());
			default -> value.isContained(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access
	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.access(v.unwrap());
			default -> value.access(right);
		};
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> value.dot(v.unwrap());
			default -> value.dot(right);
		};
	}


}
