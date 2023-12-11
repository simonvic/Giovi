package it.simonvic.giovi.lang.values.natives;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.Range;
import it.simonvic.giovi.lang.values.GioviValue;

public abstract sealed class Tuple {

	public abstract GioviValue<?> getField(int index);

	public abstract boolean containsField(String identifier);

	public abstract GioviValue<?> getField(String identifier);

	public abstract int getFieldsCount();

	public abstract List<String> getFieldNames();

	public abstract Tuple copy();

	public abstract Tuple copy(Range<Integer> range);

	public abstract boolean isSubset(Tuple tuple);

	public abstract void forEach(Consumer<GioviValue<?>> valueConsumer);

	public abstract void forEach(BiConsumer<String, GioviValue<?>> valueConsumer);


	public static final class Named extends Tuple {

		private final LinkedHashMap<String, GioviValue<?>> fields;

		public Named() {
			this.fields = new LinkedHashMap<>();
		}

		public Named(LinkedHashMap<String, GioviValue<?>> fields) {
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
			int result = 1;
			result = prime * result + ((fields == null) ? 0 : fields.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Named other = (Named) obj;
			return Objects.equals(this.fields, other.fields);
		}

		@Override
		public GioviValue<?> getField(int index) {
			// TODO: lol
			return fields.get(fields.keySet().toArray()[index]);
		}

		@Override
		public boolean containsField(String identifier) {
			return fields.containsKey(identifier);
		}

		@Override
		public GioviValue<?> getField(String identifier) {
			return fields.get(identifier);
		}

		@Override
		public int getFieldsCount() {
			return fields.size();
		}

		@Override
		public List<String> getFieldNames() {
			var names = new ArrayList<String>();
			names.addAll(fields.keySet());
			return names;
		}

		@Override
		public Named copy() {
			var a = new LinkedHashMap<String, GioviValue<?>>();
			this.fields.forEach((identifier, value) -> a.put(identifier, value.copy()));
			return new Named(a);
		}

		@Override
		public Named copy(Range<Integer> range) {
			var copiedFields = new LinkedHashMap<String, GioviValue<?>>();
			this.fields.forEach((identifier, value) -> copiedFields.put(identifier, value.copy()));
			return new Named(copiedFields);
		}

		@Override
		public boolean isSubset(Tuple tuple) {
			if (!(tuple instanceof Tuple.Named other)) return false;
			if (this.getFieldsCount() > other.getFieldsCount()) return false;
			for (String fieldName : this.getFieldNames()) {
				var otherGioviType = other.getField(fieldName).getGioviType();
				var thisGioviType = this.getField(fieldName).getGioviType();
				if (!other.contains(fieldName)
					|| !thisGioviType.equals(otherGioviType)
					|| !thisGioviType.canUpcast(otherGioviType))
					return false;
			}
			return true;
		}

		@Override
		public void forEach(Consumer<GioviValue<?>> valueConsumer) {
			fields.forEach((__, value) -> valueConsumer.accept(value));
		}

		@Override
		public void forEach(BiConsumer<String, GioviValue<?>> valueConsumer) {
			fields.forEach(valueConsumer);
		}

		public void put(String identifier, GioviValue<?> value) {
			fields.put(identifier, value);
		}

		public void putAll(Named tuple) {
			tuple.fields.forEach(this::put);
		}

		public GioviValue<?> get(String identifier) {
			return fields.get(identifier);
		}

		public boolean contains(String field) {
			return fields.containsKey(field);
		}

	}

	public static final class Unnamed extends Tuple {

		private final List<GioviValue<?>> fields;

		public Unnamed() {
			fields = new ArrayList<>();
		}

		public Unnamed(List<GioviValue<?>> fields) {
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
			Unnamed other = (Unnamed) obj;
			if (fields == null) {
				if (other.fields != null) return false;
			} else if (!fields.equals(other.fields)) return false;
			return true;
		}

		@Override
		public GioviValue<?> getField(int index) {
			return fields.get(index);
		}

		@Override
		public boolean containsField(String identifier) {
			return false;
		}

		@Override
		public GioviValue<?> getField(String identifier) {
			throw new GioviException("Can't get field " + identifier);
		}

		@Override
		public int getFieldsCount() {
			return fields.size();
		}

		@Override
		public List<String> getFieldNames() {
			List<String> names = new ArrayList<>();
			for (int i = 0; i < fields.size(); i++) {
				names.add("" + i);
			}
			return names;
		}

		@Override
		public Unnamed copy() {
			var a = new ArrayList<GioviValue<?>>();
			this.fields.forEach(value -> a.add(value.copy()));
			return new Unnamed(a);
		}

		@Override
		public Unnamed copy(Range<Integer> range) {
			var list = new ArrayList<GioviValue<?>>();
			range.iterate(index -> list.add(getField(index).copy()));
			return new Unnamed(list);
		}

		@Override
		public boolean isSubset(Tuple tuple) {
			if (this.getFieldsCount() > tuple.getFieldsCount()) return false;
			for (int i = 0; i < this.getFieldsCount(); i++) {
				var thisType = this.getField(i).getGioviType();
				var otherType = tuple.getField(i).getGioviType();
				if (!thisType.equals(otherType) || !thisType.canUpcast(otherType)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void forEach(Consumer<GioviValue<?>> valueConsumer) {
			fields.forEach(valueConsumer);
		}

		@Override
		public void forEach(BiConsumer<String, GioviValue<?>> valueConsumer) {
			for (int i = 0; i < fields.size(); i++) {
				valueConsumer.accept("" + i, fields.get(i)); // TODO: meh?
			}
		}

		public void put(GioviValue<?> value) {
			fields.add(value);
		}

	}

}
