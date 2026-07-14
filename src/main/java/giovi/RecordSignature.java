package giovi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NonWideneable;

public record RecordSignature(LinkedHashMap<String, RecordFieldSignature> fields)
	implements ProductSignature {

	public RecordSignature(RecordFieldSignature... fields) {
		this(new LinkedHashMap<>());
		for (var field : fields) {
			if (this.fields.putIfAbsent(field.identifier(), field) != null) {
				throw new IllegalArgumentException(
					"Duplicate record field identifier " + field.identifier()
				);
			}
		}
	}

	public RecordSignature() {
		this(new LinkedHashMap<>());
	}

	@Override
	public String toString() {
		if (fields.isEmpty()) return "<>";
		var sb = new StringBuilder("<");
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
	public GRecord spawn() {
		return new GRecord(RecordSignature.spawn(this.fields));
	}

	/**
	 * Deep copy
	 * @return signature
	 */
	public RecordSignature copy() {
		return new RecordSignature(RecordSignature.copy(this.fields));
	}

	/**
	 * Copy the fields at the indexes mapped by the range
	 * @param range
	 * @return signature
	 */
	public RecordSignature copy(Range<Integer> range) {
		return new RecordSignature(RecordSignature.copy(this.fields, range));
	}

	/**
	 * Check if the provided signature can be widened to this signature
	 * @param narrow
	 * @return true if can be widened, false otherwise
	 */
	public boolean canWiden(TupleSignature narrow) {
		return RecordSignature.canWiden(narrow.fields(), this.fields);
	}

	/**
	 * Check if the provided signature can be widened to this signature
	 * @param narrow
	 * @return widened tuple
	 * @throws NonWideneable
	 */
	public GRecord widen(GTuple narrow) throws NonWideneable {
		return new GRecord(RecordSignature.widenTuple(narrow, this.fields));
	}

	/**
	 * Widen the provided tuple to a record described by this signature
	 * @param narrow
	 * @return widened record
	 */
	public boolean canWiden(RecordSignature narrow) {
		return RecordSignature.canWiden(narrow.fields(), this.fields);
	}

	/**
	 * Widen the provided record to a record described by this signature
	 * @param narrow
	 * @return widened record
	 * @throws NonWideneable
	 */
	public GRecord widen(GRecord narrow) throws NonWideneable {
		return new GRecord(RecordSignature.widenRecord(narrow, this.fields));
	}

	/**
	 * @param signatureFields
	 * @return spanwed fields
	 */
	public static LinkedHashMap<String, VarD<?>> spawn(
		LinkedHashMap<String, RecordFieldSignature> signatureFields
	) {
		var fields = new LinkedHashMap<String, VarD<?>>();
		for (var field : signatureFields.values()) {
			fields.put(
				field.identifier(),
				new VarD<>(field.defaultValue().copy(), field.defaultValue().copy())
			);
		}
		return fields;
	}

	/**
	 * @param signatureFields
	 * @return copied fields
	 */
	public static LinkedHashMap<String, RecordFieldSignature> copy(
		LinkedHashMap<String, RecordFieldSignature> signatureFields
	) {
		var copied = new LinkedHashMap<String, RecordFieldSignature>(signatureFields.size());
		for (var field : signatureFields.values()) {
			copied.put(field.identifier(), field.copy());
		}
		return copied;
	}

	/**
	 * @param signatureFields
	 * @param range
	 * @return copied fields
	 */
	public static LinkedHashMap<String, RecordFieldSignature> copy(
		LinkedHashMap<String, RecordFieldSignature> signatureFields,
		Range<Integer> range
	) {
		// TODO: what about negative ranges? Add bound check
		var indexes = range.expand();
		var copied = new LinkedHashMap<String, RecordFieldSignature>(indexes.size());
		var fieldsArray = new ArrayList<>(signatureFields.values());
		for (var i : indexes) {
			var field = fieldsArray.get(i);
			copied.put(field.identifier(), field.copy());
		}
		return copied;
	}

	/**
	 * @param wide
	 * @param narrow
	 * @return true if can widen
	 */
	public static boolean canWiden(
		List<TupleFieldSignature> narrow,
		LinkedHashMap<String, RecordFieldSignature> wide
	) {
		if (narrow.size() > wide.size()) {
			return false;
		}
		var iter = wide.values().iterator();
		for (var narrowField : narrow) {
			var thisField = iter.next();
			if (!narrowField.type().isWidenableTo(thisField.type())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param wide
	 * @param narrow
	 * @return widened tuple
	 */
	public static LinkedHashMap<String, VarD<?>> widenTuple(
		GTuple narrow,
		LinkedHashMap<String, RecordFieldSignature> wide
	) throws NonWideneable {
		if (narrow.getFieldsCount() > wide.size()) {
			throw new NonWideneable("Tuple is wider than signature");
		}
		var widenedFields = new LinkedHashMap<String, VarD<?>>();
		var iter = wide.values().iterator();
		for (int i = 0; i < narrow.getFieldsCount(); i++) {
			var narrowField = narrow.getField(i).orElseThrow();
			var widerFieldSignature = iter.next();
			// we widen the field, but use the default value of the wider field
			widenedFields.put(
				widerFieldSignature.identifier(),
				new VarD<>(
					widerFieldSignature.type().widen(narrowField.unwrap()),
					widerFieldSignature.defaultValue().copy()
				)
			);
		}
		// "surplus" fields will be set to the wider field default value
		while (iter.hasNext()) {
			var widerFieldSignature = iter.next();
			widenedFields.put(
				widerFieldSignature.identifier(),
				new VarD<>(
					widerFieldSignature.defaultValue().copy(),
					widerFieldSignature.defaultValue().copy()
				)
			);
		}
		return widenedFields;
	}

	/**
	 * @param narrow
	 * @param wide
	 * @return
	 */
	public static boolean canWiden(
		LinkedHashMap<String, RecordFieldSignature> narrow,
		LinkedHashMap<String, RecordFieldSignature> wide
	) {
		if (narrow.size() > wide.size()) {
			return false;
		}
		if (!wide.keySet().containsAll(narrow.keySet())) {
			return false;
		}
		for (var widerFieldSignature : wide.values()) {
			if (!narrow.containsKey(widerFieldSignature.identifier())) {
				continue;
			}
			var narrowField = narrow.get(widerFieldSignature.identifier());
			if (!narrowField.type().isWidenableTo(widerFieldSignature.type())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param narrow
	 * @param wide
	 * @return
	 * @throws NonWideneable
	 */
	public static LinkedHashMap<String, VarD<?>> widenRecord(
		GRecord narrow,
		LinkedHashMap<String, RecordFieldSignature> wide
	) throws NonWideneable {
		if (narrow.getFieldsCount() > wide.size()) {
			throw new NonWideneable("Record is wider than signature");
		}
		if (!wide.keySet().containsAll(narrow.fields.keySet())) {
			throw new NonWideneable("Record has field not contained in widening target signature");
		}
		var widenedFields = new LinkedHashMap<String, VarD<?>>();
		for (var widerField : wide.values()) {
			GioviValue<?> widenedValue;
			GioviValue<?> widenedDefaultValue = widerField.defaultValue().copy();
			var narrowField = narrow.getField(widerField.identifier());
			if (narrowField.isPresent()) {
				widenedValue = widerField.type().widen(narrowField.orElseThrow().unwrap().copy());
			} else {
				widenedValue = widerField.defaultValue().copy();
			}
			widenedFields.put(
				widerField.identifier(),
				new VarD<>(widenedValue, widenedDefaultValue)
			);
		}
		return widenedFields;
	}

}
