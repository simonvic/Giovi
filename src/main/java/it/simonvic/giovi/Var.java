package it.simonvic.giovi;

import java.util.Objects;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;

// TODO: what if we invert? Make GioviValues mutable and use immutable view when needed

// TODO: what if we implement GioviValue<T>
// TODO: we could do the same for constants (will allow to throw in redirections)
public abstract class Var<T extends GioviValue<?>> implements ValueWrapper<T> {

	protected T value;

	/**
	 * @param value
	 */
	public Var(T value) {
		this.value = value;
	}

	@Override
	public T unwrap() {
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value = value;
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

	public abstract Var<?> copy();

	@Deprecated
	public abstract Var<?> widenTo(GioviType<?> gioviType) throws NonWideneable;

	// TODO: setting the value in a redirection is different than the GioviValue
	// implementation and might lead to a double setting of the value

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (T) this.value.redirection(content);
		return this.value;
	}


}
