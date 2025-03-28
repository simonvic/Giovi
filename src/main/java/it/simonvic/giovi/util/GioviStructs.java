package it.simonvic.giovi.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import giovi.RecordFieldSignature;
import giovi.Struct;
import giovi.StructSignature;
import giovi.lang.*;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.IncompatibleFieldsCount;
import it.simonvic.giovi.exceptions.NoSuchField;

public class GioviStructs {

	public static StructSignature signatureFrom(Struct value) {
		var fields = new LinkedHashMap<String, RecordFieldSignature>(value.getFieldsCount());
		value.forEach((identifier, field) -> fields.put(identifier, new RecordFieldSignature(
			identifier,
			field.value().getGioviType(),
			field.defaultValue())));
		return new StructSignature(
			value.getTypename(),
			fields,
			value.getConstants(),
			value.getFunctions());
	}

	public static GioviStruct of(String typename) {
		var value = new Struct(typename);
		return new GioviStruct(signatureFrom(value), value);
	}

	public static GioviStruct of(
		String typename,
		String i1, VarD<?> v1
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, v1);
		var value = new Struct(typename, s);
		return new GioviStruct(signatureFrom(value), value);
	}

	public static GioviStruct of(
		String typename,
		String i1, VarD<?> v1,
		String i2, VarD<?> v2
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		var value = new Struct(typename, s);
		return new GioviStruct(signatureFrom(value), value);
	}

	public static GioviStruct of(
		String typename,
		String i1, VarD<?> v1,
		String i2, VarD<?> v2,
		String i3, VarD<?> v3
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		s.put(i3, v3);
		var value = new Struct(typename, s);
		return new GioviStruct(signatureFrom(value), value);
	}

	public static GioviStruct of(
		String typename,
		String i1, VarD<?> v1,
		String i2, VarD<?> v2,
		String i3, VarD<?> v3,
		String i4, VarD<?> v4
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		s.put(i3, v3);
		s.put(i4, v4);
		var value = new Struct(typename, s);
		return new GioviStruct(signatureFrom(value), value);
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		var value = new Struct(typename, s);
		return new GioviStruct(signatureFrom(value), value);
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1,
		String i2, T v2
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		var value = new Struct(typename, s);
		return new GioviStruct(signatureFrom(value), value);
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1,
		String i2, T v2,
		String i3, T v3
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		s.put(i3, VarD.of(v3));
		var value = new Struct(typename, s);
		return new GioviStruct(signatureFrom(value), value);
	}

	public static <T> GioviStruct ofNative(
		String typename,
		String i1, T v1,
		String i2, T v2,
		String i3, T v3,
		String i4, T v4
	) {
		var s = new LinkedHashMap<String, VarD<?>>();
		s.put(i1, VarD.of(v1));
		s.put(i2, VarD.of(v2));
		s.put(i3, VarD.of(v3));
		s.put(i4, VarD.of(v4));
		var value = new Struct(typename, s);
		return new GioviStruct(signatureFrom(value), value);
	}

	public static GioviStruct redirection(GioviStruct dest, GioviTuple content) {
		if (content.value().getFieldsCount() != dest.value().getFieldsCount()) {
			throw new IncompatibleFieldsCount(dest.value(), content.value());
		}
		var newStruct = dest.value().copy();
		content.value().forEachIndexed((i, f) -> newStruct
			.getField(i).orElseThrow(() -> new NoSuchField(i))
			.setValueWithRedirection(f.value()));
		return new GioviStruct(dest.signature(), newStruct);
	}

	public static GioviStruct redirection(GioviStruct dest, GioviRecord content) {
		// TODO: check requirements and candidate about fields override
		// the new struct should be dest.signature().spawn()
		var newStruct = dest.value().copy();
		content.value().forEach((s, f) -> newStruct
			.getField(s).orElseThrow(() -> new NoSuchField(s))
			.setValueWithRedirection(f.value()));
		return new GioviStruct(dest.signature(), newStruct);
	}

	public static GioviStruct redirection(GioviStruct dest, GioviStruct content) {
		var newStruct = dest.value().copy();
		content.value().forEach((s, f) -> newStruct
			.getField(s).orElseThrow(() -> new NoSuchField(s))
			.setValueWithRedirection(f.value()));
		return new GioviStruct(dest.signature(), newStruct);
	}

	public static StructSignature signatureOf(
		String typename
	) {
		return new StructSignature(typename);
	}

	public static StructSignature signatureOf(
		String typename,
		String i1, GioviType<?> t1
	) {
		return new StructSignature(
			typename,
			new RecordFieldSignature(i1, t1));
	}

	public static StructSignature signatureOf(
		String typename,
		String i1, GioviType<?> t1,
		String i2, GioviType<?> t2
	) {
		return new StructSignature(
			typename,
			new RecordFieldSignature(i1, t1),
			new RecordFieldSignature(i2, t2));
	}

	public static StructSignature signatureOf(
		String typename,
		String i1, GioviType<?> t1,
		String i2, GioviType<?> t2,
		String i3, GioviType<?> t3
	) {
		return new StructSignature(
			typename,
			new RecordFieldSignature(i1, t1),
			new RecordFieldSignature(i2, t2),
			new RecordFieldSignature(i3, t3));
	}

	public static StructSignature signatureOf(
		String typename,
		String i1, GioviType<?> t1,
		String i2, GioviType<?> t2,
		String i3, GioviType<?> t3,
		String i4, GioviType<?> t4
	) {
		return new StructSignature(
			typename,
			new RecordFieldSignature(i1, t1),
			new RecordFieldSignature(i2, t2),
			new RecordFieldSignature(i3, t3),
			new RecordFieldSignature(i4, t4));
	}

	public static StructSignature signatureOf(
		String typename,
		String i1, GioviType<?> t1,
		String i2, GioviType<?> t2,
		String i3, GioviType<?> t3,
		String i4, GioviType<?> t4,
		String i5, GioviType<?> t5
	) {
		return new StructSignature(
			typename,
			new RecordFieldSignature(i1, t1),
			new RecordFieldSignature(i2, t2),
			new RecordFieldSignature(i3, t3),
			new RecordFieldSignature(i4, t4),
			new RecordFieldSignature(i5, t5));
	}

}
