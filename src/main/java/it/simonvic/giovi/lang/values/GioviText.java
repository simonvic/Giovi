package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;

public non-sealed class GioviText extends GioviValue<String> {

	public GioviText(String value) {
		super(value);
	}

	public GioviText() {
		super("");
	}

	@Override
	public void set(GioviValue<?> value) {
		switch (value.get()) {
			case String v -> set(v);
			default -> super.set(value);
		}
	}

	@Override
	public GioviObject plus(GioviObject right) {
		return switch (right) {
			case GioviText v -> new GioviText(value + v.get());
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject minus(GioviObject right) {
		return switch (right) {
			case GioviText v -> new GioviText(value.replace(v.get(), ""));
			default -> super.plus(right);
		};
	}

	@Override
	public GioviObject lessEqual(GioviObject right) {
		return switch (right) {
			case GioviText v -> new GioviBool(value.compareTo(v.get()) <= 0);
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviObject greaterEqual(GioviObject right) {
		return switch (right) {
			case GioviText v -> new GioviBool(value.compareTo(v.get()) >= 0);
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviObject greater(GioviObject right) {
		return switch (right) {
			case GioviText v -> new GioviBool(value.compareTo(v.get()) > 0);
			default -> super.greater(right);
		};
	}

	@Override
	public GioviObject less(GioviObject right) {
		return switch (right) {
			case GioviText v -> new GioviBool(value.compareTo(v.get()) < 0);
			default -> super.less(right);
		};
	}

	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviText v -> new GioviBool(value.compareTo(v.get()) == 0);
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
			case GioviText v -> new GioviBool(value.contains(v.get()));
			default -> super.contains(right);
		};
	}

}
