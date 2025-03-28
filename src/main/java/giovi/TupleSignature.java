package giovi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NonWideneable;

public record TupleSignature(List<TupleFieldSignature> fields) {

	public TupleSignature(TupleFieldSignature... fields) {
        this(Arrays.asList(fields));
    }

	public TupleSignature() {
        this(new ArrayList<>());
    }

    @Override
	public String toString() {
		if (fields.isEmpty()) return "Tuple<>";
		var sb = new StringBuilder("Tuple<");
		for (int i = 0; i < fields.size() - 1; i++) {
			var field = fields.get(i);
			sb.append(field.type());
			sb.append(", ");
		}
		sb.append(fields.getLast().type());
		sb.append(">");
		return sb.toString();
	}

	/**
	 * Spawn a tuple described by this signature
	 * @return tuple
	 */
	public Tuple spawn() {
		var f = new ArrayList<VarD<?>>(fields.size());
		for (var field : fields) {
			f.add(new VarD<>(field.defaultValue().copy(), field.defaultValue().copy()));
		}
		return new Tuple(f);
	}

	/**
	 * Deep copy
	 * @return signature
	 */
	public TupleSignature copy() {
		var copied = new ArrayList<TupleFieldSignature>(fields.size());
		for (var field : fields) {
			copied.add(field.copy());
		}
		return new TupleSignature(copied);
	}

	/**
	 * Copy the fields at the indexes mapped by the range
	 * @param range
	 * @return signature
	 */
	public TupleSignature copy(Range<Integer> range) {
		// TODO: what about negative ranges? Add bound check
		var indexes = range.expand();
		var copied = new ArrayList<TupleFieldSignature>(indexes.size());
		for (var i : indexes) {
			copied.add(fields.get(i).copy());
		}
		return new TupleSignature(copied);
	}

	/**
	 * Check if the provided signature can be widened to this signature
	 * @param narrow
	 * @return true if can be widened, false otherwise
	 */
	public boolean canWiden(TupleSignature narrow) {
		if (narrow.fields.size() > this.fields.size()) {
			return false;
		}
		for (int i = 0; i < narrow.fields.size(); i++) {
			var thisFieldType = this.fields.get(i).type();
			var narrowFieldType = narrow.fields.get(i).type();
			if (!narrowFieldType.isWidenableTo(thisFieldType)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Widen the provided tuple to a tuple described by this signature
	 * @param narrow
	 * @return widened tuple
	 * @throws NonWideneable
	 */
	public Tuple widen(Tuple narrow) throws NonWideneable {
		if (narrow.getFieldsCount() > this.fields.size()) {
			throw new NonWideneable("Widening target tuple signature has less fields");
		}
		var widenedFields = new ArrayList<VarD<?>>(this.fields.size());
		for (int i = 0; i < narrow.getFieldsCount(); i++) {
			var narrowField = narrow.getField(i).orElseThrow();
			var thisFieldSignature = this.fields.get(i);
			// we widen the field, but use the default value of the wider field
			widenedFields.add(new VarD<>(
				thisFieldSignature.type().widen(narrowField.value()),
				thisFieldSignature.defaultValue().copy()));
		}
		// "surplus" fields will be set to the wider field default value
		for (int i = narrow.getFieldsCount(); i < this.fields.size(); i++) {
			var thisFieldSignature = this.fields.get(i);
			widenedFields.add(new VarD<>(
				thisFieldSignature.defaultValue().copy(),
				thisFieldSignature.defaultValue().copy()));
		}
		return new Tuple(widenedFields);
	}

}
