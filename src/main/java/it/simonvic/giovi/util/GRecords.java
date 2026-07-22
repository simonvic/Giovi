package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.GRecord;
import it.simonvic.giovi.Member;

public class GRecords {

	public static GRecord of(
		String f1, Member<?> v1
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, v1);
		return new GRecord(s);
	}

	public static GRecord of(
		String f1, Member<?> v1,
		String f2, Member<?> v2
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		return new GRecord(s);
	}

	public static GRecord of(
		String f1, Member<?> v1,
		String f2, Member<?> v2,
		String f3, Member<?> v3
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		return new GRecord(s);
	}

	public static GRecord of(
		String f1, Member<?> v1,
		String f2, Member<?> v2,
		String f3, Member<?> v3,
		String f4, Member<?> v4
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		s.put(f4, v4);
		return new GRecord(s);
	}

	public static GRecord of(
		String f1, Member<?> v1,
		String f2, Member<?> v2,
		String f3, Member<?> v3,
		String f4, Member<?> v4,
		String f5, Member<?> v5
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		s.put(f4, v4);
		s.put(f5, v5);
		return new GRecord(s);
	}

}
