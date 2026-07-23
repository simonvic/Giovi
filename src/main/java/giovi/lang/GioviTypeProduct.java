package giovi.lang;

public sealed interface GioviTypeProduct<G extends GioviProduct<?>>
	extends GioviType<G>
	permits GioviTypeTuple, GioviTypeRecord, GioviTypeStruct {

}

