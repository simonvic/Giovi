package giovi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NonWideneable;

public record RecordSignature(List<RecordFieldSignature> fields) {

	public RecordSignature(RecordFieldSignature... fields) {
        this(Arrays.asList(fields));
    }

	public RecordSignature() {
        this(new ArrayList<>());
    }

    @Override
	public String toString() {
		if (fields.isEmpty()) return "Record<>";
		var sb = new StringBuilder("Record<");
		for (int i = 0; i < fields.size() - 1; i++) {
			var field = fields.get(i);
			sb.append(field.identifier());
			sb.append(" ");
			sb.append(field.type());
			sb.append(", ");
		}
		var lastField = fields.getLast();
		sb.append(lastField.identifier());
		sb.append(" ");
		sb.append(lastField.type());
		sb.append(">");
		return sb.toString();
	}


	/**
	 * Spawn a record described by this signature
	 * @return record
	 */
	public Record spawn() {
		var fields = new LinkedHashMap<String, VarD<?>>();
		for (var field : this.fields) {
			fields.put(field.identifier(),
				new VarD<>(field.defaultValue().copy(), field.defaultValue().copy()));
		}
		return new Record(fields);
	}

	/**
	 * Deep copy
	 * @return signature
	 */
	public RecordSignature copy() {
		var copied = new ArrayList<RecordFieldSignature>(fields.size());
		for (var field : fields) {
			copied.add(field.copy());
		}
		return new RecordSignature(copied);
	}

	/**
	 * Copy the fields at the indexes mapped by the range
	 * @param range
	 * @return signature
	 */
	public RecordSignature copy(Range<Integer> range) {
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
		return true;
	}

	/**
	 * Widen the provided tuple to a record described by this signature
	 * @param narrow
	 * @return widened record
	 * @throws NonWideneable
	 */
	public Record widen(Tuple narrow) throws NonWideneable {
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
		return new Record(widenedFields);
	}

	/**
	 * Widen the provided record to a record described by this signature
	 * @param narrow
	 * @return widened record
	 * @throws NonWideneable
	 */
	public Record widen(Record narrow) throws NonWideneable {
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
		return new Record(widenedFields);
	}


}
