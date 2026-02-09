package it.simonvic.giovi;

import java.util.Objects;

import giovi.GioviParserVisitResult;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.NonWideneable;

public class VarD<T extends GioviValue<?>> extends Var<T> {

	public static <T> VarD<? extends GioviValue<?>> of(T value) {
		return new VarD<>(GioviValue.of(value));
	}

	public static <T> VarD<? extends GioviValue<?>> of(T value, T defaultValue) {
		return new VarD<>(GioviValue.of(value), GioviValue.of(defaultValue));
	}

	private final T defaultValue; // TODO: eventually, it could be lazily initialized only if it is overriden

	/**
	 * @param value
	 * @param defaultValue
	 */
	public VarD(T value, T defaultValue) {
		super(value);
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
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(defaultValue);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		VarD<?> other = (VarD<?>) obj;
		return Objects.equals(defaultValue, other.defaultValue);
	}

	@Override
	public VarD<?> copy() {
		return new VarD<>(value.copy(), defaultValue.copy());
	}

	@Deprecated
	@Override
	public VarD<?> widenTo(GioviType<?> gioviType) throws NonWideneable {
		return new VarD<>(gioviType.widen(value), gioviType.widen(defaultValue));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.as(this.defaultValue);
			case GioviType<?> t -> this.value.as(t);
			default -> super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirection(this.defaultValue);
			default -> super.redirection(content);
		};
		return this.value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirectionPlus(this.defaultValue);
			default -> super.redirectionPlus(content);
		};
		return this.value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirectionMinus(this.defaultValue);
			default -> super.redirectionMinus(content);
		};
		return this.value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirectionMultiply(this.defaultValue);
			default -> super.redirectionMultiply(content);
		};
		return this.value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirectionDivide(this.defaultValue);
			default -> super.redirectionDivide(content);
		};
		return this.value;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.plus(this.defaultValue);
			default -> super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.minus(this.defaultValue);
			default -> super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.multiply(this.defaultValue);
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.divide(this.defaultValue);
			default -> super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.modulo(this.defaultValue);
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.power(this.defaultValue);
			default -> super.power(right);
		};
	}



	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	@Override
	public GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.and(this.defaultValue);
			default -> super.and(right);
		};
	}

	@Override
	public GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.or(this.defaultValue);
			default -> super.or(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.equal(this.defaultValue);
			default -> super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.notEqual(this.defaultValue);
			default -> super.notEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.less(this.defaultValue);
			default -> super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.lessEqual(this.defaultValue);
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.greater(this.defaultValue);
			default -> super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.greaterEqual(this.defaultValue);
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.contains(this.defaultValue);
			default -> super.contains(right);
		};
	}

	@Override
	public GioviParserVisitResult isContained(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.isContained(this.defaultValue);
			default -> super.isContained(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.access(this.defaultValue);
			default -> super.access(right);
		};
	}


	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.defaultValue;
			default -> super.dot(right);
		};
	}

}
