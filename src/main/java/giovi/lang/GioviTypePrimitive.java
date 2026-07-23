package giovi.lang;

public sealed interface GioviTypePrimitive<G extends GioviPrimitive<?>>
	extends GioviType<G>
	permits GioviTypeVoid, GioviTypeBool, GioviTypeNumber, GioviTypeText, GioviTypeType {

}

