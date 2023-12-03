package it.simonvic.giovi.exceptions;

import it.simonvic.giovi.lang.values.GioviMap;
import it.simonvic.giovi.lang.values.GioviValue;

public class KeyNotPresent extends GioviException {

    public KeyNotPresent(GioviMap gioviMap, GioviValue<?> key) {
    	super("Key " + key + " not present in map " + gioviMap);
    }

}

