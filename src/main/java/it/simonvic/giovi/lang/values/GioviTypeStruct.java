package it.simonvic.giovi.lang.values;

public final class GioviTypeStruct extends GioviType<String> {

	public GioviTypeStruct(String value, String defaultValue) {
		super(value, defaultValue);
	}

	public GioviTypeStruct(String value) {
		super(value, value);
	}

	@Override
	public String toString() {
		return "giovi." + value;
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypePrimitive(GioviTypeStruct.class);
	}

	@Override
	public GioviTypeStruct copy() {
		return new GioviTypeStruct(this.value, this.defaultValue);
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStruct t -> {
				yield false; // TODO: stub
			}
			case GioviTypeList t -> false;
			case GioviTypeMap t -> false;
			case GioviTypePrimitive t -> false;
		};
	}

}
