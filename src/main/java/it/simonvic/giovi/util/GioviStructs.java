package it.simonvic.giovi.util;

import java.util.LinkedHashMap;

import giovi.GStruct;
import giovi.RecordFieldSignature;
import giovi.StructSignature;
import giovi.lang.*;
import it.simonvic.giovi.Member;
import it.simonvic.giovi.exceptions.IncompatibleFieldsCount;
import it.simonvic.giovi.exceptions.NoSuchField;

public class GioviStructs {

	public static GioviTypeStruct typeFrom(GStruct value) {
		return new GioviTypeStruct(signatureFrom(value));
	}

	public static StructSignature signatureFrom(GStruct value) {
		var fields = new LinkedHashMap<String, RecordFieldSignature>(value.getFieldsCount());
		value.forEach(
			(identifier, field) -> fields.put(
				identifier,
				new RecordFieldSignature(
					identifier,
					field.unwrap().getGioviType(),
					field.defaultValue()
				)
			)
		);
		return new StructSignature(
			value.getTypename(),
			fields
		);
	}

	public static GioviStruct of(String typename) {
		var value = new GStruct(typename);
		return new GioviStruct(typeFrom(value), value);
	}

	public static GioviStruct of(
		String typename,
		String i1, Member<?> v1
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(i1, v1);
		var value = new GStruct(typename, s);
		return new GioviStruct(typeFrom(value), value);
	}

	public static GioviStruct of(
		String typename,
		String i1, Member<?> v1,
		String i2, Member<?> v2
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		var value = new GStruct(typename, s);
		return new GioviStruct(typeFrom(value), value);
	}

	public static GioviStruct of(
		String typename,
		String i1, Member<?> v1,
		String i2, Member<?> v2,
		String i3, Member<?> v3
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		s.put(i3, v3);
		var value = new GStruct(typename, s);
		return new GioviStruct(typeFrom(value), value);
	}

	public static GioviStruct of(
		String typename,
		String i1, Member<?> v1,
		String i2, Member<?> v2,
		String i3, Member<?> v3,
		String i4, Member<?> v4
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(i1, v1);
		s.put(i2, v2);
		s.put(i3, v3);
		s.put(i4, v4);
		var value = new GStruct(typename, s);
		return new GioviStruct(typeFrom(value), value);
	}

	public static <J> GioviStruct ofNative(
		String typename,
		String i1, J v1
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(i1, Members.of(v1));
		var value = new GStruct(typename, s);
		return new GioviStruct(typeFrom(value), value);
	}

	public static <J> GioviStruct ofNative(
		String typename,
		String i1, J v1,
		String i2, J v2
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(i1, Members.of(v1));
		s.put(i2, Members.of(v2));
		var value = new GStruct(typename, s);
		return new GioviStruct(typeFrom(value), value);
	}

	public static <J> GioviStruct ofNative(
		String typename,
		String i1, J v1,
		String i2, J v2,
		String i3, J v3
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(i1, Members.of(v1));
		s.put(i2, Members.of(v2));
		s.put(i3, Members.of(v3));
		var value = new GStruct(typename, s);
		return new GioviStruct(typeFrom(value), value);
	}

	public static <J> GioviStruct ofNative(
		String typename,
		String i1, J v1,
		String i2, J v2,
		String i3, J v3,
		String i4, J v4
	) {
		var s = new LinkedHashMap<String, Member<?>>();
		s.put(i1, Members.of(v1));
		s.put(i2, Members.of(v2));
		s.put(i3, Members.of(v3));
		s.put(i4, Members.of(v4));
		var value = new GStruct(typename, s);
		return new GioviStruct(typeFrom(value), value);
	}

	public static GioviStruct redirection(GioviStruct dest, GioviTuple content) {
		if (content.value().getFieldsCount() != dest.value().getFieldsCount()) {
			throw new IncompatibleFieldsCount(dest.value(), content.value());
		}
		var newDest = dest.value().copy();
		for (int i = 0; i < content.value().getFieldsCount(); i++) {
			final int j = i;
			var contentField = content.value().getField(j).orElseThrow();
			var newField = newDest.getField(j).orElseThrow(() -> new NoSuchField(j));
			if (newField.unwrap() instanceof GioviSelf) {
				newField.setValueUnsafe(dest.type().spawn());
			}
			newField.setValueWithRedirection(contentField.unwrap());
		}
		return new GioviStruct(dest.type(), newDest);
	}

	public static GioviStruct redirection(GioviStruct dest, GioviRecord content) {
		// TODO: check requirements and candidate about fields override
		// the new struct should be dest.signature().spawn()
		var newDest = dest.value().copy();
		for (var contentEntry : content.value().entrySet()) {
			var fieldIdentifier = contentEntry.getKey();
			var contentField = contentEntry.getValue();
			var newField = newDest.getField(fieldIdentifier)
				.orElseThrow(() -> new NoSuchField(fieldIdentifier));
			if (newField.unwrap() instanceof GioviSelf) {
				newField.setValueUnsafe(dest.type().spawn());
			}
			newField.setValueWithRedirection(contentField.unwrap());
		}
		return new GioviStruct(dest.type(), newDest);
	}

	public static GioviStruct redirection(GioviStruct dest, GioviStruct content) {
		var newDest = dest.value().copy();
		for (var contentEntry : content.value().entrySet()) {
			var fieldIdentifier = contentEntry.getKey();
			var contentField = contentEntry.getValue();
			var newField = newDest.getField(fieldIdentifier)
				.orElseThrow(() -> new NoSuchField(fieldIdentifier));
			if (newField.unwrap() instanceof GioviSelf) {
				newField.setValueUnsafe(dest.type().spawn());
			}
			newField.setValueWithRedirection(contentField.unwrap());
		}
		return new GioviStruct(dest.type(), newDest);
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
			new RecordFieldSignature(i1, t1)
		);
	}

	public static StructSignature signatureOf(
		String typename,
		String i1, GioviType<?> t1,
		String i2, GioviType<?> t2
	) {
		return new StructSignature(
			typename,
			new RecordFieldSignature(i1, t1),
			new RecordFieldSignature(i2, t2)
		);
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
			new RecordFieldSignature(i3, t3)
		);
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
			new RecordFieldSignature(i4, t4)
		);
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
			new RecordFieldSignature(i5, t5)
		);
	}

}
