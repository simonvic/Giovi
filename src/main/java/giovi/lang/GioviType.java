package giovi.lang;

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
		return value.getName().replace("Giovi", "");
	}

	public String getSimpleName() {
		return value.getSimpleName().replace("Giovi", "");
	}

	@Override
	public String toString() {
		return getSimpleName();
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypeType();
	}

	public abstract boolean canWidenTo(GioviType<?> to);

	public abstract GioviValue<?> spawn();

	@Override
	public abstract GioviType<T> copy();

}
