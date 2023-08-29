package it.simonvic.giovi.lang.primitives;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import it.simonvic.giovi.exceptions.IncompatibleStructs;
import it.simonvic.giovi.lang.values.GioviValue;

public abstract sealed class Struct {

	public abstract void from(Struct struct);

	public abstract GioviValue<?> getField(int index);

	public abstract int getFieldsCount();

	public abstract Struct copy();

	public static final class Named extends Struct {
		private final LinkedHashMap<String, GioviValue<?>> fields;

		public Named() {
			this.fields = new LinkedHashMap<>();
		}

		public Named(LinkedHashMap<String, GioviValue<?>> fields) {
			this.fields = fields;
		}

		@Override
		public GioviValue<?> getField(int index) {
			// TODO lol
			return fields.get(fields.keySet().toArray()[index]);
		}

		@Override
		public int getFieldsCount() {
			return fields.size();
		}

		@Override
		public Struct copy() {
			var a = new LinkedHashMap<String, GioviValue<?>>();
			this.fields.forEach((identifier, value) -> a.put(identifier, value.copy()));
			return new Named(a);
		}

		public void put(String identifier, GioviValue<?> value) {
			fields.put(identifier, value);
		}

		public void putAll(Named struct) {
			struct.fields.forEach(this::put);
		}

		public GioviValue<?> get(String identifier) {
			return fields.get(identifier);
		}

		public boolean contains(String field) {
			return fields.containsKey(field);
		}

		@Override
		public void from(Struct struct) {
			switch (struct) {
				case Named s -> {
					s.fields.forEach((s1, gioviValue) -> {
						if (!this.contains(s1)) {
							throw new IncompatibleStructs("Incompatible structs. Struct " + this
								+ " doesn't contain a field " + s1);
						}
						fields.get(s1).box(gioviValue.copy());
					});
				}
				case Unnamed s -> {
					if (s.fields.size() != this.fields.size()) {
						throw new IncompatibleStructs("Incompatible structs. Can't assign " + s
							+ " to " + this + " because of wrong fields numbers");
					}
					var unnamedIterator = s.fields.iterator();
					this.fields.forEach((s1, gioviValue) -> gioviValue
						.box(GioviValue.of(unnamedIterator.next().unbox())));
				}
			}
		}

		@Override
		public String toString() {
			var i = fields.entrySet().iterator();
			if (!i.hasNext())
				return "()";

			StringBuilder sb = new StringBuilder();
			sb.append('(');
			for (;;) {
				var e = i.next();
				var key = e.getKey();
				var value = e.getValue();
				sb.append(key);
				sb.append(':');
				sb.append(value);
				if (!i.hasNext())
					return sb.append(')').toString();
				sb.append(',').append(' ');
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Named that = (Named) o;
			return fields.equals(that.fields);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), fields);
		}

	}

	public static final class Unnamed extends Struct {

		private final List<GioviValue<?>> fields;

		public Unnamed() {
			fields = new ArrayList<>();
		}

		public Unnamed(Struct struct) {
			fields = new ArrayList<>();
			from(struct);
		}

		public Unnamed(List<GioviValue<?>> fields) {
			this.fields = fields;
		}

		@Override
		public GioviValue<?> getField(int index) {
			return fields.get(index);
		}

		@Override
		public int getFieldsCount() {
			return fields.size();
		}

		@Override
		public Struct copy() {
			var a = new ArrayList<GioviValue<?>>();
			this.fields.forEach(value -> a.add(value.copy()));
			return new Unnamed(a);
		}

		@Override
		public void from(Struct struct) {
			switch (struct) {
				case Named s -> {
					fields.clear();
					s.fields.values().forEach(gioviValue -> this.put(gioviValue.copy()));
				}
				case Unnamed s -> s.fields.forEach(gioviValue -> this.put(gioviValue.copy()));
			}
		}

		public void put(GioviValue<?> value) {
			fields.add(value);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Unnamed unnamed = (Unnamed) o;
			return fields.equals(unnamed.fields);
		}

		@Override
		public int hashCode() {
			return Objects.hash(fields);
		}

		@Override
		public String toString() {
			var i = fields.iterator();
			if (!i.hasNext()) return "()";

			StringBuilder sb = new StringBuilder();
			sb.append('(');
			while (i.hasNext()) {
				sb.append(i.next());
				sb.append(i.hasNext() ? ", " : ")");
			}
			return sb.toString();
		}

	}

}
