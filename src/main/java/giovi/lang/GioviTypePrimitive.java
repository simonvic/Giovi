package giovi.lang;

public abstract sealed class GioviTypePrimitive<T extends GioviPrimitive<?>>
	extends GioviType<T>
	permits GioviTypeVoid, GioviTypeBool, GioviTypeNumber, GioviTypeText, GioviTypeType {

	public GioviTypePrimitive(Class<T> value, Class<T> defaultValue) {
		super(value, defaultValue);
	}

	public GioviTypePrimitive(Class<T> value) {
		super(value);
	}

}

