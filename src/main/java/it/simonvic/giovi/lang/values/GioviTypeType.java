package it.simonvic.giovi.lang.values;

public final class GioviTypeType extends GioviTypePrimitive<GioviType<?>> {

	public GioviTypeType() {
		super(null);
	}

	@Override
	public String getFullyQualifiedName() {
		return "giovi.Type";
	}

	@Override
	public String getSimpleName() {
		return "Type";
	}

	@Override
	public String toString() {
		return "Type";
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		return to instanceof GioviTypeType;
	}

	@Override
	public GioviValue<?> spawn() {
		throw new UnsupportedOperationException("Can't spawn a GioviType");
	}

	@Override
	public GioviTypeType copy() {
		return new GioviTypeType();
	}

}

