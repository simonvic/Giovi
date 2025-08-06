package it.simonvic.giovi;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviBool;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;

// TODO: what if we invert? Make GioviValues mutable and use immutable view when needed

// TODO: relax upper bound to GioviParserVisitResult? (In case we want to handle ranges and such)
public class Var<T extends GioviValue<?>> implements Op.All {

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
	public void setValue(T value) {
		this.value = value;
	}

	// TODO: temporary shorthand. Eventually remove and handle manually the redirection
	// TODO: or use safe generic getField
	public void setValueWithRedirection(GioviParserVisitResult value) {
		redirection(value);
	}

	@Override
	public String toString() {
		return "" + value + "";
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
		return Op.as(this.value, right);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	// TODO: eventually remove redirection handling of default values from giovi values

	// TODO: setting the value in a redirection is different than the GioviValue
	// implementation and might lead to a double setting of the value

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (T) Op.redirection(this.value, content);
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) {
		this.value = (T) Op.redirectionPlus(this.value, content);
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) {
		this.value = (T) Op.redirectionMinus(this.value, content);
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult content) {
		this.value = (T) Op.redirectionMultiply(this.value, content);
		return this.value;
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult content) {
		this.value = (T) Op.redirectionDivide(this.value, content);
		return this.value;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult positive() {
		return Op.positive(this.value);
	}

	@Override
	public GioviParserVisitResult negative() {
		return Op.negative(this.value);
	}

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return Op.plus(this.value, right);
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return Op.minus(this.value, right);
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return Op.multiply(this.value, right);
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return Op.divide(this.value, right);
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return Op.modulo(this.value, right);
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return Op.power(this.value, right);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	@Override
	public GioviParserVisitResult not() {
		return Op.not(this.value);
	}

	@Override
	public GioviParserVisitResult and(GioviParserVisitResult right) {
		return Op.and(this.value, right);
	}

	@Override
	public GioviParserVisitResult or(GioviParserVisitResult right) {
		return Op.or(this.value, right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviBool equal(GioviParserVisitResult right) {
		return Op.equal(this.value, right);
	}

	@Override
	public GioviBool notEqual(GioviParserVisitResult right) {
		return Op.notEqual(this.value, right);
	}

	@Override
	public GioviParserVisitResult less(GioviParserVisitResult right) {
		return Op.less(this.value, right);
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return Op.lessEqual(this.value, right);
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return Op.greater(this.value, right);
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return Op.greaterEqual(this.value, right);
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return Op.contains(this.value, right);
	}

	@Override
	public GioviParserVisitResult isContained(GioviParserVisitResult right) {
		return Op.isContained(this.value, right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access
	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return Op.access(this.value, right);
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return Op.dot(this.value, right);
	}


}
