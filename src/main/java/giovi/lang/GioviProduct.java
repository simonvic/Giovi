package giovi.lang;

import giovi.GTuple;

public sealed interface GioviProduct<T extends GTuple>
	extends GioviValue<T>
	permits GioviTuple, GioviRecord, GioviStruct {

}

