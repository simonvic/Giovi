package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.GStruct;
import it.simonvic.giovi.Member;

public class GStructs {

	public static GStruct of(String typename) {
		return new GStruct(typename);
	}

	public static GStruct of(
		String typename,
		String f1, Member<?> v1
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, v1);
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
		String f1, Member<?> v1,
		String f2, Member<?> v2
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
		String f1, Member<?> v1,
		String f2, Member<?> v2,
		String f3, Member<?> v3
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
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
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
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
		return new GStruct(typename, s);
	}

	public static <J> GStruct ofNative(
		String typename,
		String f1, J v1
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, Members.of(v1));
		return new GStruct(typename, s);
	}

	public static <J> GStruct ofNative(
		String typename,
		String f1, J v1,
		String f2, J v2
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, Members.of(v1));
		s.put(f2, Members.of(v2));
		return new GStruct(typename, s);
	}

	public static <J> GStruct ofNative(
		String typename,
		String f1, J v1,
		String f2, J v2,
		String f3, J v3
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, Members.of(v1));
		s.put(f2, Members.of(v2));
		s.put(f3, Members.of(v3));
		return new GStruct(typename, s);
	}

	public static <J> GStruct ofNative(
		String typename,
		String f1, J v1,
		String f2, J v2,
		String f3, J v3,
		String f4, J v4
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, Members.of(v1));
		s.put(f2, Members.of(v2));
		s.put(f3, Members.of(v3));
		s.put(f4, Members.of(v4));
		return new GStruct(typename, s);
	}

	public static <J> GStruct ofNative(
		String typename,
		String f1, J v1,
		String f2, J v2,
		String f3, J v3,
		String f4, J v4,
		String f5, J v5
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(f1, Members.of(v1));
		s.put(f2, Members.of(v2));
		s.put(f3, Members.of(v3));
		s.put(f4, Members.of(v4));
		s.put(f5, Members.of(v5));
		return new GStruct(typename, s);
	}

}
