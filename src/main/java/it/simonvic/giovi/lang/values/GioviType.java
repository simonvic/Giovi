package it.simonvic.giovi.lang.values;

public abstract sealed class GioviType<T extends GioviValue<?>>
	extends GioviPrimitive<Class<T>>
	permits GioviTypePrimitive, GioviTypeList, GioviTypeMap, GioviTypeTuple, GioviTypeStruct {

	public GioviType(Class<T> value, Class<T> defaultValue) {
		super(value, defaultValue);
	}

	public GioviType(Class<T> value) {
		super(value, value);
	}

	public String getFullyQualifiedName() {
		return value.getSimpleName().replace("Giovi", "giovi.");
	}

	public String getSimpleName() {
		return value.getSimpleName().replace("Giovi", "");
	}

	@Override
	public String toString() {
		return value.getSimpleName();
	}

	@Override
	public GioviType<?> getGioviType() {
		return null; // TODO: GioviTypeType
	}

	public abstract boolean canUpcast(GioviType<?> to);

	public abstract GioviValue<?> spawn();

	@Override
	public abstract GioviType<T> copy();

}
