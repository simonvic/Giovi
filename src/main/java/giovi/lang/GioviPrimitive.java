package giovi.lang;

public sealed interface GioviPrimitive<T>
	extends GioviValue<T>
	permits GioviNumber, GioviBool, GioviText, GioviType, GioviVoid {

}
