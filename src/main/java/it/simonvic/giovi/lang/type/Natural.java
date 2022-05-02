package it.simonvic.giovi.lang.type;

public final class Natural extends Number {

	private final Integer value;

	public Natural(Integer value) {
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
}
