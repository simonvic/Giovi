package giovi.lang;

public sealed interface GioviComposite<T>
	extends GioviValue<T>
	permits GioviList, GioviMap {

}

