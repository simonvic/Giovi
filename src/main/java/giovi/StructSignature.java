package giovi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import giovi.lang.GioviFunction;
import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NonWideneable;

public record StructSignature(
	String typename,
	LinkedHashMap<String, RecordFieldSignature> fields,
	Map<String, GioviValue<?>> constants,
	Map<String, GioviFunction> functions
) {

	public StructSignature(String typename, RecordFieldSignature... fields) {
		this(typename, new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
		for (var field : fields) {
			if (this.fields.putIfAbsent(field.identifier(), field) != null) {
				throw new IllegalArgumentException(
					"Duplicate struct field identifier " + field.identifier());
			}
		}
	}

	@Override
	public String toString() {
		if (fields.size() == 0) return typename + "<>";
		var sb = new StringBuilder(typename + "<");
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
	 * Spawn a struct described by this signature
	 * @return struct
	 */
	public Struct spawn() {
		// TODO: we should copy constants and functions as well, or maybe keep just signatures here and then spawn
		return new Struct(
			this.typename,
			RecordSignature.spawn(this.fields),
			this.constants,
			this.functions);
	}

	/**
	 * Deep copy
	 * @return signature
	 */
	public StructSignature copy() {
		return new StructSignature(
			this.typename,
			RecordSignature.copy(this.fields),
			this.constants,
			this.functions);
	}

	/**
	 * Copy the fields at the indexes mapped by the range
	 * @param range
	 * @return signature
	 */
	// NOTE: we return Record signature
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
	 * @return true if can be widened, false otherwise
	 */
	public Struct widen(Tuple narrow) throws NonWideneable {
		return new Struct(
			this.typename,
			RecordSignature.widenTuple(narrow, this.fields),
			this.constants,
			this.functions);
	}

	/**
	 * Check if the provided signature can be widened to this signature
	 * @param narrow
	 * @return true if can be widened, false otherwise
	 */
	public boolean canWiden(RecordSignature narrow) {
		return RecordSignature.canWiden(narrow.fields(), this.fields);
	}

	/**
	 * Widen the provided tuple to a Struct described by this signature
	 * @param narrow
	 * @return widened struct
	 * @throws NonWideneable
	 */
	public Struct widen(Record narrow) throws NonWideneable {
		return new Struct(
			this.typename,
			RecordSignature.widenRecord(narrow, this.fields),
			this.constants,
			this.functions);
	}

	/**
	 * Widen the provided record to a struct described by this signature
	 * @param narrow
	 * @return widened struct
	 * @throws NonWideneable
	 */
	public boolean canWiden(StructSignature narrow) {
		// TODO: do we ignore constants and functions? Clarify requirements
		return RecordSignature.canWiden(narrow.fields, this.fields);
	}

	/**
	 * Widen the provided struct to a struct described by this signature
	 * @param narrow
	 * @return widened struct
	 * @throws NonWideneable
	 */
	public Struct widen(Struct narrow) throws NonWideneable {
		if (narrow.getTypename().equals(this.typename)) {
			return narrow;
		}
		throw new NonWideneable(
			"Can't widen struct " + narrow.getTypename() + " to " + this.typename);
	}

}
