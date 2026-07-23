package giovi.lang;

public sealed interface GioviTypeComposite<G extends GioviComposite<?>>
	extends GioviType<G>
	permits GioviTypeList, GioviTypeMap {

}

