package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public class TextValue extends Value<String> {

	public TextValue(String value) {
		super(value);
	}

	public TextValue() {
		super("");
	}

	@Override
	public void set(Value<?> value) {
		switch(value.get()) {
			case String v -> set(v);
			default -> super.set(value);
		}
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case TextValue v -> new TextValue(value + v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case TextValue v -> new TextValue(value.replace(v.get(),""));
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case TextValue v -> new BoolValue(value.compareTo(v.get()) <= 0);
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case TextValue v -> new BoolValue(value.compareTo(v.get()) >= 0);
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case TextValue v -> new BoolValue(value.compareTo(v.get()) > 0);
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case TextValue v -> new BoolValue(value.compareTo(v.get()) < 0);
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case TextValue v -> new BoolValue(value.compareTo(v.get()) == 0);
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject notEqual(GioviObject right) {
		return equal(right).not();
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return switch (right) {
			case TextValue v -> new BoolValue(value.contains(v.get()));
			default -> super.contains(right);
		};
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
		final TextValue other = (TextValue) obj;
		return this.value.equals(other.value);
	}

}
