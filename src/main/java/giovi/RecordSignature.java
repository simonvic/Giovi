package giovi;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NonWideneable;

public record RecordSignature(LinkedHashMap<String, RecordFieldSignature> fields) {

	public RecordSignature(RecordFieldSignature... fields) {
		this(new LinkedHashMap<>());
		for (var field : fields) {
			if (this.fields.putIfAbsent(field.identifier(), field) != null) {
				throw new IllegalArgumentException(
					"Duplicate record field identifier " + field.identifier());
			}
		}
	}

	public RecordSignature() {
		this(new LinkedHashMap<>());
	}

	@Override
	public String toString() {
		if (fields.isEmpty()) return "Record<>";
		var sb = new StringBuilder("Record<");
		var i = this.fields.values().iterator();
		while (i.hasNext()) {
			var next = i.next();
			sb.append(next.identifier());
			sb.append(" ");
			sb.append(next.type());
			sb.append(" ?");
			sb.append(next.defaultValue());
			sb.append(i.hasNext() ? ", " : ">");
		}
		return sb.toString();
	}

	/**
	 * Spawn a record described by this signature
	 * @return record
	 */
	public Record spawn() {
		var fields = new LinkedHashMap<String, VarD<?>>();
		for (var field : this.fields.values()) {
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
		var copied = new LinkedHashMap<String, RecordFieldSignature>(fields.size());
		for (var field : fields.values()) {
			copied.put(field.identifier(), field.copy());
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
		var copied = new LinkedHashMap<String, RecordFieldSignature>(indexes.size());
		var fieldsArray = new ArrayList<>(fields.values());
		for (var i : indexes) {
			var field = fieldsArray.get(i);
			copied.put(field.identifier(), field.copy());
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
		var iter = this.fields.values().iterator();
		for (var narrowField : narrow.fields()) {
			var thisField = iter.next();
			if (!narrowField.type().isWidenableTo(thisField.type())) {
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

		var narrowFields = narrow.fields();
		if (!this.fields.keySet().containsAll(narrowFields.keySet())) {
			return false;
		}

		for (var widerFieldSignature : this.fields.values()) {
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
		var iter = this.fields.values().iterator();
		for (int i = 0; i < narrow.getFieldsCount(); i++) {
			var narrowField = narrow.getField(i).orElseThrow();
			var widerFieldSignature = iter.next();
			// we widen the field, but use the default value of the wider field
			widenedFields.put(widerFieldSignature.identifier(), new VarD<>(
				widerFieldSignature.type().widen(narrowField.value()),
				widerFieldSignature.defaultValue().copy()));
		}
		// "surplus" fields will be set to the wider field default value
		while (iter.hasNext()) {
			var widerFieldSignature = iter.next();
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
		if (!this.fields.keySet().containsAll(narrow.fields.keySet())) {
			throw new NonWideneable("Record has field not contained in widening target signature");
		}
		var widenedFields = new LinkedHashMap<String, VarD<?>>();
		for (var widerField : this.fields.values()) {
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
