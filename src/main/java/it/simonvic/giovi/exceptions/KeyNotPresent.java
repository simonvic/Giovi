package it.simonvic.giovi.exceptions;

import giovi.lang.GioviMap;
import giovi.lang.GioviValue;

public class KeyNotPresent extends GioviException {

    public KeyNotPresent(GioviMap gioviMap, GioviValue<?> key) {
    	super("Key " + key + " not present in map " + gioviMap);
    }

}

