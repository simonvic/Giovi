package giovi.lang;

import giovi.GProduct;

public sealed interface GioviProduct<T extends GProduct>
	extends GioviValue<T>
	permits GioviTuple, GioviRecord, GioviStruct {

}

