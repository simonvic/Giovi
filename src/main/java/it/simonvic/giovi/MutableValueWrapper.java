package it.simonvic.giovi;

import java.util.Objects;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.exceptions.RedirectionException;

// TODO: what if we invert? Make GioviValues mutable and use immutable view when needed
public abstract sealed class MutableValueWrapper<G extends GioviValue<?>>
	implements ValueWrapper<G>
	permits Entry, DefaultableValueWrapper {

	protected G value;

	public MutableValueWrapper(G value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
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
		var other = (MutableValueWrapper<?>) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public G unwrap() {
		return value;
	}

	@Override
	public void setValue(G value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public void setValueUnsafe(GioviValue<?> value) {
		setValue((G) value);
	}

	@SuppressWarnings("unchecked")
	public void setValueWithWidening(GioviValue<?> value) throws NonWideneable {
		setValue((G) value.widenTo(getGioviType()));
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

	// TODO: setting the value in a redirection is different than the GioviValue
	// implementation and might lead to a double setting of the value

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (G) this.value.redirection(content);
		return this.value;
	}

}
