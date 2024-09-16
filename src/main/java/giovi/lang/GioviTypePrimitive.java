package giovi.lang;

public sealed interface GioviTypePrimitive<T extends GioviPrimitive<?>>
	extends GioviType<T>
	permits GioviTypeVoid, GioviTypeBool, GioviTypeNumber, GioviTypeText, GioviTypeType {

}

