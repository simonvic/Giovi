package it.simonvic.giovi;

public class Variable<T> {

	private final String identifier;
	private T value;

	public Variable(String identifier) {
		this.identifier = identifier;
	}

	public Variable(String identifier, T value) {
		this.identifier = identifier;
		this.value = value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
