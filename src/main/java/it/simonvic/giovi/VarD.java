package it.simonvic.giovi;

import java.util.Objects;

import giovi.GioviParserVisitResult;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.NonWideneable;

// TODO: extend with Member, FunctionParam?
public class VarD<T extends GioviValue<?>> implements ValueWrapper<T> {

	public static <T> VarD<? extends GioviValue<?>> of(T value) {
		return new VarD<>(GioviValue.of(value));
	}

	public static <T> VarD<? extends GioviValue<?>> of(T value, T defaultValue) {
		return new VarD<>(GioviValue.of(value), GioviValue.of(defaultValue));
	}

	private T value;
	private final T defaultValue; // TODO: eventually, it could be lazily initialized only if it is overriden

	/**
	 * @param value
	 * @param defaultValue
	 */
	public VarD(T value, T defaultValue) {
		this.value = value;
		this.defaultValue = defaultValue;
	}

	/**
	 * The default value will be set to its type's default value
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public VarD(T value) {
		this(value, (T) value.getGioviType().spawn());
	}


	@Override
	public T unwrap() {
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * @return the defaultValue
	 */
	@SuppressWarnings("unchecked")
	public T defaultValue() {
		return (T) defaultValue.copy(); // return copy to ensure immutability
	}

	@Override
	public String toString() {
		return value.toString();
		// return "" + value + " default " + defaultValue + "";
		// return "Var [value=" + value + ", defaultValue=" + defaultValue + ", value()=" + value()
		// 	+ ", defaultValue()=" + defaultValue() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, defaultValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		VarD<?> other = (VarD<?>) obj;
		return Objects.equals(value, other.value)
			&& Objects.equals(defaultValue, other.defaultValue);
	}

	public VarD<?> copy() {
		return new VarD<>(value.copy(), defaultValue.copy());
	}

	@Deprecated
	public VarD<?> widenTo(GioviType<?> gioviType) throws NonWideneable {
		return new VarD<>(gioviType.widen(value), gioviType.widen(defaultValue));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS


	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviType<?> t -> value.as(t);
			default -> ValueWrapper.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirection(this.defaultValue);
			default -> this.value.redirection(content);
		};
		return this.value;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// bitwise

	@Override
	public GioviParserVisitResult bitwiseAnd(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseAnd(this.defaultValue);
			default -> ValueWrapper.super.bitwiseAnd(right);
		};
	}


	@Override
	public GioviParserVisitResult bitwiseOr(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseOr(this.defaultValue);
			default -> ValueWrapper.super.bitwiseOr(right);
		};
	}


	@Override
	public GioviParserVisitResult bitwiseXor(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseXor(this.defaultValue);
			default -> ValueWrapper.super.bitwiseXor(right);
		};
	}


	@Override
	public GioviParserVisitResult bitwiseLeftShift(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseLeftShift(this.defaultValue);
			default -> ValueWrapper.super.bitwiseLeftShift(right);
		};
	}


	@Override
	public GioviParserVisitResult bitwiseRightShift(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseRightShift(this.defaultValue);
			default -> ValueWrapper.super.bitwiseRightShift(right);
		};
	}


	@Override
	public GioviParserVisitResult bitwiseRightUnsignedShift(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseRightUnsignedShift(this.defaultValue);
			default -> ValueWrapper.super.bitwiseRightUnsignedShift(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.plus(this.defaultValue);
			default -> ValueWrapper.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.minus(this.defaultValue);
			default -> ValueWrapper.super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.multiply(this.defaultValue);
			default -> ValueWrapper.super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.divide(this.defaultValue);
			default -> ValueWrapper.super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.modulo(this.defaultValue);
			default -> ValueWrapper.super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.power(this.defaultValue);
			default -> ValueWrapper.super.power(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	@Override
	public GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.and(this.defaultValue);
			default -> ValueWrapper.super.and(right);
		};
	}

	@Override
	public GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.or(this.defaultValue);
			default -> ValueWrapper.super.or(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.equal(this.defaultValue);
			default -> ValueWrapper.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.notEqual(this.defaultValue);
			default -> ValueWrapper.super.notEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.less(this.defaultValue);
			default -> ValueWrapper.super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.lessEqual(this.defaultValue);
			default -> ValueWrapper.super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.greater(this.defaultValue);
			default -> ValueWrapper.super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.greaterEqual(this.defaultValue);
			default -> ValueWrapper.super.greaterEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.contains(this.defaultValue);
			default -> ValueWrapper.super.contains(right);
		};
	}

	@Override
	public GioviParserVisitResult isContained(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.isContained(this.defaultValue);
			default -> ValueWrapper.super.isContained(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.defaultValue;
			default -> ValueWrapper.super.dot(right);
		};
	}

}
