package giovi.lang;

public sealed interface GioviTypeProduct<T extends GioviProduct<?>>
	extends GioviType<T>
	permits GioviTypeTuple, GioviTypeRecord, GioviTypeStruct {

}

