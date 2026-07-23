package giovi.lang;

public sealed interface GioviTypeNumber<G extends GioviNumber<?>>
	extends GioviTypePrimitive<G>
	permits GioviTypeNatural, GioviTypeInt, GioviTypeReal {

}
