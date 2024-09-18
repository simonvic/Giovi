package giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;

@SuppressWarnings("unchecked")
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
	public T value() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public <U extends GioviValue<?>> void setValue(U value) throws ClassCastException {
		this.value = (T) value;
	}

	@Override
	public String toString() {
		return "Var {" + value + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Var<?> other = (Var<?>) obj;
		if (value == null) {
			if (other.value != null) return false;
		} else if (!value.equals(other.value)) return false;
		return true;
	}

	public GioviType<? extends GioviValue<?>> getGioviType() {
		return value.getGioviType();
	}

	public Var<?> copy() {
		return new Var<>(value.copy());
	}

	public Var<?> widenTo(GioviType<? extends GioviValue<?>> gioviType) throws NonWideneable {
		return new Var<>(value.widenTo(gioviType));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviType<?> t -> new Var<>(value.as(t).asGioviValue());
			default -> GioviParserVisitResult.super.as(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	// TODO: eventually remove redirection handling of default values from giovi values

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			// case GioviDefault _ -> this. value.redirection(defaultValue);
			default -> this.value.redirection(content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			// case GioviDefault _ -> this. value.redirectionPlus(defaultValue);
			default -> this.value.redirectionPlus(content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			// case GioviDefault _ -> this. value.redirectionMinus(defaultValue);
			default -> this.value.redirectionMinus(content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			// case GioviDefault _ -> this. value.redirectionMultiply(defaultValue);
			default -> this.value.redirectionMultiply(content);
		};
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult content) {
		this.value = (T) switch (content) {
			// case GioviDefault _ -> this. value.redirectionDivide(defaultValue);
			default -> this.value.redirectionDivide(content);
		};
		return this.value;
	}

	//@formatter:off
	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override public GioviParserVisitResult positive()                                 { return value.positive(); }
	@Override public GioviParserVisitResult negative()                                 { return value.negative(); }
	@Override public GioviParserVisitResult plus (GioviParserVisitResult right)        { return value.plus(right); }
	@Override public GioviParserVisitResult minus (GioviParserVisitResult right)       { return value.minus(right); }
	@Override public GioviParserVisitResult multiply(GioviParserVisitResult right)     { return value.multiply(right); }
	@Override public GioviParserVisitResult divide (GioviParserVisitResult right)      { return value.divide(right); }
	@Override public GioviParserVisitResult modulo (GioviParserVisitResult right)      { return value.modulo(right); }
	@Override public GioviParserVisitResult power (GioviParserVisitResult right)       { return value.power(right); }


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	@Override public GioviParserVisitResult not()                                      { return value.not(); }
	@Override public GioviParserVisitResult and(GioviParserVisitResult right)          { return value.and(right); }
	@Override public GioviParserVisitResult or(GioviParserVisitResult right)           { return value.or(right); }

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override public GioviParserVisitResult equal(GioviParserVisitResult right)        { return value.equal(right); }
	@Override public GioviParserVisitResult notEqual(GioviParserVisitResult right)     { return value.notEqual(right); }
	@Override public GioviParserVisitResult less(GioviParserVisitResult right)         { return value.less(right); }
	@Override public GioviParserVisitResult lessEqual(GioviParserVisitResult right)    { return value.lessEqual(right); }
	@Override public GioviParserVisitResult greater(GioviParserVisitResult right)      { return value.greater(right); }
	@Override public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) { return value.greaterEqual(right); }
	@Override public GioviParserVisitResult contains(GioviParserVisitResult right)     { return value.contains(right); }
	@Override public GioviParserVisitResult isContained(GioviParserVisitResult right)  { return value.isContained(right); }

	//////////////////////////////////////////////////////////////////////////////////////////////// access
	@Override public GioviParserVisitResult access(GioviParserVisitResult right)       { return value.access(right); }
	@Override public GioviParserVisitResult dot(GioviParserVisitResult right)          { return value.dot(right); }
	//@formatter:on



}
