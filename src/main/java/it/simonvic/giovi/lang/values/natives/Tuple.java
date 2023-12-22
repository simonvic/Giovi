package it.simonvic.giovi.lang.values.natives;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.simonvic.giovi.lang.Range;
import it.simonvic.giovi.lang.values.GioviValue;

public sealed class Tuple permits Tuple.Named {

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

	public static sealed class Named extends Tuple permits Struct {

		private final LinkedHashMap<String, GioviValue<?>> fields;

		public Named() {
			this.fields = new LinkedHashMap<>();
		}

		public Named(LinkedHashMap<String, GioviValue<?>> fields) {
			super(new ArrayList<>(fields.values()));
			this.fields = fields;
		}

		@Override
		public String toString() {
			var i = fields.entrySet().iterator();
			if (!i.hasNext())
				return "<>";

			StringBuilder sb = new StringBuilder();
			sb.append('<');
			for (;;) {
				var e = i.next();
				var key = e.getKey();
				var value = e.getValue();
				sb.append(key);
				sb.append(':');
				sb.append(value);
				if (!i.hasNext())
					return sb.append('>').toString();
				sb.append(',').append(' ');
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((fields == null) ? 0 : fields.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!super.equals(obj)) return false;
			if (getClass() != obj.getClass()) return false;
			Named other = (Named) obj;
			if (fields == null) {
				if (other.fields != null) return false;
			} else if (!fields.equals(other.fields)) return false;
			return true;
		}

		@Override
		public Named copy() {
			var a = new LinkedHashMap<String, GioviValue<?>>();
			this.fields.forEach((identifier, value) -> a.put(identifier, value.copy()));
			return new Named(a);
		}

		@Override
		public boolean isSubset(Tuple tuple) {
			if (tuple instanceof Tuple.Named other) {
				if (this.getFieldsCount() > other.getFieldsCount()) return false;
				for (var fieldEntry : fields.entrySet()) {
					String fieldName = fieldEntry.getKey();
					var fieldValue = fieldEntry.getValue();
					var thisGioviType = fieldValue.getGioviType();
					if (!other.containsField(fieldName)) {
						return false;
					}
					var otherGioviType = other.getField(fieldName).getGioviType();
					if (!thisGioviType.equals(otherGioviType)
						&& !thisGioviType.canUpcast(otherGioviType))
						return false;

				}
				return true;
			}
			return super.isSubset(tuple);
		}

		public boolean containsField(String identifier) {
			return fields.containsKey(identifier);
		}

		public GioviValue<?> getField(String identifier) {
			return fields.get(identifier);
		}

		public List<String> getFieldNames() {
			var names = new ArrayList<String>();
			names.addAll(fields.keySet());
			return names;
		}

		public GioviValue<?> get(String identifier) {
			return fields.get(identifier);
		}

		public void forEach(BiConsumer<String, GioviValue<?>> valueConsumer) {
			fields.forEach(valueConsumer);
		}

	}

}
