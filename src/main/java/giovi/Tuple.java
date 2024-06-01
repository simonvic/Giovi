package giovi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import giovi.lang.GioviValue;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.exceptions.OperatorNotSupported;

public sealed class Tuple permits TupleNamed {

	private final List<GioviValue<?>> fields;

	public Tuple() {
		fields = new ArrayList<>();
	}

	public Tuple(List<GioviValue<?>> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		var i = fields.iterator();
		if (!i.hasNext()) return "<>";

		StringBuilder sb = new StringBuilder();
		sb.append('<');
		while (i.hasNext()) {
			sb.append(i.next());
			sb.append(i.hasNext() ? ", " : ">");
		}
		return sb.toString();
	}

	// TODO: eventually remove this
	public String toStringDefinition() {
		var i = fields.iterator();
		if (!i.hasNext()) return "<>";
		StringBuilder sb = new StringBuilder();
		sb.append('<');
		while (i.hasNext()) {
			sb.append(i.next().getGioviType());
			sb.append(i.hasNext() ? ", " : ">");
		}
		return sb.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Tuple other = (Tuple) obj;
		if (fields == null) {
			if (other.fields != null) return false;
		} else if (!fields.equals(other.fields)) return false;
		return true;
	}

	public Tuple copy() {
		var a = new ArrayList<GioviValue<?>>();
		this.fields.forEach(value -> a.add(value.copy()));
		return new Tuple(a);
	}

	public Tuple copy(Range<Integer> range) {
		var list = new ArrayList<GioviValue<?>>();
		range.iterate(index -> list.add(getField(index).copy()));
		return new Tuple(list);
	}

	public int getFieldsCount() {
		return fields.size();
	}

	public GioviValue<?> getField(int index) {
		return fields.get(index);
	}

	public void forEach(Consumer<GioviValue<?>> valueConsumer) {
		fields.forEach(valueConsumer);
	}

	public void forEachIndexed(BiConsumer<Integer, GioviValue<?>> valueConsumer) {
		for (int i = 0; i < fields.size(); i++) {
			valueConsumer.accept(i, fields.get(i));
		}
	}

	public boolean equal(Tuple other) {
		if (this.fields.size() != other.fields.size()) return false;
		for (int i = 0; i < fields.size(); i++) {
			var thisField = this.fields.get(i);
			var otherField = other.fields.get(i);
			try {
				if (!thisField.equal(otherField).asNativeBool()) {
					return false;
				}
			} catch (OperatorNotSupported e) {
				return false;
			}
		}
		return true;
	}

	// TODO: clarify widening requirements

	public boolean canWidenTo(Tuple to) {
		if (this.fields.size() != to.fields.size()) {
			return false;
		}
		for (int i = 0; i < fields.size(); i++) {
			var thisFieldType = this.fields.get(i).getGioviType();
			var otherFieldType = to.fields.get(i).getGioviType();
			if (!thisFieldType.canWidenTo(otherFieldType)) {
				return false;
			}
		}
		return true;
	}

	public Tuple widenTo(Tuple to) throws NonWideneable {
		if (this.fields.size() != to.fields.size()) {
			throw new NonWideneable("Tuples have different fields count");
		}
		var widenedFields = new ArrayList<GioviValue<?>>();
		for (int i = 0; i < fields.size(); i++) {
			widenedFields.add(this.fields.get(i).widenTo(to.fields.get(i).getGioviType()));
		}
		return new Tuple(widenedFields);
	}

	public TupleNamed widenTo(TupleNamed to) throws NonWideneable {
		if (this.fields.size() != to.fields.size()) {
			throw new NonWideneable("Tuples have different fields count");
		}
		var widenedFields = new LinkedHashMap<String, GioviValue<?>>();
		int i = 0;
		for (var entry : to.fields.entrySet()) {
			widenedFields.put(
				entry.getKey(),
				this.getField(i).widenTo(entry.getValue().getGioviType()));
			i++;
		}
		return new TupleNamed(widenedFields);
	}

	public Struct widenTo(Struct to) throws NonWideneable {
		if (this.fields.size() != to.fields.size()) {
			throw new NonWideneable("Tuples have different fields count");
		}
		var widenedFields = new LinkedHashMap<String, GioviValue<?>>();
		int i = 0;
		for (var entry : to.fields.entrySet()) {
			widenedFields.put(
				entry.getKey(),
				this.getField(i).widenTo(entry.getValue().getGioviType()));
			i++;
		}
		return new Struct(to.getTypename(), widenedFields);
	}

}
