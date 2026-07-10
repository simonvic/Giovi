package giovi.lang;

public sealed interface GioviTypeComposite<T extends GioviComposite<?>>
	extends GioviType<T>
	permits GioviTypeList, GioviTypeMap {

}

