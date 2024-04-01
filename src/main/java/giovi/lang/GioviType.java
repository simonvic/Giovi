package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;

public abstract sealed class GioviType<T extends GioviValue<?>>
	extends GioviPrimitive<Class<T>>
	permits GioviTypePrimitive, GioviTypeList, GioviTypeMap, GioviTypeTuple, GioviTypeStruct,
	GioviTypeFunction {

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

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		if (type.equals(this)) {
			return this;
		}
		throw new NonWideneable(this, type);
	}

	public abstract boolean canWidenTo(GioviType<?> to);

	public abstract GioviValue<?> spawn();

	@Override
	public abstract GioviType<T> copy();

}
