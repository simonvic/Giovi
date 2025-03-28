package giovi;

import java.util.*;
import java.util.stream.Collectors;

import giovi.lang.GioviFunction;
import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NonWideneable;

public record StructSignature(
	String typename,
	List<RecordFieldSignature> fields,
	Map<String, GioviValue<?>> constants,
	Map<String, GioviFunction> functions
) {

	public StructSignature(String typename, RecordFieldSignature... fields) {
		this(typename, Arrays.asList(fields), new LinkedHashMap<>(), new LinkedHashMap<>());
	}

	@Override
	public String toString() {
		if (fields.size() == 0) return typename + "<>";
		var sb = new StringBuilder(typename + "<");
		for (int i = 0; i < fields.size() - 1; i++) {
			var field = fields.get(i);
			sb.append(field.identifier());
			sb.append(" ");
			sb.append(field.type());
			sb.append("?");
			sb.append(field.defaultValue());
			sb.append(", ");
		}
		var lastField = fields.getLast();
		sb.append(lastField.identifier());
		sb.append(" ");
		sb.append(lastField.type());
		sb.append("?");
		sb.append(lastField.defaultValue());
		sb.append(">");
		return sb.toString();
	}


	/**
	 * Spawn a struct described by this signature
	 * @return struct
	 */
	public Struct spawn() {
		var fields = new LinkedHashMap<String, VarD<?>>();
		for (var field : this.fields) {
			fields.put(field.identifier(),
				new VarD<>(field.defaultValue().copy(), field.defaultValue().copy()));
		}
		// TODO: we should copy constants and functions as well, or maybe keep just signatures here and then spawn
		return new Struct(typename, fields, constants, functions);
	}

	/**
	 * Deep copy
	 * @return signature
	 */
	public StructSignature copy() {
		var copied = new ArrayList<RecordFieldSignature>(fields.size());
		for (int i = 0; i < fields.size(); i++) {
			copied.add(fields.get(i).copy());
		}
		return new StructSignature(this.typename, copied, this.constants, this.functions);
	}

	/**
	 * Copy the fields at the indexes mapped by the range
	 * @param range
	 * @return signature
	 */
	// NOTE: we return Record signature
	public RecordSignature copy(Range<Integer> range) {
		// TOOD: DRY
		// TODO: what about negative ranges? Add bound check
		var indexes = range.expand();
		var copied = new ArrayList<RecordFieldSignature>(indexes.size());
		for (var i : indexes) {
			copied.add(fields.get(i).copy());
		}
		return new RecordSignature(copied);
	}

	/**
	 * Check if the provided signature can be widened to this signature
	 * @param narrow
	 * @return true if can be widened, false otherwise
	 */
	public boolean canWiden(TupleSignature narrow) {
		if (narrow.fields().size() > this.fields.size()) {
			return false;
		}
		for (int i = 0; i < narrow.fields().size(); i++) {
			var thisFieldType = this.fields.get(i).type();
			var narrowFieldType = narrow.fields().get(i).type();
			if (!narrowFieldType.isWidenableTo(thisFieldType)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the provided signature can be widened to this signature
	 * @param narrow
	 * @return true if can be widened, false otherwise
	 */
	public boolean canWiden(RecordSignature narrow) {
		if (narrow.fields().size() > this.fields.size()) {
			return false;
		}
		var narrowFields = narrow.fields().stream()
			.collect(Collectors.toMap(f -> f.identifier(), f -> f));

		var widerFields = this.fields.stream()
			.collect(Collectors.toMap(f -> f.identifier(), f -> f));

		if (!widerFields.keySet().containsAll(narrowFields.keySet())) {
			return false;
		}

		for (var widerFieldSignature : this.fields) {
			if (!narrowFields.containsKey(widerFieldSignature.identifier())) {
				continue;
			}
			var narrowField = narrowFields.get(widerFieldSignature.identifier());
			if (!narrowField.type().isWidenableTo(widerFieldSignature.type())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the provided signature can be widened to this signature
	 * @param narrow
	 * @return true if can be widened, false otherwise
	 */
	public boolean canWiden(StructSignature narrow) {
		if (narrow.fields.size() > this.fields.size()) {
			return false;
		}
		var narrowFields = narrow.fields.stream()
			.collect(Collectors.toMap(f -> f.identifier(), f -> f));

		var widerFields = this.fields.stream()
			.collect(Collectors.toMap(f -> f.identifier(), f -> f));

		if (!widerFields.keySet().containsAll(narrowFields.keySet())) {
			return false;
		}

		for (var widerFieldSignature : this.fields) {
			if (!narrowFields.containsKey(widerFieldSignature.identifier())) {
				continue;
			}
			var narrowField = narrowFields.get(widerFieldSignature.identifier());
			if (!narrowField.type().isWidenableTo(widerFieldSignature.type())) {
				return false;
			}
		}
		// TODO: do we ignore constants and functions? Clarify requirements
		return true;
	}

	/**
	 * Widen the provided tuple to a Struct described by this signature
	 * @param narrow
	 * @return widened struct
	 * @throws NonWideneable
	 */
	public Struct widen(Tuple narrow) throws NonWideneable {
		if (narrow.getFieldsCount() > this.fields.size()) {
			throw new NonWideneable("Tuple is wider than signature");
		}
		var widenedFields = new LinkedHashMap<String, VarD<?>>();
		for (int i = 0; i < narrow.getFieldsCount(); i++) {
			var narrowField = narrow.getField(i).orElseThrow();
			var widerFieldSignature = this.fields.get(i);
			// we widen the field, but use the default value of the wider field
			widenedFields.put(widerFieldSignature.identifier(), new VarD<>(
				widerFieldSignature.type().widen(narrowField.value()),
				widerFieldSignature.defaultValue().copy()));
		}
		// "surplus" fields will be set to the wider field default value
		for (int i = narrow.getFieldsCount(); i < this.fields.size(); i++) {
			var widerFieldSignature = this.fields().get(i);
			widenedFields.put(widerFieldSignature.identifier(), new VarD<>(
				widerFieldSignature.defaultValue().copy(),
				widerFieldSignature.defaultValue().copy()));
		}
		return new Struct(this.typename, widenedFields, this.constants, this.functions);
	}

	/**
	 * Widen the provided record to a struct described by this signature
	 * @param narrow
	 * @return widened struct
	 * @throws NonWideneable
	 */
	public Struct widen(Record narrow) throws NonWideneable {
		if (narrow.getFieldsCount() > this.fields.size()) {
			throw new NonWideneable("Record is wider than signature");
		}
		var widerIdentifiers = this.fields.stream().map(f -> f.identifier()).toList();
		if (!widerIdentifiers.containsAll(narrow.fields.keySet())) {
			throw new NonWideneable("Record has field not contained in widening target signature");
		}
		var widenedFields = new LinkedHashMap<String, VarD<?>>();
		for (var widerField : this.fields) {
			GioviValue<?> widenedValue;
			GioviValue<?> widenedDefaultValue = widerField.defaultValue().copy();
			var narrowField = narrow.getField(widerField.identifier());
			if (narrowField.isPresent()) {
				widenedValue = widerField.type().widen(narrowField.orElseThrow().value().copy());
			} else {
				widenedValue = widerField.defaultValue().copy();
			}
			widenedFields.put(
				widerField.identifier(),
				new VarD<>(widenedValue, widenedDefaultValue));
		}
		return new Struct(this.typename, widenedFields, this.constants, this.functions);
	}

	/**
	 * Widen the provided struct to a struct described by this signature
	 * @param narrow
	 * @return widened struct
	 * @throws NonWideneable
	 */
	public Struct widen(Struct narrow) throws NonWideneable {
		// TODO: clarify requirements
		if (narrow.getTypename().equals(this.typename)) {
			return narrow;
		}
		throw new NonWideneable(
			"Can't widen struct " + narrow.getTypename() + " to " + this.typename);
		//if (narrow.getFieldsCount() > this.fields.size()) {
		//	throw new NonWideneable("Record is wider than signature");
		//}
		//var widerIdentifiers = this.fields.stream().map(f -> f.identifier()).toList();
		//if (!widerIdentifiers.containsAll(narrow.fields.keySet())) {
		//	throw new NonWideneable("Record has field not contained in widening target signature");
		//}
		//var widenedFields = new LinkedHashMap<String, VarD<?>>();
		//for (var widerField : this.fields) {
		//	GioviValue<?> widenedValue;
		//	GioviValue<?> widenedDefaultValue = widerField.defaultValue().copy();
		//	var narrowField = narrow.getField(widerField.identifier());
		//	if (narrowField.isPresent()) {
		//		widenedValue = widerField.type().widen(narrowField.orElseThrow().value().copy());
		//	} else {
		//		widenedValue = widerField.defaultValue().copy();
		//	}
		//	widenedFields.put(
		//		widerField.identifier(),
		//		new VarD<>(widenedValue, widenedDefaultValue));
		//}
		//return new Struct(this.typename, widenedFields, this.constants, this.functions);
	}

}
