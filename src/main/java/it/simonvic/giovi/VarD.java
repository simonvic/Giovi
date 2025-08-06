package it.simonvic.giovi;

import java.util.Objects;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviDefault;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;
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
	public VarD(T value) {
		this(value, (T) value.getGioviType().spawn());
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

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return Op.as(this.value, right);
	}

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> Op.redirection(this.value, defaultValue);
			default -> Op.redirection(this.value, content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> Op.redirectionPlus(this.value, defaultValue);
			default -> Op.redirectionPlus(this.value, content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> Op.redirectionMinus(this.value, defaultValue);
			default -> Op.redirectionMinus(this.value, content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> Op.redirectionMultiply(this.value, defaultValue);
			default -> Op.redirectionMultiply(this.value, content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			case GioviDefault _ -> Op.redirectionDivide(this.value, defaultValue);
			default -> Op.redirectionDivide(this.value, content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return defaultValue;
		}
		return Op.dot(this.value, right);
	}


}
