package it.simonvic.giovi.util;

import it.simonvic.giovi.lang.values.GioviStruct;
import it.simonvic.giovi.lang.values.Struct;

import java.util.LinkedHashMap;

public class GioviStructs {

	public static GioviStruct of(Struct v) {
		return new GioviStruct(v);
	}

	public static Struct defaultOf(Struct value) {
		return new Struct(new LinkedHashMap<>());
	}
}
