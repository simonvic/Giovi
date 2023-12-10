package it.simonvic.giovi.lang.values.natives;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.IncompatibleStructs;
import it.simonvic.giovi.lang.values.GioviValue;

public abstract sealed class Struct {

	public abstract void from(Struct struct);

	public abstract GioviValue<?> getField(int index);

	public abstract GioviValue<?> getField(String identifier);

	public abstract int getFieldsCount();

	public abstract List<String> getFieldNames();

	public abstract Struct copy();

	public abstract boolean isSubset(Struct struct);


	public static final class Named extends Struct {
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
		public boolean isSubset(Struct struct) {
			if (!(struct instanceof Struct.Named other)) return false;
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
			return Objects.equals(this.fields, other.fields);
		}

		@Override
		public GioviValue<?> getField(int index) {
			return fields.get(index);
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
		public void from(Struct struct) {
			switch (struct) {
				case Named s -> {
					fields.clear();
					s.fields.values().forEach(gioviValue -> this.put(gioviValue.copy()));
				}
				case Unnamed s -> s.fields.forEach(gioviValue -> this.put(gioviValue.copy()));
			}
		}

		@Override
		public boolean isSubset(Struct struct) {
			if (this.getFieldsCount() > struct.getFieldsCount()) return false;
			for (int i = 0; i < this.getFieldsCount(); i++) {
				var thisType = this.getField(i).getGioviType();
				var otherType = struct.getField(i).getGioviType();
				if (!thisType.equals(otherType) || !thisType.canUpcast(otherType)) {
					return false;
				}
			}
			return true;
		}


		public void put(GioviValue<?> value) {
			fields.add(value);
		}

	}


}
