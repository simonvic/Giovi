package giovi.lang;

import giovi.GProduct;

public sealed interface GioviProduct<J extends GProduct>
	extends GioviValue<J>
	permits GioviTuple, GioviRecord, GioviStruct {

}

