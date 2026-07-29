package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.GRecord;
import it.simonvic.giovi.GField;

public class GRecords {

	public static GRecord of(
		String f1, GField<?> v1
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, v1);
		return new GRecord(s);
	}

	public static GRecord of(
		String f1, GField<?> v1,
		String f2, GField<?> v2
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		return new GRecord(s);
	}

	public static GRecord of(
		String f1, GField<?> v1,
		String f2, GField<?> v2,
		String f3, GField<?> v3
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		return new GRecord(s);
	}

	public static GRecord of(
		String f1, GField<?> v1,
		String f2, GField<?> v2,
		String f3, GField<?> v3,
		String f4, GField<?> v4
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		s.put(f4, v4);
		return new GRecord(s);
	}

	public static GRecord of(
		String f1, GField<?> v1,
		String f2, GField<?> v2,
		String f3, GField<?> v3,
		String f4, GField<?> v4,
		String f5, GField<?> v5
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		s.put(f4, v4);
		s.put(f5, v5);
		return new GRecord(s);
	}

}
