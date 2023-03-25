package it.simonvic.giovi.lang.primitives;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import it.simonvic.giovi.exceptions.IncompatibleStructs;
import it.simonvic.giovi.lang.values.GioviValue;

public abstract sealed class Struct {

	public static Struct copyOf(Struct other) {
		return switch (other) {
			case Named s -> new Named(s);
			case Unnamed s -> new Unnamed(s);
		};
	}

	public abstract void set(Struct struct);

	public static final class Named extends Struct {
		private final LinkedHashMap<String, GioviValue<?>> attributes;

		public Named() {
			this.attributes = new LinkedHashMap<>();
		}

		public Named(Named struct) {
			this.attributes = new LinkedHashMap<>();
			putAll(struct);
		}

		public Named(LinkedHashMap<String, GioviValue<?>> attributes) {
			this.attributes = attributes;
		}

		public void put(String identifier, GioviValue<?> value) {
			attributes.put(identifier, value);
		}

		public void putAll(Named struct) {
			struct.attributes.forEach(this::put);
		}

		public GioviValue<?> get(String identifier) {
			return attributes.get(identifier);
		}

		public boolean contains(String attribute) {
			return attributes.containsKey(attribute);
		}

		@Override
		public void set(Struct struct) {
			switch (struct) {
				case Named s -> {
					if (s.attributes.size() != this.attributes.size()) {
						throw new IncompatibleStructs("Incompatible structs. %s has %s attributes, while %s requires %s".formatted(s, s.attributes.size(), this, this.attributes.size()));
					}
					s.attributes.forEach((s1, gioviValue) -> {
						if (!this.contains(s1)) {
							throw new IncompatibleStructs("Incompatible structs. Struct " + this + " doesn't contain an attribute " + s1);
						}
						attributes.put(s1, GioviValue.of(gioviValue.unbox()));
					});
				}
				case Unnamed s -> {
					if (s.attributes.size() != this.attributes.size()) {
						throw new IncompatibleStructs("Incompatible structs. Can't assign " + s + " to " + this + " because of wrong attributes numbers");
					}
					var unnamedIterator = s.attributes.iterator();
					this.attributes.forEach((s1, gioviValue) -> gioviValue.box(GioviValue.of(unnamedIterator.next().unbox())));
				}
			}
		}

		@Override
		public String toString() {
			var i = attributes.entrySet().iterator();
			if (!i.hasNext())
				return "()";

			StringBuilder sb = new StringBuilder();
			sb.append('(');
			for (; ; ) {
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
			return attributes.equals(that.attributes);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), attributes);
		}

	}

	public static final class Unnamed extends Struct {

		private final List<GioviValue<?>> attributes;

		public Unnamed() {
			attributes = new ArrayList<>();
		}

		public Unnamed(Struct struct) {
			attributes = new ArrayList<>();
			set(struct);
		}

		public Unnamed(List<GioviValue<?>> attributes) {
			this.attributes = attributes;
		}

		@Override
		public void set(Struct struct) {
			switch (struct) {
				case Named s -> s.attributes.forEach((s1, gioviValue) -> put(GioviValue.of(gioviValue.unbox())));
				case Unnamed s -> s.attributes.forEach(gioviValue -> this.put(GioviValue.of(gioviValue.unbox())));
			}
		}

		public void put(GioviValue<?> value) {
			attributes.add(value);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Unnamed unnamed = (Unnamed) o;
			return attributes.equals(unnamed.attributes);
		}

		@Override
		public int hashCode() {
			return Objects.hash(attributes);
		}

		@Override
		public String toString() {
			if (attributes.isEmpty()) return "()";

			StringBuilder sb = new StringBuilder();
			sb.append('(');
			for (var attribute : attributes) {
				sb.append(attribute);
				sb.append(" ");
			}
			sb.append(')');
			return sb.toString();
		}
	}

}
