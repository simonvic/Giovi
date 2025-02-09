package it.simonvic.giovi;

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
	 * @param value
	 */
	public VarD(T value) {
		this(value, (T) value.getGioviType().spawn());
		// TODO: ugly but needed in order to have proper default value for maps, lists and structs
	}

	/**
	 * @return the defaultValue
	 */
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
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		VarD<?> other = (VarD<?>) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null) return false;
		} else if (!defaultValue.equals(other.defaultValue)) return false;
		return true;
	}

	@Override
	public VarD<?> copy() {
		return new VarD<>(value.copy(), defaultValue.copy());
	}

	public VarD<?> widenTo(GioviType<?> gioviType, GioviValue<?> defaultValue)
		throws NonWideneable {
		return new VarD<>(gioviType.widen(value), gioviType.widen(defaultValue));
	}

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviType<?> t -> new VarD<>(
				value.as(t).asGioviValue(),
				defaultValue.as(t).asGioviValue());
			default -> super.as(right);
		};
	}

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirection(defaultValue);
			default -> super.redirection(content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirectionPlus(defaultValue);
			default -> super.redirectionPlus(content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirectionMinus(defaultValue);
			default -> super.redirectionMinus(content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirectionMultiply(defaultValue);
			default -> super.redirectionMultiply(content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> this.value.redirectionDivide(defaultValue);
			default -> super.redirectionDivide(content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return defaultValue;
		}
		return super.dot(right);
	}


}
