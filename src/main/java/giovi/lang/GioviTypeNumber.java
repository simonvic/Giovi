package giovi.lang;

public sealed interface GioviTypeNumber<T extends GioviNumber<?>>
	extends GioviTypePrimitive<T>
	permits GioviTypeNatural, GioviTypeInt, GioviTypeReal {

}
