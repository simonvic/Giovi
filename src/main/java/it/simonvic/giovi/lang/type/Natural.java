package it.simonvic.giovi.lang.type;

import it.simonvic.giovi.exceptions.GioviException;

public final class Natural extends Number {

	private final Integer value;

	public Natural(Integer value) {
		if (value <= 0) throw new GioviException("Value out of bound");
		this.value = value;
	}

	public Natural() {
		this.value = 1;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Natural other = (Natural) obj;
		return this.value.equals(other.value);
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

	@Override
	public String toString() {
		return value.toString();
	}
}
