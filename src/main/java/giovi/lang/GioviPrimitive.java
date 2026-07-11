package giovi.lang;

public sealed interface GioviPrimitive<T>
	extends GioviValue<T>
	permits GioviNumber, GioviBool, GioviText, GioviType, GioviVoid {

	// TODO: it should be safe to avoid copies for all primitives (Type included)
	// eventually replace with a `return this`

}
