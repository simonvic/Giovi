package it.simonvic.giovi;

import java.util.Objects;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;

// TODO: what if we invert? Make GioviValues mutable and use immutable view when needed

// TODO: what if we implement GioviValue<T>
// TODO: we could do the same for constants (will allow to throw in redirections)
public class Var<T extends GioviValue<?>> implements ValueWrapper<T> {

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

	public Var<?> copy() {
		return new Var<>(value.copy());
	}

	@Deprecated
	public Var<?> widenTo(GioviType<?> gioviType) throws NonWideneable {
		return new Var<>(gioviType.widen(value));
	}

}
