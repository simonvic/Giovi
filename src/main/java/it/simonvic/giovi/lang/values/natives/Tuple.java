package it.simonvic.giovi.lang.values.natives;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.simonvic.giovi.lang.Range;
import it.simonvic.giovi.lang.values.GioviValue;

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

	/**
	 * Check if it's a subset of another tuple.
	 * @param tuple
	 * @return
	 */
	public boolean isSubset(Tuple tuple) {
		if (this.getFieldsCount() > tuple.getFieldsCount()) return false;
		for (int i = 0; i < this.getFieldsCount(); i++) {
			var thisType = this.getField(i).getGioviType();
			var otherType = tuple.getField(i).getGioviType();
			if (!thisType.equals(otherType) && !thisType.canUpcast(otherType)) {
				return false;
			}
		}
		return true;
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

}
