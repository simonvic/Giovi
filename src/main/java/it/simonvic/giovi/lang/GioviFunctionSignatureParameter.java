package it.simonvic.giovi.lang;

import it.simonvic.giovi.lang.values.GioviValue;

public record GioviFunctionSignatureParameter(
	GioviValue<?> value,
	boolean hasDefault) {

}
