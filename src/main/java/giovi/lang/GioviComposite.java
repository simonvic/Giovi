package giovi.lang;

public sealed interface GioviComposite<J>
	extends GioviValue<J>
	permits GioviList, GioviMap {

}

