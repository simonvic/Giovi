package it.simonvic.giovi.lang.values.natives;

import java.util.Objects;

import it.simonvic.giovi.exceptions.GioviException;

public final class Natural extends Number {

	public static Natural valueOf(String s) throws NumberFormatException {
		return new Natural(Integer.decode(s.replace("n", "")));
	}

	private final Integer value;

	public Natural(Integer value) {
		if (value < 0)
			throw new GioviException("Value out of bound");
		this.value = value;
	}

	public Natural() {
		this.value = 0;
	}

	@Override
	public String toString() {
		return value.toString();
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
		Natural other = (Natural) obj;
		return Objects.equals(this.value, other.value);
	}

	@Override
	public int intValue() {
		return value;
	}

	@Override
	public long longValue() {
		return value.longValue();
	}

	@Override
	public float floatValue() {
		return value.floatValue();
	}

	@Override
	public double doubleValue() {
		return value.doubleValue();
	}

	public Natural plus(Number number) {
		return new Natural(value + number.intValue());
	}

	public Natural minus(Number number) {
		return new Natural(value - number.intValue());
	}

	public Natural multiply(Number number) {
		return new Natural(value * number.intValue());
	}

	public Natural divide(Number number) {
		return new Natural(value / number.intValue());
	}

}
