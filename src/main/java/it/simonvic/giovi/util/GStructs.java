package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.GStruct;
import it.simonvic.giovi.GField;

public class GStructs {

	public static GStruct of(String typename) {
		return new GStruct(typename);
	}

	public static GStruct of(
		String typename,
		String f1, GField<?> v1
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, v1);
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
		String f1, GField<?> v1,
		String f2, GField<?> v2
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
		String f1, GField<?> v1,
		String f2, GField<?> v2,
		String f3, GField<?> v3
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, v1);
		s.put(f2, v2);
		s.put(f3, v3);
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
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
		return new GStruct(typename, s);
	}

	public static GStruct of(
		String typename,
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
		return new GStruct(typename, s);
	}

	public static <J> GStruct ofNative(
		String typename,
		String f1, J v1
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, GFields.of(v1));
		return new GStruct(typename, s);
	}

	public static <J> GStruct ofNative(
		String typename,
		String f1, J v1,
		String f2, J v2
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, GFields.of(v1));
		s.put(f2, GFields.of(v2));
		return new GStruct(typename, s);
	}

	public static <J> GStruct ofNative(
		String typename,
		String f1, J v1,
		String f2, J v2,
		String f3, J v3
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, GFields.of(v1));
		s.put(f2, GFields.of(v2));
		s.put(f3, GFields.of(v3));
		return new GStruct(typename, s);
	}

	public static <J> GStruct ofNative(
		String typename,
		String f1, J v1,
		String f2, J v2,
		String f3, J v3,
		String f4, J v4
	) {
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, GFields.of(v1));
		s.put(f2, GFields.of(v2));
		s.put(f3, GFields.of(v3));
		s.put(f4, GFields.of(v4));
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
		var s = new LinkedHashMap<String, GField<?>>();
		s.put(f1, GFields.of(v1));
		s.put(f2, GFields.of(v2));
		s.put(f3, GFields.of(v3));
		s.put(f4, GFields.of(v4));
		s.put(f5, GFields.of(v5));
		return new GStruct(typename, s);
	}

}
